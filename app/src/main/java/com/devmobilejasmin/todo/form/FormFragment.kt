package com.devmobilejasmin.todo.form

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.devmobilejasmin.todo.R
import com.devmobilejasmin.todo.databinding.FragmentFormBinding
import com.devmobilejasmin.todo.databinding.FragmentLoginBinding
import com.devmobilejasmin.todo.taskList.*
import com.devmobilejasmin.todo.taskList.getNavigationResult
import com.devmobilejasmin.todo.user.LoginFragment
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class FormActivity : Fragment() {


    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val task = intent.getSerializableExtra("task") as Task?

        val id = task?.id ?: UUID.randomUUID().toString()



        val titleString = this.findViewById<EditText>(R.id.title_text_input)
        val descriptionString = this.findViewById<EditText>(R.id.description_text_input)

        titleString.setText(task?.title.toString())
        descriptionString.setText(task?.description.toString())

        this.findViewById<Button>(R.id.task_modification_validation_button).setOnClickListener{
            val newTask = Task(id = id, title = titleString.text.toString(), description = descriptionString.text.toString())
            intent.putExtra("task", newTask)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
    */


    private var param1: String? = null
    private var param2: String? = null


    private var _binding: FragmentFormBinding? = null
    private val binding get() = _binding!!


    val SHARED_PREF_TOKEN_KEY = "auth_token_key"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(com.devmobilejasmin.todo.form.ARG_PARAM1)
            param2 = it.getString(com.devmobilejasmin.todo.form.ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentFormBinding.inflate(inflater, container, false)
        val view = binding.root


        var id = UUID.randomUUID().toString();

        val result = this.getNavigationInfo<Task>("modifiedTask")



        id = result?.id ?: UUID.randomUUID().toString()

        binding.titleTextInput.setText(result?.title)
        binding.descriptionTextInput.setText(result?.description)


        binding.taskModificationValidationButton.setOnClickListener {

            val newTask = Task(id = id, title = binding.titleTextInput.text.toString(), description = binding.descriptionTextInput.text.toString())
            setNavigationResult(true, "valid")
            setNavigationResult(newTask, "modifiedTask")
            findNavController().popBackStack()
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
                    putString(com.devmobilejasmin.todo.form.ARG_PARAM1, param1)
                    putString(com.devmobilejasmin.todo.form.ARG_PARAM2, param2)
                }
            }
    }
}