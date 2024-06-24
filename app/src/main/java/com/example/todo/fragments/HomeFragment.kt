package com.example.todo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.R
import com.example.todo.databinding.FragmentHomeBinding
import com.example.todo.utils.TodoAdapter
import com.example.todo.utils.TodoData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment(), AddTodoPopupFragment.PopupNextButtonClickListener,
    TodoAdapter.TodoAdapterClickListener {

    private  lateinit var auth:FirebaseAuth
    private  lateinit var databaseRef:DatabaseReference
    private  lateinit var navController: NavController
    private  lateinit var binding:FragmentHomeBinding
    private var popupFragment:AddTodoPopupFragment? = null
    private lateinit var adapter: TodoAdapter
    private lateinit var mList: MutableList<TodoData>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentHomeBinding.inflate(inflater, container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()
        registerEvents()
    }

    private fun getDataFromFirebase() {
        databaseRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for(taskSanpshot in snapshot.children){
                    val TodoTask = taskSanpshot.key?.let {
                        TodoData(it,taskSanpshot.value.toString())
                    }
                    if(TodoTask != null){
                        mList.add(TodoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message,Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun registerEvents() {
        binding.addTaskBtnHome.setOnClickListener{
            if(popupFragment!=null)
                childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
            popupFragment= AddTodoPopupFragment()
            popupFragment!!.setListener(this)
            popupFragment!!.show(childFragmentManager,AddTodoPopupFragment.TAG)
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth=FirebaseAuth.getInstance()
        databaseRef=FirebaseDatabase.getInstance().reference
            .child("Tasks").child(auth.currentUser?.uid.toString())

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        mList= mutableListOf()
        adapter=TodoAdapter(mList)
        adapter.setListener(this)
        binding.recyclerView.adapter=adapter
    }

    override fun onSaveTask(task: String, TaskEt: EditText) {
            databaseRef.push().setValue(task).addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(context, "Task saved successfully",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, it.exception?.message,Toast.LENGTH_SHORT).show()
                }
                TaskEt.text=null
                popupFragment!!.dismiss()
            }
    }

    override fun onUpdateTask(toDoData: TodoData, TaskEt: EditText) {
        val map= HashMap<String,Any>()
        map[toDoData.TaskId]= toDoData.Task
        databaseRef.updateChildren(map).addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(context, "Task updated successfully",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, it.exception?.message,Toast.LENGTH_SHORT).show()
            }
            TaskEt.text=null
            popupFragment!!.dismiss()
        }
    }


    override fun onDeleteTaskButtonClicked(todoData: TodoData) {
        databaseRef.child(todoData.TaskId).removeValue().addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(context, "Deletion Successful",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, it.exception?.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditTaskButtonClicked(toDoData: TodoData) {
        if(popupFragment != null)
            childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
        popupFragment= AddTodoPopupFragment.newInstance(toDoData.TaskId, toDoData.Task)
        popupFragment!!.setListener(this)
        popupFragment!!.show(childFragmentManager, AddTodoPopupFragment.TAG)
    }
}