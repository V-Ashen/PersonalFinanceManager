package com.example.personalfinancemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.personalfinancemanager.data.AppDatabase
import com.example.personalfinancemanager.data.Category
import com.example.personalfinancemanager.data.Expense
import com.example.personalfinancemanager.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // --- Database Reference ---
    private val db by lazy { AppDatabase.getDatabase(this) }
    private var currentUserId = 1 // Hardcoding user ID for now

    // --- UI References ---
    private lateinit var editTextAmount: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var buttonSaveExpense: Button

    private var categoryList: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        editTextAmount = findViewById(R.id.editTextAmount)
        editTextDescription = findViewById(R.id.editTextDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        buttonSaveExpense = findViewById(R.id.buttonSaveExpense)

        // Load data from database
        setupInitialData()

        // Set up the save button click listener
        buttonSaveExpense.setOnClickListener {
            saveExpense()
        }
    }

    private fun setupInitialData() {
        // Run database operations in the background
        CoroutineScope(Dispatchers.IO).launch {
            // Check if our test user exists, if not, create it.
            val user = db.userDao().getUserById(currentUserId)
            if (user == null) {
                db.userDao().insertUser(User(userId = currentUserId, username = "testuser", email = "test@email.com"))
            }

            // Add default categories if none exist for this user
            val categories = db.categoryDao().getCategoriesForUser(currentUserId)
            if (categories.isEmpty()) {
                db.categoryDao().insertCategory(Category(userId = currentUserId, name = "Groceries"))
                db.categoryDao().insertCategory(Category(userId = currentUserId, name = "Transport"))
                db.categoryDao().insertCategory(Category(userId = currentUserId, name = "Bills"))
            }

            // Load categories into the spinner on the main thread
            withContext(Dispatchers.Main) {
                loadCategoriesIntoSpinner()
            }
        }
    }

    private fun loadCategoriesIntoSpinner() {
        CoroutineScope(Dispatchers.IO).launch {
            // Fetch the categories from the database
            categoryList = db.categoryDao().getCategoriesForUser(currentUserId)
            val categoryNames = categoryList.map { it.name }

            // Update the spinner on the UI thread
            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, categoryNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCategory.adapter = adapter
            }
        }
    }

    private fun saveExpense() {
        val amountStr = editTextAmount.text.toString()
        val description = editTextDescription.text.toString()
        val selectedCategoryPosition = spinnerCategory.selectedItemPosition

        // Basic validation
        if (amountStr.isBlank()) {
            Toast.makeText(this, "Amount cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedCategoryPosition < 0 || categoryList.isEmpty()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDouble()
        val selectedCategory = categoryList[selectedCategoryPosition]

        // Get current date as a "YYYY-MM-DD" string
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.format(Date())

        // Create the Expense object
        val newExpense = Expense(
            userId = currentUserId,
            categoryId = selectedCategory.categoryId,
            amount = amount,
            expenseDate = currentDate,
            description = description
        )

        // Save to database in the background
        CoroutineScope(Dispatchers.IO).launch {
            db.expenseDao().insertExpense(newExpense)
            // Show a confirmation message on the UI thread
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Expense Saved!", Toast.LENGTH_SHORT).show()
                // Clear the form for the next entry
                editTextAmount.text.clear()
                editTextDescription.text.clear()
            }
        }
    }

    // You need to add this function to your UserDao interface
    // Go to UserDao.kt and add:
    // @Query("SELECT * FROM users WHERE userId = :id")
    // suspend fun getUserById(id: Int): User?
}