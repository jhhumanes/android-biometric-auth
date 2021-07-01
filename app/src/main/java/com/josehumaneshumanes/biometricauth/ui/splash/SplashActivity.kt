package com.josehumaneshumanes.biometricauth.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.josehumaneshumanes.biometricauth.ui.auth.BiometricAuthActivity
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SplashActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initScope()

        initApp()
    }

    private fun initApp() {
        launch {
            delay(3000)
            goToAuth()
        }
    }

    private fun goToAuth() {
        startActivity(Intent(this, BiometricAuthActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private fun initScope() {
        job = SupervisorJob()
    }

}
