package com.devmobilejasmin.todo.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devmobilejasmin.todo.taskList.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskListViewModel : ViewModel() {

    private val repository = TasksRepository()

    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    public val taskList: StateFlow<List<Task>> = _taskList

    fun refresh() {
        viewModelScope.launch {
            val newList = repository.loadTasks()

            if (newList != null) _taskList.value = newList
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            repository.delete(task)

            val oldTask = taskList.value.firstOrNull { it.id == task.id }
            if (oldTask != null) _taskList.value = taskList.value - oldTask
        }
    }

    fun createOrUpdate(task: Task) {
        viewModelScope.launch {
            val updatedTask = repository.createOrUpdate(task)

            if (updatedTask != null){
                val oldTask = taskList.value.firstOrNull { it.id == task.id }
                if (oldTask != null) {
                    _taskList.value = taskList.value - oldTask
                    _taskList.value = taskList.value + updatedTask
                }
                else {
                    _taskList.value = taskList.value + updatedTask
                }

            }

        }
    }
}