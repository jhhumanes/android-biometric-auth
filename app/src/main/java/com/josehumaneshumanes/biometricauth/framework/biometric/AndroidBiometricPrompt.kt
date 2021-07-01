package com.josehumaneshumanes.biometricauth.framework.biometric

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.josehumaneshumanes.biometricauth.R
import com.josehumaneshumanes.biometricauth.framework.biometric.AndroidBiometricPrompt.Authenticator

class AndroidBiometricPrompt(private val context: AppCompatActivity) {

    enum class Authenticator { STRONG }

    fun canAuthenticate(authenticator: Authenticator): Boolean {
        val canAuthenticate = BiometricManager.from(context)
            .canAuthenticate(authenticator.toAndroidId())
        return canAuthenticate == BIOMETRIC_SUCCESS
    }

    fun getPromptInfo(): PromptInfo = PromptInfo.Builder()
        .setTitle(context.getString(R.string.prompt_info_title))
        .setSubtitle(context.getString(R.string.prompt_info_subtitle))
        .setNegativeButtonText(context.getString(R.string.prompt_info_cancel))
        .setConfirmationRequired(false)
        .build()

    fun getPrompt(onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(context)

        return BiometricPrompt(context, executor, object : AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess(result)
            }
        })
    }

}

private fun Authenticator.toAndroidId() = when (this) {
    Authenticator.STRONG -> BiometricManager.Authenticators.BIOMETRIC_STRONG
}
