package com.devmobilejasmin.todo.taskList

import android.content.Intent
import android.os.Bundle
import android.service.autofill.OnClickAction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devmobilejasmin.todo.R
import com.devmobilejasmin.todo.form.FormActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class TaskListFragment : Fragment() {

    private var taskList = listOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )


    val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task



        taskList = taskList + task;
        adapter.taskList = taskList
        adapter.notifyItemInserted(taskList.size)

        // ici on récupérera le résultat pour le traiter
    }

    val adapter = TaskListAdapter(taskList)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_task_list, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        //val adapter = TaskListAdapter(taskList)
        recyclerView.adapter = adapter

        view.findViewById<FloatingActionButton>(R.id.add_task_button).setOnClickListener {
            val intent = Intent(activity, FormActivity::class.java)
            formLauncher.launch(intent)

            //val newTask = Task(id = UUID.randomUUID().toString(), title = "Task ${taskList.size + 1}")
            //taskList = taskList + newTask
            //view.findViewById<RecyclerView>(R.id.recycler_view).adapter?.notifyItemChanged(taskList.size-1, taskList);

            adapter.taskList = taskList
            adapter.notifyItemInserted(taskList.size)
        }

        /*
        formLauncher.runCatching {
            val task = result.data?.getSerializableExtra("task") as? Task
        }
        */

        adapter.onClickDelete = {task ->
            taskList = taskList - task
            adapter.taskList = taskList
            adapter.notifyDataSetChanged()
        }

        adapter.onClickEdit = {task ->

            taskList = taskList - task
            adapter.taskList = taskList
            adapter.notifyDataSetChanged()

            val intent = Intent(activity, FormActivity::class.java)
            intent.putExtra("task", task)
            formLauncher.launch(intent)

        }
    }
}