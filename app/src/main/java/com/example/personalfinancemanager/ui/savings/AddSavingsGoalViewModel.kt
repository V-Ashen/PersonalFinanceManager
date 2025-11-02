package com.example.personalfinancemanager.ui.savings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.personalfinancemanager.data.AppDatabase
import com.example.personalfinancemanager.data.SavingsGoal
import kotlinx.coroutines.launch

class AddSavingsGoalViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val currentUserId = 1

    // LiveData for the list of goals
    private val _goals = MutableLiveData<List<SavingsGoal>>()
    val goals: LiveData<List<SavingsGoal>> = _goals

    // LiveData for the save operation result
    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult

    // --- THIS IS THE MISSING PROPERTY ---
    // LiveData to pass a goal to the UI for editing
    private val _goalToEdit = MutableLiveData<SavingsGoal?>()
    val goalToEdit: LiveData<SavingsGoal?> = _goalToEdit

    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            _goals.postValue(db.savingsGoalDao().getSavingsGoalsForUser(currentUserId))
        }
    }

    fun saveGoal(goalId: Int?, goalName: String, targetAmountStr: String, currentAmountStr: String) {
        if (goalName.isBlank() || targetAmountStr.isBlank()) { return }
        val targetAmount = targetAmountStr.toDoubleOrNull() ?: return
        val currentAmount = currentAmountStr.toDoubleOrNull() ?: 0.0

        val goal = SavingsGoal(
            goalId = goalId ?: 0, // Use existing ID if updating, or 0 if new
            userId = currentUserId,
            goalName = goalName,
            targetAmount = targetAmount,
            currentAmount = currentAmount
        )

        viewModelScope.launch {
            db.savingsGoalDao().insertOrUpdateSavingsGoal(goal)
            _saveResult.postValue(true)
            loadGoals()
        }
    }

    fun deleteGoal(goal: SavingsGoal) {
        viewModelScope.launch {
            db.savingsGoalDao().deleteSavingsGoal(goal)
            loadGoals() // Refresh the list
        }
    }

    // --- THESE ARE THE MISSING FUNCTIONS ---
    fun onEditClicked(goal: SavingsGoal) {
        _goalToEdit.value = goal
    }

    fun onEditComplete() {
        _goalToEdit.value = null
    }
}