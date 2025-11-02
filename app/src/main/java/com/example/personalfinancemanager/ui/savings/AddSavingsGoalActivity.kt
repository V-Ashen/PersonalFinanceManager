package com.example.personalfinancemanager.ui.savings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancemanager.R
import com.example.personalfinancemanager.data.SavingsGoal

class AddSavingsGoalActivity : AppCompatActivity() {

    private val viewModel: AddSavingsGoalViewModel by viewModels()
    private lateinit var savingsGoalAdapter: SavingsGoalAdapter
    private var goalIdToUpdate: Int? = null

    private lateinit var goalNameEditText: EditText
    private lateinit var targetAmountEditText: EditText
    private lateinit var currentAmountEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_savings_goal)

        // Initialize all UI components here
        goalNameEditText = findViewById(R.id.editTextGoalName)
        targetAmountEditText = findViewById(R.id.editTextTargetAmount)
        currentAmountEditText = findViewById(R.id.editTextCurrentAmount)
        saveButton = findViewById(R.id.buttonSaveGoal)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewSavingsGoals)

        savingsGoalAdapter = SavingsGoalAdapter(
            emptyList(),
            onEditClick = { goal -> viewModel.onEditClicked(goal) },
            onDeleteClick = { goal -> showDeleteConfirmationDialog(goal) }
        )
        recyclerView.adapter = savingsGoalAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.goals.observe(this) { goalsList ->
            savingsGoalAdapter.updateData(goalsList)
        }

        saveButton.setOnClickListener {
            viewModel.saveGoal(
                goalId = goalIdToUpdate,
                goalName = goalNameEditText.text.toString(),
                targetAmountStr = targetAmountEditText.text.toString(),
                currentAmountStr = currentAmountEditText.text.toString()
            )
        }

        viewModel.goalToEdit.observe(this) { goal ->
            goal?.let {
                goalIdToUpdate = it.goalId
                goalNameEditText.setText(it.goalName)
                targetAmountEditText.setText(it.targetAmount.toString())
                currentAmountEditText.setText(it.currentAmount.toString())
                saveButton.setText("Update Goal")
            }
        }

        viewModel.saveResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Savings goal saved!", Toast.LENGTH_SHORT).show()
                clearForm()
            }
        }
    }

    private fun clearForm() {
        goalNameEditText.text.clear()
        targetAmountEditText.text.clear()
        currentAmountEditText.text.clear()
        saveButton.setText("Save Savings Goal")
        goalIdToUpdate = null
        viewModel.onEditComplete()
    }

    private fun showDeleteConfirmationDialog(goal: SavingsGoal) {
        AlertDialog.Builder(this)
            .setTitle("Delete Goal")
            .setMessage("Are you sure you want to delete the goal '${goal.goalName}'?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteGoal(goal)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
} // This is the final closing brace for the class
// <-- This is the closing brace for the class