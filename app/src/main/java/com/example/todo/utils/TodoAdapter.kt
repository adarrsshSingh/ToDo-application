package com.example.todo.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.EachTodoItemBinding

class TodoAdapter(private val list:MutableList<TodoData>):RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {


    inner class TodoViewHolder(val binding:EachTodoItemBinding):RecyclerView.ViewHolder(binding.root)


    private var listener:TodoAdapterClickListener? = null


    fun setListener(listener: TodoAdapterClickListener){
        this.listener=listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding= EachTodoItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TodoViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return  list.size
    }


    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.tvTask.text=this.Task

                binding.ivEditTask.setOnClickListener{
                    listener?.onEditTaskButtonClicked(this)
                }
                binding.ivDeleteTask.setOnClickListener{
                    listener?.onDeleteTaskButtonClicked(this)
                }
            }
        }
    }


    interface  TodoAdapterClickListener{
        fun onDeleteTaskButtonClicked(todoData: TodoData)
        fun onEditTaskButtonClicked(todoData: TodoData)
    }
}