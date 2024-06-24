package com.example.todo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.todo.R
import com.example.todo.databinding.FragmentAddTodoPopupBinding
import com.example.todo.utils.TodoData

class AddTodoPopupFragment : DialogFragment() {

    private lateinit var binding:FragmentAddTodoPopupBinding
    private lateinit var listener:PopupNextButtonClickListener
    private var toDoData: TodoData? = null


    companion object{
        const val TAG ="AddTodoPopUpFragment"
        @JvmStatic
        fun newInstance(taskId: String, task: String)=AddTodoPopupFragment().apply{
            arguments= Bundle().apply {
                putString("taskId",taskId)
                putString("task",task)
            }
        }
    }


    fun setListener(listener: PopupNextButtonClickListener){
        this.listener=listener
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentAddTodoPopupBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if( arguments != null ){
            toDoData= TodoData(
                arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString()
            )

            binding.etPopUp.setText(toDoData?.Task)
        }

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_button_popup)
        val params = dialog?.window?.attributes
        val margin = resources.getDimensionPixelSize(R.dimen.dialog_horizontal_margin)
        params?.width = resources.displayMetrics.widthPixels - 2 * margin
        dialog?.window?.attributes = params as WindowManager.LayoutParams

        registerEvents()
    }


    private fun registerEvents() {
        binding.btnAddPopUp.setOnClickListener{
            val todoTask=binding.etPopUp.text.toString()
            if(todoTask.isNotEmpty()){
                if(toDoData == null){
                    listener.onSaveTask(todoTask,binding.etPopUp)
                }else{
                    toDoData?.Task= todoTask
                    listener.onUpdateTask(toDoData!!,binding.etPopUp)
                }
            }else{
                Toast.makeText(context,"The task cannot be blank",Toast.LENGTH_SHORT).show()
            }
        }
        binding.ivClosePopUP.setOnClickListener{
            dismiss()
        }
    }


    interface PopupNextButtonClickListener{
        fun onSaveTask(task:String, TaskEt :EditText)
        fun onUpdateTask(todoData: TodoData, TaskEt :EditText)
    }
}