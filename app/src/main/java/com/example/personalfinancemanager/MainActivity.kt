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
import com.example.personalfinancemanager.network.RetrofitInstance // <-- NEW IMPORT
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
    private lateinit var buttonSync: Button // <-- ADDED

    private var categoryList: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        editTextAmount = findViewById(R.id.editTextAmount)
        editTextDescription = findViewById(R.id.editTextDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        buttonSaveExpense = findViewById(R.id.buttonSaveExpense)
        buttonSync = findViewById(R.id.buttonSync) // <-- ADDED

        // Load data from database
        setupInitialData()

        // Set up the save button click listener
        buttonSaveExpense.setOnClickListener {
            saveExpense()
        }

        // --- NEW ---
        // Set up the sync button click listener
        buttonSync.setOnClickListener {
            syncData()
        }
        // --- END NEW ---
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

        // Create the Expense object. It will default to 'unsynced'.
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
                Toast.makeText(this@MainActivity, "Expense Saved Locally!", Toast.LENGTH_SHORT).show()
                // Clear the form for the next entry
                editTextAmount.text.clear()
                editTextDescription.text.clear()
            }
        }
    }

    // =========================================================================
    // --- NEW SYNC FUNCTION ---
    // =========================================================================
    private fun syncData() {
        // Show a message to the user that sync is starting
        Toast.makeText(this, "Syncing data...", Toast.LENGTH_SHORT).show()

        // Run all networking and DB operations in the background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Get all unsynced expenses from the local Room database
                val unsyncedExpenses = db.expenseDao().getUnsyncedExpenses()

                // If there's nothing to sync, show a message and stop.
                if (unsyncedExpenses.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "No new data to sync.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // 2. Use Retrofit to send the data to our API
                val response = RetrofitInstance.api.syncExpenses(unsyncedExpenses)

                // 3. Handle the response from the server on the Main thread
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        // If sync was successful, update the local database
                        val idsToUpdate = unsyncedExpenses.map { it.expenseId }
                        // We launch another background coroutine for this DB operation
                        CoroutineScope(Dispatchers.IO).launch {
                            db.expenseDao().updateSyncStatus(idsToUpdate)
                        }
                        Toast.makeText(this@MainActivity, "Sync successful!", Toast.LENGTH_LONG).show()
                    } else {
                        // If the server returned an error (e.g., 400, 500)
                        val errorMessage = response.errorBody()?.string() ?: "Unknown sync error"
                        Toast.makeText(this@MainActivity, "Sync failed: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                // If there was a network error (e.g., no internet, server is down)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Sync failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}