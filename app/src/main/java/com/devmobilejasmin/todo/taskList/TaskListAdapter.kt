package com.devmobilejasmin.todo.taskList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.devmobilejasmin.todo.R


object TasksDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
        // are they the same "entity" ? (usually same id)
        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem.equals(newItem)
    // do they have the same data ? (content)
}


class TaskListAdapter : androidx.recyclerview.widget.ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TasksDiffCallback) {

    var onClickDelete: (Task) -> Unit = {}
    var onClickEdit: (Task) -> Unit = {}

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(task: Task) {
            val textView = itemView.findViewById<TextView>(R.id.task_title)
            textView.text = task.title
            val textViewDescription = itemView.findViewById<TextView>(R.id.task_description)
            textViewDescription.text = task.description


            itemView.findViewById<ImageButton>(R.id.delete_task_button).setOnClickListener {
                onClickDelete(task)
            }

            itemView.findViewById<ImageButton>(R.id.edit_task_button).setOnClickListener {
                onClickEdit(task)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))


    }
}