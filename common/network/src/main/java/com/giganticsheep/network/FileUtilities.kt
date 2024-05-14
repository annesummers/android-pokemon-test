package com.giganticsheep.network

import android.content.Context
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.reflect.KType

interface FileUtilities {
    fun stringFromFile(filename: String): String

    companion object {
        fun create(context: Context): FileUtilities = InternalFileUtilities(context)
    }
}

internal class InternalFileUtilities(
    private val context: Context,
) : FileUtilities {

    override fun stringFromFile(
        filename: String,
    ) = if (filename.isNotEmpty()) {
        streamFromFile(filename).readString()
    } else {
        throw RuntimeException()
    }

    private fun InputStream.readString(): String {
        var bufferedInputStream: BufferedInputStream? = null
        val byteArrayOutputStream: ByteArrayOutputStream
        return try {
            bufferedInputStream = BufferedInputStream(this)
            byteArrayOutputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var available: Int
            while (bufferedInputStream.read(buffer).also { available = it } >= 0) {
                byteArrayOutputStream.write(buffer, 0, available)
            }
            byteArrayOutputStream.toString()
        } finally {
            bufferedInputStream?.close()
        }
    }

    private fun InternalFileUtilities.streamFromFile(
        filename: String,
    ) = context.assets.open(filename)

    private fun InternalFileUtilities.bufferedReaderFromFile(
        filename: String,
    ) = BufferedReader(InputStreamReader(context.assets.open(filename)))
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> FileUtilities.objectFromFile(
    filename: String,
    type: KType,
    jsonUtilities: JsonUtilities = JsonUtilities.get(),
): T = stringFromFile(filename)
    .let { jsonUtilities.jsonToObj(it, type) as T }

inline fun <reified T : Any> FileUtilities.objectFromFile(
    filename: String,
    jsonUtilities: JsonUtilities = JsonUtilities.get(),
): T = stringFromFile(filename)
    .let { jsonUtilities.jsonToObj(it) as T }
