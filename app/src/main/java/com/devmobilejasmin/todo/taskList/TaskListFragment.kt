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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devmobilejasmin.todo.R
import com.devmobilejasmin.todo.form.FormActivity
import com.devmobilejasmin.todo.network.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.util.*

class TaskListFragment : Fragment() {

    val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task

        viewModel.createOrUpdate(task)
    }

    val adapter = TaskListAdapter()

    private val viewModel: TaskListViewModel by viewModels()

    var infoTextView = view?.findViewById<TextView>(R.id.info_text)



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
        recyclerView.adapter = adapter

        infoTextView = view.findViewById<TextView>(R.id.info_text)


        view.findViewById<FloatingActionButton>(R.id.add_task_button).setOnClickListener {
            val intent = Intent(activity, FormActivity::class.java)
            formLauncher.launch(intent)
        }


        view.findViewById<FloatingActionButton>(R.id.reload_task_button).setOnClickListener {
            viewModel.refresh() // on demande de rafraîchir les données sans attendre le retour directement
        }


        adapter.onClickDelete = {task ->
            viewModel.delete(task)
        }

        adapter.onClickEdit = {task ->
            val intent = Intent(activity, FormActivity::class.java)
            intent.putExtra("task", task)
            formLauncher.launch(intent)
        }

        // Dans onViewCreated()
        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            viewModel.taskList.collect { newList ->

                // cette lambda est executée à chaque fois que la liste est mise à jour dans le repository
                // on met à jour la liste dans l'adapteur

                adapter.submitList(newList)
                adapter.notifyDataSetChanged()

            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()


        lifecycleScope.launch {
            val userInfo = Api.userWebService.getInfo().body()!!
            infoTextView?.text = "${userInfo.firstName} ${userInfo.lastName}"
        }

        /*
        // Dans onResume()
        lifecycleScope.launch {
            tasksRepository.refresh() // on demande de rafraîchir les données sans attendre le retour directement
        }

         */


    }
}