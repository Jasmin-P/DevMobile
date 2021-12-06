package com.devmobilejasmin.todo.taskList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devmobilejasmin.todo.R

class TaskListAdapter (public var taskList: List<Task>) : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

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
        holder.bind(taskList[position])


    }

    override fun getItemCount(): Int {
        return taskList.count()
    }


}