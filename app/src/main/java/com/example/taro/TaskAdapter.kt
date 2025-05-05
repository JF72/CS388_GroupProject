package com.example.taro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val tasks: List<UserTask>,
    private val onTaskClicked: (UserTask) -> Unit,
    private val onCompleteClicked: (UserTask) -> Unit,
    private val onEditClicked: (UserTask) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.taskName)
        val taskPoints: TextView = itemView.findViewById(R.id.taskPoints)
        val taskDueDate: TextView = itemView.findViewById(R.id.taskDueDate)
        val completeTaskButton: Button = itemView.findViewById(R.id.completeTaskButton)
        val editTaskButton: Button = itemView.findViewById(R.id.editTaskButton) // <-- Add this to XML


        fun bind(task: UserTask) {
            taskName.text = task.name
            taskPoints.text = "Points: ${task.points}"

            taskDueDate.text = task.dueDate?.let {
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                "Due: ${formatter.format(Date(it))}"
            } ?: "No Due Date"

            if (task.completed) {
                itemView.alpha = 0.5f
                completeTaskButton.isEnabled = false
                completeTaskButton.text = "Completed"
                editTaskButton.isEnabled = false
            } else {
                itemView.alpha = 1.0f
                completeTaskButton.isEnabled = true
                completeTaskButton.text = "Mark Complete"
                editTaskButton.isEnabled = true
            }

            completeTaskButton.setOnClickListener {
                onCompleteClicked(task)
            }

            editTaskButton.setOnClickListener {
                onEditClicked(task)
            }

            itemView.setOnClickListener {
                onTaskClicked(task)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.taro_task_item_layout, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size
}
