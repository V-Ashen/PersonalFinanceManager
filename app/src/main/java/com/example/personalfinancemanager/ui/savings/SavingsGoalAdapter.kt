package com.example.personalfinancemanager.ui.savings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancemanager.R
import com.example.personalfinancemanager.data.SavingsGoal
import java.text.NumberFormat
import java.util.Locale

class SavingsGoalAdapter( private var goals: List<SavingsGoal>,
                          private val onEditClick: (SavingsGoal) -> Unit,
                          private val onDeleteClick: (SavingsGoal) -> Unit
) : RecyclerView.Adapter<SavingsGoalAdapter.GoalViewHolder>() {

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewGoalName)
        val amountsTextView: TextView = itemView.findViewById(R.id.textViewAmounts)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBarGoal)
        val percentageTextView: TextView = itemView.findViewById(R.id.textViewProgressPercentage)
        val editButton: ImageButton = itemView.findViewById(R.id.buttonEditGoal)
        val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDeleteGoal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_savings_goal, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]

        // Format numbers for better readability
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "LK")).apply {
            maximumFractionDigits = 2
            currency = java.util.Currency.getInstance("LKR")
        }
        val currentFormatted = currencyFormat.format(goal.currentAmount)
        val targetFormatted = currencyFormat.format(goal.targetAmount)

        holder.nameTextView.text = goal.goalName
        holder.amountsTextView.text = "$currentFormatted / $targetFormatted"

        // Calculate and set progress
        val progress = if (goal.targetAmount > 0) {
            (goal.currentAmount / goal.targetAmount * 100).toInt()
        } else {
            0
        }
        holder.progressBar.progress = progress
        holder.percentageTextView.text = "$progress% Complete"

        // TODO: Implement delete functionality later
        holder.deleteButton.setOnClickListener {
            // viewModel.deleteGoal(goal)
        }

        holder.editButton.setOnClickListener { onEditClick(goal) }
        holder.deleteButton.setOnClickListener { onDeleteClick(goal) }
    }

    override fun getItemCount() = goals.size

    fun updateData(newGoals: List<SavingsGoal>) {
        this.goals = newGoals
        notifyDataSetChanged()
    }
}