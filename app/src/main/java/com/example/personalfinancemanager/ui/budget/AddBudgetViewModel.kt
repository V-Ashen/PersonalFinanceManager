package com.example.personalfinancemanager.ui.budget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.personalfinancemanager.data.AppDatabase
import com.example.personalfinancemanager.data.Budget
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddBudgetViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val currentUserId = 1

    private val _budgets = MutableLiveData<List<Budget>>()
    val budgets: LiveData<List<Budget>> = _budgets

    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult

    init {
        loadBudgets()
    }

    private fun loadBudgets() {
        viewModelScope.launch {
            _budgets.postValue(db.budgetDao().getBudgetsForUser(currentUserId))
        }
    }

    fun saveBudget(amountStr: String, month: String) {
        if (amountStr.isBlank()) { return }
        val amount = amountStr.toDoubleOrNull() ?: return

        val newBudget = Budget(
            userId = currentUserId,
            amount = amount,
            month = month
        )

        viewModelScope.launch {
            db.budgetDao().insertBudget(newBudget)
            _saveResult.postValue(true)
            loadBudgets() // Refresh the list
        }
    }

    fun deleteBudget(item: Budget) {
        viewModelScope.launch {
            db.budgetDao().deleteBudget(item)
            loadBudgets() // Refresh the list after deleting
        }
    }
}