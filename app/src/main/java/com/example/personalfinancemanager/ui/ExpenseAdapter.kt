package com.example.personalfinancemanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancemanager.R
import com.example.personalfinancemanager.data.ExpenseWithCategory

class ExpenseAdapter(private var items: List<ExpenseWithCategory>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.imageViewCategoryIcon)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textViewDescription)
        val categoryNameTextView: TextView = itemView.findViewById(R.id.textViewCategoryName)
        val amountTextView: TextView = itemView.findViewById(R.id.textViewAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val item = items[position]
        val expense = item.expense

        holder.descriptionTextView.text = expense.description
        holder.categoryNameTextView.text = item.categoryName
        holder.amountTextView.text = "LKR ${expense.amount}"

        // Set the icon based on the category name
        val iconResId = when (item.categoryName) {
            "Groceries" -> R.drawable.ic_category_groceries
            "Transport" -> R.drawable.ic_category_transport
            "Bills" -> R.drawable.ic_category_bills
            else -> R.drawable.ic_category_another // Default icon
        }
        holder.iconImageView.setImageResource(iconResId)
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<ExpenseWithCategory>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}