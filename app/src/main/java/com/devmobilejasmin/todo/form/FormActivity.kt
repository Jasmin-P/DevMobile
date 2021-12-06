package com.devmobilejasmin.todo.form

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.devmobilejasmin.todo.R
import com.devmobilejasmin.todo.taskList.Task
import java.util.*

class FormActivity : AppCompatActivity() {
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
}