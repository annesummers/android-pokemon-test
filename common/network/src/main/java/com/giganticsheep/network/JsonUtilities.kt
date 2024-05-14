package com.giganticsheep.network

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface JsonUtilities {

    @OptIn(ExperimentalSerializationApi::class)
    val json: Json
        get() = Json {
            explicitNulls = true
            ignoreUnknownKeys = true
            serializersModule = SerializersModule { }
        }

    fun objToJson(obj: Any, type: KType): String
    fun jsonToObj(jsonString: String, type: KType): Any?
    fun isJsonObject(jsonString: String): Boolean

    companion object {
        fun get() = InternalJsonUtilities
    }
}

inline fun <reified T : Any> JsonUtilities.objToJson(
    obj: T,
): String = objToJson(obj, typeOf<T>())

inline fun <reified T : Any> JsonUtilities.jsonToObj(
    jsonString: String,
): T = jsonToObj(jsonString, typeOf<T>()) as T

object InternalJsonUtilities : JsonUtilities {

    override fun objToJson(
        obj: Any,
        type: KType,
    ) = json.encodeToString(
        serializer = json.serializersModule.serializer(type),
        value = obj,
    )

    override fun jsonToObj(
        jsonString: String,
        type: KType,
    ) = json.decodeFromString(
        deserializer = json.serializersModule.serializer(type),
        string = jsonString,
    )

    override fun isJsonObject(
        jsonString: String,
    ): Boolean = try {
        json.parseToJsonElement(jsonString)

        true
    } catch (e: Exception) {
        false
    }
}
