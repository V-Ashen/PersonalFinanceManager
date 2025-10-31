package com.example.personalfinancemanager.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.example.personalfinancemanager.R

class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Find all the UI elements
        val fullNameEditText = findViewById<EditText>(R.id.editTextFullName)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.editTextConfirmPassword)
        val registerButton = findViewById<Button>(R.id.buttonRegister)
        val loginTextView = findViewById<TextView>(R.id.textViewLogin)

        // Set the action for the register button
        registerButton.setOnClickListener {
            viewModel.registerUser(
                fullName = fullNameEditText.text.toString().trim(),
                email = emailEditText.text.toString().trim(),
                password = passwordEditText.text.toString(),
                confirmPassword = confirmPasswordEditText.text.toString()
            )
        }

        // Set the action for the "Login" text to go back to the LoginActivity
        loginTextView.setOnClickListener {
            // Finish this activity to go back to the previous one (LoginActivity)
            finish()
        }

        // Observe the result from the ViewModel
        viewModel.registrationResult.observe(this) { result ->
            when (result) {
                is RegistrationResult.Success -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                    // On success, go back to the Login screen for the user to log in
                    finish()
                }
                is RegistrationResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}