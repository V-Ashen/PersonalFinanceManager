package com.example.personalfinancemanager.ui.budget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancemanager.R
import com.example.personalfinancemanager.data.Budget
import java.text.SimpleDateFormat
import java.util.Locale

class BudgetAdapter(
    private var budgets: List<Budget>,
    private val onDeleteClick: (Budget) -> Unit
) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val monthTextView: TextView = itemView.findViewById(R.id.textViewMonth)
        val limitTextView: TextView = itemView.findViewById(R.id.textViewBudgetLimit)
        val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDeleteBudget)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val item = budgets[position]

        // Format the "YYYY-MM" string into "Month YYYY"
        val inputFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        try {
            val date = inputFormat.parse(item.month)
            holder.monthTextView.text = outputFormat.format(date)
        } catch (e: Exception) {
            holder.monthTextView.text = item.month // Fallback
        }

        holder.limitTextView.text = "Limit: Rs. ${item.amount}"
        holder.deleteButton.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount() = budgets.size

    fun updateData(newBudgets: List<Budget>) {
        this.budgets = newBudgets
        notifyDataSetChanged()
    }
}