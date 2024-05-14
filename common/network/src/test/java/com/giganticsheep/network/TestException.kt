package com.giganticsheep.network

import com.giganticsheep.error.HandledException

class TestException(
    message: String = "",
) : HandledException(message = message, internalMessage = "test")
