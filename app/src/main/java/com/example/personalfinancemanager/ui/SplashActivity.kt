package com.example.personalfinancemanager.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.personalfinancemanager.ui.auth.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start the LoginActivity
        startActivity(Intent(this, LoginActivity::class.java))

        // Close the SplashActivity so the user can't navigate back to it
        finish()
    }
}