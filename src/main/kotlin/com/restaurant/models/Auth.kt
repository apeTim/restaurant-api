package com.restaurant.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterCredentials (
    val username: String,
    val password: String,
    val isAdmin: Boolean
)
