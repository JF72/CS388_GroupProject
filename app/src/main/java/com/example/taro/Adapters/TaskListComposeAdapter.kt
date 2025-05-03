package com.example.taro.Adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.example.taro.R
import com.example.taro.components.DateCard
import com.example.taro.components.TaskListItem


class TaskListComposeAdapter(
    private val items : List<Pair<String,Boolean>>
) : RecyclerView.Adapter<TaskListComposeAdapter.TaskListViewHolder>(){
    class TaskListViewHolder(val composeView: ComposeView) : RecyclerView.ViewHolder(composeView);

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_list_compose_item, parent, false) as ComposeView
        return TaskListViewHolder(view)
    };

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        val (taskName,completed) = items[position]
        holder.composeView.setContent {
            TaskListItem(taskName = taskName,completed=completed)
        }
    }

    override fun getItemCount() = items.size
}

