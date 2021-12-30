package com.devmobilejasmin.todo.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.devmobilejasmin.todo.databinding.ActivityUserFormBinding
import com.devmobilejasmin.todo.databinding.ActivityUserInfoBinding

class UserFormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserFormBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val userInfo = intent.getSerializableExtra("userinfo") as UserInfo?


        binding.firstNameTextInput.setText(userInfo?.firstName.toString())
        binding.lastNameTextInput.setText(userInfo?.lastName.toString())
        binding.emailTextInput.setText(userInfo?.email.toString())

        binding.taskModificationValidationButton.setOnClickListener{
            val newUserInfo = UserInfo(
                firstName = binding.firstNameTextInput.text.toString(),
                lastName = binding.lastNameTextInput.text.toString(),
                email = binding.emailTextInput.text.toString(),
                avatar = userInfo?.avatar
            )

            intent.putExtra("userinfo", newUserInfo)
            setResult(RESULT_OK, intent)
            finish()
        }
        /*
        binding. .setText(task?.title.toString())
        descriptionString.setText(task?.description.toString())

        this.findViewById<Button>(R.id.task_modification_validation_button).setOnClickListener{
            val newTask = Task(id = id, title = titleString.text.toString(), description = descriptionString.text.toString())
            intent.putExtra("task", newTask)
            setResult(RESULT_OK, intent)
            finish()
        }
        */

    }

    private lateinit var binding: ActivityUserFormBinding
}