package com.example.personalfinancemanager.ui.budget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancemanager.R
import com.example.personalfinancemanager.data.Budget
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddBudgetActivity : AppCompatActivity() {

    private val viewModel: AddBudgetViewModel by viewModels()
    private lateinit var budgetAdapter: BudgetAdapter
    private var currentMonthString: String = ""
    private var currentDisplayMonth: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_budget)

        val monthValueTextView = findViewById<TextView>(R.id.textViewMonthValue)
        val amountEditText = findViewById<EditText>(R.id.editTextBudgetAmount)
        val saveButton = findViewById<Button>(R.id.buttonSaveBudget)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewBudgets)

        // Set the current month in the UI
        val dbSdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val displaySdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val now = Date()
        currentMonthString = dbSdf.format(now)
        currentDisplayMonth = displaySdf.format(now)
        monthValueTextView.text = currentDisplayMonth

        // Setup RecyclerView
        budgetAdapter = BudgetAdapter(emptyList()) { budgetItem ->
            showDeleteConfirmationDialog(budgetItem)
        }
        recyclerView.adapter = budgetAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Observe existing budgets for the list
        viewModel.budgets.observe(this) { budgetList ->
            budgetAdapter.updateData(budgetList)
        }

        saveButton.setOnClickListener {
            viewModel.saveBudget(
                amountStr = amountEditText.text.toString(),
                month = currentMonthString
            )
        }

        viewModel.saveResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Budget saved successfully!", Toast.LENGTH_SHORT).show()
                amountEditText.text.clear()
            }
        }
    }

    private fun showDeleteConfirmationDialog(item: Budget) {
        AlertDialog.Builder(this)
            .setTitle("Delete Budget")
            .setMessage("Are you sure you want to delete this budget?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteBudget(item)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}