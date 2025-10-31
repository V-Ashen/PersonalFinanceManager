package com.example.personalfinancemanager.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.personalfinancemanager.data.AppDatabase
import com.example.personalfinancemanager.data.Category
import com.example.personalfinancemanager.data.Expense
import com.example.personalfinancemanager.data.ExpenseWithCategory
import com.example.personalfinancemanager.data.User
import com.example.personalfinancemanager.network.RetrofitInstance
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val currentUserId = 1 // Hardcoded for now

    // --- LiveData to communicate with the UI ---
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _isSyncEnabled = MutableLiveData<Boolean>()
    val isSyncEnabled: LiveData<Boolean> = _isSyncEnabled

    private val _expenses = MutableLiveData<List<ExpenseWithCategory>>()
    val expenses: LiveData<List<ExpenseWithCategory>> = _expenses

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        // Load initial data when the ViewModel is created
        viewModelScope.launch {
            setupInitialData()
            loadExpenses()
            loadCategories()
        }
    }

    private suspend fun setupInitialData() {
        val user = db.userDao().getUserById(currentUserId)
        if (user == null) {
            db.userDao().insertUser(User(userId = currentUserId, username = "testuser", passwordHash = "12345" ,email = "test@email.com"))
        }
        val existingCategories = db.categoryDao().getCategoriesForUser(currentUserId)
        if (existingCategories.isEmpty()) {
            db.categoryDao().insertCategory(Category(userId = currentUserId, name = "Groceries"))
            db.categoryDao().insertCategory(Category(userId = currentUserId, name = "Transport"))
            db.categoryDao().insertCategory(Category(userId = currentUserId, name = "Bills"))
            db.categoryDao().insertCategory(Category(userId = currentUserId, name = "Other"))
        }
    }

    private suspend fun loadCategories() {
        _categories.postValue(db.categoryDao().getCategoriesForUser(currentUserId))
    }

    private suspend fun loadExpenses() {
        val allExpenses = db.expenseDao().getAllExpensesWithCategory(currentUserId)
        _expenses.postValue(allExpenses)
        // Check if there are unsynced expenses
        val hasUnsyncedData = allExpenses.any { it.expense.syncStatus != "synced" }
        _isSyncEnabled.postValue(hasUnsyncedData)
    }

    fun saveExpense(amountStr: String, description: String, selectedCategory: Category?) {
        // Input Validation
        if (amountStr.isBlank()) {
            _toastMessage.value = "Amount cannot be empty"
            return
        }
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _toastMessage.value = "Please enter a valid amount"
            return
        }
        if (selectedCategory == null) {
            _toastMessage.value = "Please select a category"
            return
        }

        val newExpense = Expense(
            userId = currentUserId,
            categoryId = selectedCategory.categoryId,
            amount = amount,
            expenseDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
            description = description
        )

        viewModelScope.launch {
            db.expenseDao().insertExpense(newExpense)
            loadExpenses() // Refresh the expense list
            _toastMessage.postValue("Expense Saved!")
        }
    }

    fun syncData() {
        viewModelScope.launch {
            _isLoading.postValue(true) // Show loading indicator
            try {
                val unsyncedExpenses = db.expenseDao().getUnsyncedExpenses()
                if (unsyncedExpenses.isEmpty()) {
                    _toastMessage.postValue("No new data to sync.")
                    _isLoading.postValue(false)
                    return@launch
                }

                val response = RetrofitInstance.api.syncExpenses(unsyncedExpenses)
                if (response.isSuccessful && response.body()?.status == "success") {
                    val idsToUpdate = unsyncedExpenses.map { it.expenseId }
                    db.expenseDao().updateSyncStatus(idsToUpdate)
                    _toastMessage.postValue("Sync successful!")
                    _isSyncEnabled.postValue(false) // <-- Disable sync button after successful sync
                } else {
                    _toastMessage.postValue("Sync failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _toastMessage.postValue("Sync failed: ${e.message}")
            } finally {
                _isLoading.postValue(false) // Hide loading indicator
            }
        }
    }
}