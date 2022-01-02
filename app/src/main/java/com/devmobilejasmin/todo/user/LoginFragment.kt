package com.devmobilejasmin.todo.user

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.devmobilejasmin.todo.R
import com.devmobilejasmin.todo.databinding.FragmentAuthenticationBinding
import com.devmobilejasmin.todo.databinding.FragmentLoginBinding
import com.devmobilejasmin.todo.network.Api
import com.devmobilejasmin.todo.taskList.Task
import com.devmobilejasmin.todo.taskList.TaskListFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    val SHARED_PREF_TOKEN_KEY = "auth_token_key"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.logInValidationButton.setOnClickListener {
            validationButtonClicked()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    fun validationButtonClicked(){
        if (binding.logInEmail.text == null || binding.logInPassword.text == null){
            Toast.makeText(context, "Erreur de connexion : Un des champ est vide", Toast.LENGTH_LONG).show()
        }

        val loginForm = LoginForm(binding.logInEmail.text.toString(), binding.logInPassword.text.toString())

        // permet d'éviter qu'il y ait n'importe quoi dans le token
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(SHARED_PREF_TOKEN_KEY, "error")
        }

        lifecycleScope.launch(Dispatchers.Main) {
            val result = Api.userWebService.login(loginForm)

            if (result.isSuccessful){
                PreferenceManager.getDefaultSharedPreferences(context).edit {
                    putString(SHARED_PREF_TOKEN_KEY, result.body().toString())
                }

                findNavController().navigate(R.id.action_loginFragment_to_taskListFragment)

            }
            else{
                Toast.makeText(context, "Erreur de connexion", Toast.LENGTH_LONG).show()
            }
        }
    }
}