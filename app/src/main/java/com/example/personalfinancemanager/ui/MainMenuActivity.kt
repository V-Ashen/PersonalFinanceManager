package com.example.personalfinancemanager.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.personalfinancemanager.ExpensesActivity
import com.example.personalfinancemanager.R
import com.example.personalfinancemanager.ui.auth.LoginActivity

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val expensesButton = findViewById<Button>(R.id.buttonExpenses)
        val addBudgetButton = findViewById<Button>(R.id.buttonAddBudget)
        val addSavingsGoalButton = findViewById<Button>(R.id.buttonAddSavingsGoal)
        val logoutButton = findViewById<Button>(R.id.buttonLogout)

        expensesButton.setOnClickListener {
            startActivity(Intent(this, ExpensesActivity::class.java))
        }

        addBudgetButton.setOnClickListener {
            startActivity(Intent(this, com.example.personalfinancemanager.ui.budget.AddBudgetActivity::class.java))
        }

        addSavingsGoalButton.setOnClickListener {
            startActivity(Intent(this, com.example.personalfinancemanager.ui.savings.AddSavingsGoalActivity::class.java))
        }

        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            // Clear the activity stack so the user can't go back
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}