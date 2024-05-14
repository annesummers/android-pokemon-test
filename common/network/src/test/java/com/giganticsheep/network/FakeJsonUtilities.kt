package com.giganticsheep.network

import kotlinx.serialization.SerializationException
import kotlin.reflect.KType
import kotlin.reflect.typeOf

open class FakeJsonUtilities : JsonUtilities {

    object TestClass
    object TestClass2

    val jsonString = "jsonString"
    val jsonString2 = "jsonString2"

    val jsonObject = TestClass
    val jsonObject2 = TestClass2

    override fun objToJson(
        obj: Any,
        type: KType,
    ) = when {
        (obj == jsonObject && type == typeOf<TestClass>()) -> jsonString
        (obj == jsonObject2 && type == typeOf<TestClass2>()) -> jsonString2
        else -> throw SerializationException()
    }

    override fun jsonToObj(
        jsonString: String,
        type: KType,
    ): Any = when {
        (jsonString == this.jsonString && type == typeOf<TestClass>()) -> jsonObject
        (jsonString == this.jsonString2 && type == typeOf<TestClass2>()) -> jsonObject2
        else -> throw SerializationException()
    }

    override fun isJsonObject(jsonString: String) = true
}
