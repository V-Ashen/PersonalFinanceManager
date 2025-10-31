package com.example.personalfinancemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancemanager.data.Category
import com.example.personalfinancemanager.ui.ExpenseAdapter
import com.example.personalfinancemanager.ui.MainViewModel

class ExpensesActivity : AppCompatActivity() {

    // --- ViewModel ---
    private val viewModel: MainViewModel by viewModels()

    // --- UI References ---
    private lateinit var editTextAmount: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var buttonSaveExpense: Button
    private lateinit var buttonSync: Button
    private lateinit var recyclerViewExpenses: RecyclerView
    private lateinit var progressBar: ProgressBar

    private lateinit var expenseAdapter: ExpenseAdapter
    private var categoryList: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUI()
        setupObservers()
        setupListeners()
    }

    private fun setupUI() {
        // Initialize UI components
        editTextAmount = findViewById(R.id.editTextAmount)
        editTextDescription = findViewById(R.id.editTextDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        buttonSaveExpense = findViewById(R.id.buttonSaveExpense)
        buttonSync = findViewById(R.id.buttonSync)
        recyclerViewExpenses = findViewById(R.id.recyclerViewExpenses)
        progressBar = findViewById(R.id.progressBar)

        // Setup RecyclerView
        expenseAdapter = ExpenseAdapter(emptyList())
        recyclerViewExpenses.adapter = expenseAdapter
        recyclerViewExpenses.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        buttonSaveExpense.setOnClickListener {
            val selectedCategory = if (spinnerCategory.selectedItemPosition >= 0 && categoryList.isNotEmpty()) {
                categoryList[spinnerCategory.selectedItemPosition]
            } else {
                null
            }
            viewModel.saveExpense(
                editTextAmount.text.toString(),
                editTextDescription.text.toString(),
                selectedCategory
            )
        }

        buttonSync.setOnClickListener {
            viewModel.syncData()
        }
    }

    private fun setupObservers() {
        // Observe categories for the spinner
        viewModel.categories.observe(this) { categories ->
            this.categoryList = categories
            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }

        // Observe expenses for the RecyclerView
        viewModel.expenses.observe(this) { expenses ->
            expenseAdapter.updateData(expenses)
            // After saving, clear the form
            editTextAmount.text.clear()
            editTextDescription.text.clear()
        }

        // Observe toast messages
        viewModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            buttonSync.isEnabled = !isLoading
            buttonSaveExpense.isEnabled = !isLoading
        }

        // Observe the sync button's enabled state
        viewModel.isSyncEnabled.observe(this) { isEnabled ->
            if (isEnabled) {
                // If there is data to sync, make the button fully visible and clickable
                buttonSync.isEnabled = true
                buttonSync.alpha = 1.0f // 1.0f means fully opaque
            } else {
                // If there is no data to sync, make it half-transparent and not clickable
                buttonSync.isEnabled = false
                buttonSync.alpha = 0.5f // 0.5f means 50% transparent
            }
        }
    }
}