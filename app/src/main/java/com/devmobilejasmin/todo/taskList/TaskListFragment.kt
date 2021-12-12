package com.devmobilejasmin.todo.taskList

import android.content.Intent
import android.os.Bundle
import android.service.autofill.OnClickAction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devmobilejasmin.todo.R
import com.devmobilejasmin.todo.form.FormActivity
import com.devmobilejasmin.todo.network.Api
import com.devmobilejasmin.todo.network.TasksRepository
import com.devmobilejasmin.todo.network.UserInfo
import com.devmobilejasmin.todo.network.UserWebService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Retrofit
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
    var infoTextView = view?.findViewById<TextView>(R.id.info_text)

    private val tasksRepository = TasksRepository()









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


        view.findViewById<FloatingActionButton>(R.id.reload_task_button).setOnClickListener {
            lifecycleScope.launch {
                tasksRepository.refresh() // on demande de rafraîchir les données sans attendre le retour directement
            }
        }

        infoTextView = view.findViewById<TextView>(R.id.info_text)


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

        // Dans onViewCreated()
        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            tasksRepository.taskList.collect { newList ->

                // cette lambda est executée à chaque fois que la liste est mise à jour dans le repository
                // on met à jour la liste dans l'adapteur

                adapter.taskList = newList
                adapter.notifyDataSetChanged()

            }
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            val userInfo = Api.userWebService.getInfo().body()!!
            infoTextView?.text = "${userInfo.firstName} ${userInfo.lastName}"
        }


        // Dans onResume()
        lifecycleScope.launch {
            tasksRepository.refresh() // on demande de rafraîchir les données sans attendre le retour directement
        }


    }
}