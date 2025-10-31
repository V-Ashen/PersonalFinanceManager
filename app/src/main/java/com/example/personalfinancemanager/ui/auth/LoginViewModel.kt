package com.example.personalfinancemanager.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.personalfinancemanager.data.AppDatabase
import com.example.personalfinancemanager.data.User
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()

    // This helps the Activity know when login is successful
    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    // For simplicity, we are creating a default user if none exists.
    // In a real app, registration would be required.
    init {
        viewModelScope.launch {
            val user = userDao.getUserById(1)
            if (user == null) {
                userDao.insertUser(User(userId = 1, username = "Test User", email = "test@email.com", passwordHash = "123456"))
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = userDao.findUserByEmail(email)
            if (user != null && user.passwordHash == password) {
                // Successful login
                _loginSuccess.postValue(true)
            } else {
                // Failed login
                _loginSuccess.postValue(false)
            }
        }
    }
}