package com.example.taro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val tasks: List<UserTask>, private val onTaskClicked: (UserTask) -> Unit, private val onCompleteClicked: (UserTask) -> Unit) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.taskName)
        val taskPoints: TextView = itemView.findViewById(R.id.taskPoints)
        val taskDueDate: TextView = itemView.findViewById(R.id.taskDueDate)
        val completeTaskButton: Button = itemView.findViewById(R.id.completeTaskButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.taro_task_item_layout, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.taskName.text = task.name
        holder.taskPoints.text = "Points: ${task.points}"
        holder.taskDueDate.text = if (task.dueDate != null) {
            val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date(task.dueDate))
            "Due: $date"
        } else {
            "No Due Date"
        }

        // NEW: Change appearance based on completion
        if (task.completed) {
            holder.itemView.alpha = 0.5f  // Lightly fade out the card
            holder.completeTaskButton.isEnabled = false
            holder.completeTaskButton.text = "Completed"
        } else {
            holder.itemView.alpha = 1.0f
            holder.completeTaskButton.isEnabled = true
            holder.completeTaskButton.text = "Mark Complete"
        }

        holder.completeTaskButton.setOnClickListener {
            onCompleteClicked(task)
        }

        holder.itemView.setOnClickListener {
            onTaskClicked(task)
        }
    }


    override fun getItemCount(): Int {
        return tasks.size
    }
}
