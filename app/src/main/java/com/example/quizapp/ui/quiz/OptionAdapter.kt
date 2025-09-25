package com.example.quizapp.ui.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quizapp.R

data class OptionItem(
    val text: String,
    val index: Int,
    val isEnabled: Boolean,
    val isCorrect: Boolean,
    val isSelectedWrong: Boolean
)

class OptionAdapter(
    private val onOptionClick: (index: Int) -> Unit
) : ListAdapter<OptionItem, OptionAdapter.OptionViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_option_button, parent, false)
        return OptionViewHolder(view, onOptionClick)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object Diff : DiffUtil.ItemCallback<OptionItem>() {
        override fun areItemsTheSame(oldItem: OptionItem, newItem: OptionItem): Boolean =
            oldItem.index == newItem.index

        override fun areContentsTheSame(oldItem: OptionItem, newItem: OptionItem): Boolean =
            oldItem == newItem
    }

    class OptionViewHolder(
        itemView: View,
        private val onOptionClick: (index: Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.optionText)

        fun bind(item: OptionItem) {
            val context = itemView.context
            textView.text = item.text
            itemView.isEnabled = item.isEnabled

            val defaultText = ContextCompat.getColor(context, R.color.text_primary)

            when {
                item.isCorrect -> {
                    itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.correct_answer))
                    textView.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                }
                item.isSelectedWrong -> {
                    itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.wrong_answer))
                    textView.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                }
                else -> {
                    itemView.setBackgroundResource(R.drawable.option_button_selector)
                    textView.setTextColor(defaultText)
                }
            }

            itemView.setOnClickListener {
                if (item.isEnabled) onOptionClick(item.index)
            }
        }
    }
}


