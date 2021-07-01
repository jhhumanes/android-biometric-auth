package com.josehumaneshumanes.biometricauth.ui.auth

import android.util.Patterns
import androidx.biometric.BiometricPrompt.AuthenticationResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.josehumaneshumanes.biometricauth.domain.User
import com.josehumaneshumanes.biometricauth.framework.crypto.AndroidCrypto
import com.josehumaneshumanes.biometricauth.framework.crypto.PREFS_TOKEN_KEY

class BiometricAuthViewModel(private val androidCrypto: AndroidCrypto) : ViewModel() {

    sealed class UiModel {
        object BiometricAvailabilityCheck : UiModel()
        object Valid : UiModel()
        class Invalid(val usernameError: String?, val passwordError: String?) : UiModel()
        object ShowBiometricPromptForEncryption : UiModel()
        object ShowBiometricPromptForDecryption : UiModel()
        object Home : UiModel()
    }

    private val _uiModel = MutableLiveData<UiModel>()
    val uiModel: LiveData<UiModel>
        get() {
            if (_uiModel.value == null) _uiModel.value = UiModel.BiometricAvailabilityCheck
            return _uiModel
        }

    private val encryptedToken: String?
        get() = androidCrypto.getPreference(PREFS_TOKEN_KEY)

    fun onCreate() {
        encryptedToken?.let { showBiometricPromptForDecryption() }
    }

    fun onFormDataChanged(username: String, password: String) {
        if (!isUsernameValid(username)) {
            val previousPasswordError = when (_uiModel.value) {
                is UiModel.Invalid -> (_uiModel.value as UiModel.Invalid).passwordError
                else -> null
            }
            _uiModel.value = UiModel.Invalid(USERNAME_ERROR, previousPasswordError)
        } else if (!isPasswordValid(password)) {
            val previousUsernameError = when (_uiModel.value) {
                is UiModel.Invalid -> (_uiModel.value as UiModel.Invalid).usernameError
                else -> null
            }
            _uiModel.value = UiModel.Invalid(previousUsernameError, PASSWORD_ERROR)
        } else {
            _uiModel.value = UiModel.Valid
        }
    }

    fun onActivateClicked(username: String, password: String) {
        val usernameValid = isUsernameValid(username)
        val passwordValid = isPasswordValid(password)

        if (usernameValid && passwordValid) {
            doLogin(username, password)
            _uiModel.value = UiModel.ShowBiometricPromptForEncryption
        } else {
            _uiModel.value = UiModel.Invalid(
                if (usernameValid) null else USERNAME_ERROR,
                if (passwordValid) null else PASSWORD_ERROR
            )
        }
    }

    fun onBiometricEnable(authResult: AuthenticationResult) {
        User.token?.let {
            androidCrypto.putPreference(PREFS_TOKEN_KEY, it)
            _uiModel.value = UiModel.Home
        }
    }

    private fun doLogin(username: String, password: String) {
        val token = User.getToken(username, password)
        User.token = token
    }

    private fun showBiometricPromptForDecryption() {
        _uiModel.value = UiModel.ShowBiometricPromptForDecryption
    }

    fun onBiometricAuth(authResult: AuthenticationResult) {
        User.token = encryptedToken
        _uiModel.value = UiModel.Home
    }

}

class BiometricAuthViewModelFactory(private val androidCrypto: AndroidCrypto) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        BiometricAuthViewModel(androidCrypto) as T
}

private const val MIN_PASSWORD_LENGTH: Int = 8

private const val USERNAME_ERROR: String = "El email no es correcto"
private const val PASSWORD_ERROR: String =
    "La contraseña debe tener al menos $MIN_PASSWORD_LENGTH caracteres"

private fun isUsernameValid(username: String): Boolean =
    Patterns.EMAIL_ADDRESS.matcher(username).matches()

private fun isPasswordValid(password: String): Boolean = password.length >= MIN_PASSWORD_LENGTH
