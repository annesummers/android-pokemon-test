package com.giganticsheep.network

import com.giganticsheep.error.HandledException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class HttpResponse(
    private val universalErrorTransformers: ErrorTransformerBuilder.() -> Unit,
    private val coreErrorTransformer: ErrorTransformer,
    val status: HttpStatusCode,
    val body: suspend () -> String,
) {

    constructor(
        universalErrorTransformers: ErrorTransformerBuilder.() -> Unit,
        coreErrorTransformer: ErrorTransformer,
        httpResponse: io.ktor.client.statement.HttpResponse,
    ) : this(
        universalErrorTransformers,
        coreErrorTransformer,
        httpResponse.status,
        { httpResponse.bodyAsText() },
    )

    suspend fun handleErrors(
        jsonUtilities: JsonUtilities,
        errorTransformers: ErrorTransformerBuilder.() -> Unit,
    ): HandledException = handleError(
        jsonUtilities,
        ErrorTransformerBuilder()
            .also(universalErrorTransformers)
            .also(errorTransformers)
            .transformers
            .toList(),
    )

    private suspend fun handleError(
        jsonUtilities: JsonUtilities,
        errorTransformers: List<ErrorTransformer>,
    ): HandledException = StringErrorWrapper(
        code = status,
        error = body(),
    )
        .let { errorWrapper ->
            errorTransformers
                .firstNotNullOfOrNull { it.transform(errorWrapper, jsonUtilities) }
                ?: coreErrorTransformer.transform(errorWrapper, jsonUtilities)!!
        }
}

class ErrorTransformerBuilder {

    val transformers: MutableList<ErrorTransformer> = mutableListOf()

    inline fun <reified E : Any> transform(
        code: HttpStatusCode,
        crossinline transformer: (E) -> HandledException?,
    ) = transformers.add(ErrorCodeTransformer.create(code, transformer))
        .let { this }

    inline fun <reified E : Any> transform(
        crossinline transformer: (E) -> HandledException?,
    ) = transformers.add(ErrorTypeTransformer.create(transformer))
        .let { this }

    fun transform(
        code: HttpStatusCode,
        transformer: () -> HandledException?,
    ) = transformers.add(ErrorCodeTransformer.create(code, transformer))
        .let { this }
}

interface ErrorTransformer {
    fun transform(
        errorWrapper: ErrorWrapper<String>,
        jsonUtilities: JsonUtilities,
    ): HandledException?
}

interface ErrorCodeTransformer<E : Any> : ErrorTransformer {

    companion object {

        inline fun <reified E : Any> create(
            code: HttpStatusCode,
            crossinline transformer: (E) -> HandledException?,
        ) = InternalErrorCodeTransformer(
            expectedCode = code,
            transformer = ErrorTypeTransformer.create(
                transformer = transformer,
            ),
        )

        fun create(
            code: HttpStatusCode,
            transformer: () -> HandledException?,
        ) = InternalErrorCodeTransformer(
            expectedCode = code,
            transformer = ErrorTypeTransformer.create(transformer),
        )
    }
}

class InternalErrorCodeTransformer<E : Any>(
    val expectedCode: HttpStatusCode,
    val transformer: ErrorTypeTransformer<E>,
) : ErrorCodeTransformer<E> {

    override fun transform(
        errorWrapper: ErrorWrapper<String>,
        jsonUtilities: JsonUtilities,
    ): HandledException? =
        if (expectedCode == errorWrapper.code) {
            transformer.transform(errorWrapper, jsonUtilities)
        } else {
            null
        }
}

inline fun <reified E : Any> errorTransform(
    crossinline transformer: (HttpStatusCode, E) -> HandledException?,
) = ErrorTypeTransformer.create<E>(transformer)

interface ErrorTypeTransformer<E : Any> : ErrorTransformer {

    companion object {
        inline fun <reified E : Any> create(
            crossinline transformer: (E) -> HandledException?,
        ): ErrorTypeTransformer<E> = ErrorObjectTypeTransformer(
            type = typeOf<E>(),
        ) { transformer(it.error) }

        inline fun <reified E : Any> create(
            crossinline transformer: (HttpStatusCode, E) -> HandledException?,
        ): ErrorTypeTransformer<E> = ErrorObjectTypeTransformer(
            type = typeOf<E>(),
        ) { transformer(it.code, it.error) }

        fun create(
            transformer: () -> HandledException?,
        ): ErrorTypeTransformer<Any> = ErrorAnyTypeTransformer(transformer)
    }
}

internal class ErrorAnyTypeTransformer(
    private val errorTransformer: () -> HandledException?,
) : ErrorTypeTransformer<Any> {

    override fun transform(
        errorWrapper: ErrorWrapper<String>,
        jsonUtilities: JsonUtilities,
    ) = errorTransformer()
}

@Suppress("UNCHECKED_CAST")
class ErrorObjectTypeTransformer<E : Any>(
    val type: KType,
    private val errorTransformer: (ErrorWrapper<E>) -> HandledException?,
) : ErrorTypeTransformer<E> {

    override fun transform(
        errorWrapper: ErrorWrapper<String>,
        jsonUtilities: JsonUtilities,
    ): HandledException? = try {
        when (type) {
            typeOf<String>() -> errorWrapper // no need to deserialise
            else ->
                if (jsonUtilities.isJsonObject(errorWrapper.error)) {
                    // if we can't deserialise to this type then we can catch the exception
                    ErrorWrapper(
                        code = errorWrapper.code,
                        error = jsonUtilities.jsonToObj(
                            jsonString = errorWrapper.error,
                            type = type,
                        ) as E,
                        type = type,
                    )
                } else {
                    null
                }
        }
            ?.let { errorTransformer(it as ErrorWrapper<E>) }
    } catch (e: Exception) {
        null
    }
}

open class ErrorWrapper<E : Any>(
    open val code: HttpStatusCode,
    open val error: E,
    val type: KType,
) {
    companion object {

        inline fun <reified E : Any> create(
            code: HttpStatusCode,
            error: E,
        ) = ErrorWrapper(
            code = code,
            error = error,
            type = typeOf<E>(),
        )
    }
}

internal data class StringErrorWrapper(
    override val code: HttpStatusCode,
    override val error: String,
) : ErrorWrapper<String>(
    code,
    error,
    typeOf<String>(),
)
