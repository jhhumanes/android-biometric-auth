package com.josehumaneshumanes.biometricauth.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.josehumaneshumanes.biometricauth.BiometricApplication
import com.josehumaneshumanes.biometricauth.databinding.ActivityHomeBinding
import com.josehumaneshumanes.biometricauth.domain.User
import com.josehumaneshumanes.biometricauth.ui.auth.BiometricAuthActivity

class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((application as BiometricApplication).androidCrypto)
    }

    private lateinit var viewBinding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupLayout()
        setupViewModelObservers()
        setupViewBinding()
    }

    private fun setupViewBinding() {
        with(viewBinding) {
            "Token: ${User.token}".also { tokenText.text = it }
            logoutButton.setOnClickListener {
                viewModel.onLogoutClicked()
            }

            invalidateButton.setOnClickListener {
                viewModel.onInvalidateClicked()
            }
        }
    }

    private fun setupViewModelObservers() {
        viewModel.logout.observe(this, { logout ->
            if (logout) {
                startActivity(Intent(this, BiometricAuthActivity::class.java))
                finish()
            }
        })
    }

    private fun setupLayout() {
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }
}
