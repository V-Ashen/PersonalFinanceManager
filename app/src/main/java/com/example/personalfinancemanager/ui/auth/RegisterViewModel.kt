package com.example.personalfinancemanager.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.personalfinancemanager.data.AppDatabase
import com.example.personalfinancemanager.data.User
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()

    // LiveData to signal success or failure back to the Activity
    private val _registrationResult = MutableLiveData<RegistrationResult>()
    val registrationResult: LiveData<RegistrationResult> = _registrationResult

    fun registerUser(fullName: String, email: String, password: String, confirmPassword: String) {
        // --- Input Validation ---
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            _registrationResult.value = RegistrationResult.Error("All fields are required.")
            return
        }
        if (password != confirmPassword) {
            _registrationResult.value = RegistrationResult.Error("Passwords do not match.")
            return
        }
        if (password.length < 6) {
            _registrationResult.value = RegistrationResult.Error("Password must be at least 6 characters.")
            return
        }

        // --- Database Operation ---
        viewModelScope.launch {
            // Check if a user with this email already exists
            val existingUser = userDao.findUserByEmail(email)
            if (existingUser != null) {
                _registrationResult.postValue(RegistrationResult.Error("An account with this email already exists."))
            } else {
                // In a real app, you would generate a unique ID and HASH the password here.
                // For this project, we'll use a simple approach.
                val newUser = User(
                    userId = (System.currentTimeMillis() % 10000).toInt(), // Simple ID generation
                    username = fullName,
                    email = email,
                    passwordHash = password // Storing password directly (NOT FOR PRODUCTION)
                )
                userDao.insertUser(newUser)
                _registrationResult.postValue(RegistrationResult.Success("Registration successful!"))
            }
        }
    }
}

// A helper sealed class to represent the different outcomes of registration
sealed class RegistrationResult {
    data class Success(val message: String) : RegistrationResult()
    data class Error(val message: String) : RegistrationResult()
}