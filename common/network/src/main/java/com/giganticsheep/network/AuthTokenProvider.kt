package com.giganticsheep.network

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface AuthTokenProvider {
    val authToken: Flow<AuthToken?>
}

@Serializable
data class AuthToken(
    @SerialName("token")
    val token: String,
)
