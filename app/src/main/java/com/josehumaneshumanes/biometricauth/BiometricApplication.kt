package com.josehumaneshumanes.biometricauth

import android.app.Application
import com.josehumaneshumanes.biometricauth.framework.crypto.AndroidCrypto

class BiometricApplication : Application() {

    lateinit var androidCrypto: AndroidCrypto

    override fun onCreate() {
        super.onCreate()

        androidCrypto = AndroidCrypto(this)
    }
}