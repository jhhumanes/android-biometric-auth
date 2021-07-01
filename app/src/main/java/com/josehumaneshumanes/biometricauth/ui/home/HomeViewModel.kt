package com.josehumaneshumanes.biometricauth.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.josehumaneshumanes.biometricauth.domain.User
import com.josehumaneshumanes.biometricauth.framework.crypto.AndroidCrypto
import com.josehumaneshumanes.biometricauth.framework.crypto.PREFS_TOKEN_KEY

class HomeViewModel(private val androidCrypto: AndroidCrypto) : ViewModel() {

    private val _logout = MutableLiveData<Boolean>()
    val logout: LiveData<Boolean>
        get() = _logout

    fun onLogoutClicked() {
        User.token = null
        _logout.value = true
    }

    fun onInvalidateClicked() {
        androidCrypto.removePreference(PREFS_TOKEN_KEY)
        User.token = null
        _logout.value = true
    }

}

class HomeViewModelFactory(private val androidCrypto: AndroidCrypto) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        HomeViewModel(androidCrypto) as T
}
