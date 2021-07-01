package com.josehumaneshumanes.biometricauth.domain

import java.util.*

object User {
    var token: String? = null

    fun getToken(username: String, password: String): String = UUID.randomUUID().toString()
}
