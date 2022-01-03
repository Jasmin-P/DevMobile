package com.devmobilejasmin.todo.taskList

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.service.autofill.OnClickAction
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.devmobilejasmin.todo.R
import com.devmobilejasmin.todo.databinding.FragmentFormBinding
import com.devmobilejasmin.todo.databinding.FragmentTaskListBinding
import com.devmobilejasmin.todo.form.FormActivity
import com.devmobilejasmin.todo.network.*
import com.devmobilejasmin.todo.user.UserInfo
import com.devmobilejasmin.todo.user.UserInfoActivity
import com.devmobilejasmin.todo.user.UserInfoViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.util.*

class TaskListFragment : Fragment() {

    val userActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val userInfo = result.data?.getSerializableExtra("userInfo") as UserInfo

        binding.infoText.text = "${userInfo.firstName} ${userInfo.lastName}"
        binding.avatarImage.load(userInfo?.avatar) {
            // affiche une image par défaut en cas d'erreur:
            error(R.drawable.ic_launcher_background)
            transformations(CircleCropTransformation())
        }
    }

    val adapter = TaskListAdapter()

    private val viewModel: TaskListViewModel by viewModels()

    val SHARED_PREF_TOKEN_KEY = "auth_token_key"


    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter


        binding.addTaskButton.setOnClickListener {
            setNavigationInfo(Task("","",""), "modifiedTask")
            findNavController().navigate(R.id.action_taskListFragment_to_formActivity)
        }


        binding.reloadTaskButton.setOnClickListener {
            viewModel.refresh() // on demande de rafraîchir les données sans attendre le retour directement
        }

        binding.avatarImage.setOnClickListener {
            findNavController().navigate(R.id.action_taskListFragment_to_userInfoActivity)

        }

        binding.deconnexionButton.setOnClickListener {
            PreferenceManager.getDefaultSharedPreferences(context).edit {
                putString(SHARED_PREF_TOKEN_KEY, "error")
            }
            findNavController().navigate(R.id.action_taskListFragment_to_authenticationFragment)
        }


        adapter.onClickDelete = {task ->
            viewModel.delete(task)
        }

        adapter.onClickEdit = {task ->
            setNavigationInfo(task, "modifiedTask")
            findNavController().navigate(R.id.action_taskListFragment_to_formActivity)
        }

        val resultTask = this.getNavigationResultLiveData<Task>("modifiedTask")
        resultTask?.observe(viewLifecycleOwner){ task->
            viewModel.createOrUpdate(task)
        }

        /*
        val resultUser = this.getNavigationResultLiveData<UserInfo>("user")
        resultUser?.observe(viewLifecycleOwner){ userInfo ->
            binding.infoText.text = "${userInfo.firstName} ${userInfo.lastName}"
            binding.avatarImage.load(userInfo?.avatar) {
                // affiche une image par défaut en cas d'erreur:
                error(R.drawable.ic_launcher_background)
                transformations(CircleCropTransformation())
            }
        }

         */


        // Dans onViewCreated()
        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            viewModel.taskList.collect { newList ->

                // cette lambda est executée à chaque fois que la liste est mise à jour dans le repository
                // on met à jour la liste dans l'adapteur

                adapter.submitList(newList)
                adapter.notifyDataSetChanged()
            }
        }

        lifecycleScope.launch {
            val userInfo = Api.userWebService.getInfo().body()
            binding.infoText.text = "${userInfo?.firstName} ${userInfo?.lastName}"
            binding.avatarImage.load(userInfo?.avatar) {
                // affiche une image par défaut en cas d'erreur:
                error(R.drawable.ic_launcher_background)
                transformations(CircleCropTransformation())
            }
        }


        val token = PreferenceManager.getDefaultSharedPreferences(Api.appContext).getString(SHARED_PREF_TOKEN_KEY, "error")
        if (token == null || token == "error"){
            findNavController().navigate(R.id.action_taskListFragment_to_authenticationFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    /*
    android:maxSdkVersion="28"
        tools:replace="android:maxSdkVersion"
     */
}

fun <T> Fragment.getNavigationResult(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.get<T>(key)

fun <T> Fragment.getNavigationInfo(key: String = "result") =
    findNavController().previousBackStackEntry?.savedStateHandle?.get<T>(key)

fun <T> Fragment.getNavigationResultLiveData(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

fun <T> Fragment.setNavigationResult(result: T, key: String = "result") {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}

fun <T> Fragment.setNavigationInfo(result: T, key: String = "result") {
    findNavController().currentBackStackEntry?.savedStateHandle?.set(key, result)
}