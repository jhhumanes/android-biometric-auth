package com.josehumaneshumanes.biometricauth.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt.AuthenticationResult
import androidx.core.widget.doAfterTextChanged
import com.josehumaneshumanes.biometricauth.BiometricApplication
import com.josehumaneshumanes.biometricauth.R
import com.josehumaneshumanes.biometricauth.databinding.ActivityBiometricAuthBinding
import com.josehumaneshumanes.biometricauth.framework.biometric.AndroidBiometricPrompt
import com.josehumaneshumanes.biometricauth.framework.biometric.AndroidBiometricPrompt.Authenticator.STRONG
import com.josehumaneshumanes.biometricauth.ui.auth.BiometricAuthViewModel.UiModel.*
import com.josehumaneshumanes.biometricauth.ui.home.HomeActivity

class BiometricAuthActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityBiometricAuthBinding

    private val viewModel: BiometricAuthViewModel by viewModels {
        BiometricAuthViewModelFactory((application as BiometricApplication).androidCrypto)
    }

    private lateinit var androidBiometricPrompt: AndroidBiometricPrompt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        androidBiometricPrompt = AndroidBiometricPrompt(this)

        setupLayout()
        setupViewModelObservers()
        setupViewBinding()

        viewModel.onCreate()
    }

    private fun setupLayout() {
        viewBinding = ActivityBiometricAuthBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }

    private fun setupViewModelObservers() {
        viewModel.uiModel.observe(this, {
            when (it) {
                is BiometricAvailabilityCheck -> checkBiometricAvailability()
                is Home -> navigateToHome()
                is Valid -> enableActivateButton()
                is Invalid -> showFormErrors(it)
                is ShowBiometricPromptForEncryption -> showBiometricPrompt(viewModel::onBiometricEnable)
                is ShowBiometricPromptForDecryption -> showBiometricPrompt(viewModel::onBiometricAuth)
            }
        })
    }

    private fun setupViewBinding() {
        with(viewBinding) {
            activateButton.setOnClickListener {
                viewModel.onActivateClicked(username.text.toString(), password.text.toString())
            }

            username.doAfterTextChanged {
                viewModel.onFormDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            with(password) {
                doAfterTextChanged {
                    viewModel.onFormDataChanged(
                        username.text.toString(),
                        password.text.toString()
                    )
                }
                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE ->
                            viewModel.onActivateClicked(
                                username.text.toString(), password.text.toString()
                            )
                    }
                    false
                }
            }
        }
    }

    private fun checkBiometricAvailability() {
        val isAvailable = androidBiometricPrompt.canAuthenticate(STRONG)
        if (!isAvailable) {
            disableForm()
        }
    }

    private fun disableForm() {
        with(viewBinding) {
            title.text = getString(R.string.biometric_no_available)
            username.visibility = View.GONE
            password.visibility = View.GONE
            activateButton.visibility = View.GONE
        }
    }

    private fun enableActivateButton() {
        with(viewBinding) {
            activateButton.isEnabled = true
            username.error = null
            password.error = null
        }
    }

    private fun showFormErrors(uiModel: Invalid) {
        with(viewBinding) {
            activateButton.isEnabled = false
            username.error = uiModel.passwordError
            password.error = uiModel.usernameError
        }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun showBiometricPrompt(onBiometricResult: (AuthenticationResult) -> Unit) {
        val promptInfo = androidBiometricPrompt.getPromptInfo()
        val biometricPrompt = androidBiometricPrompt.getPrompt(onBiometricResult)

        biometricPrompt.authenticate(promptInfo)
    }

}
