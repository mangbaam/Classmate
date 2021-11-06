package mangbaam.classmate.ui.todo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_todo.*
import mangbaam.classmate.Constants.Companion.MODE_ADDITION
import mangbaam.classmate.Constants.Companion.MODE_EDIT
import mangbaam.classmate.Constants.Companion.TAG
import mangbaam.classmate.adapter.TodoAdapter
import mangbaam.classmate.database.TodoDB
import mangbaam.classmate.database.getTodoDB
import mangbaam.classmate.databinding.FragmentTodoBinding
import mangbaam.classmate.databinding.ItemTodoBinding
import mangbaam.classmate.model.SwipeButton
import mangbaam.classmate.model.TodoModel

class TodoFragment : Fragment() {
    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!
    private lateinit var todoDB: TodoDB
    private val todoList = mutableListOf<TodoModel>()
    private lateinit var todoAdapter: TodoAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        todoDB = getTodoDB(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "HomeFragment - onCreateView() called")
        _binding = FragmentTodoBinding.inflate(inflater, container, false)

        initViews()

        return binding.root
    }

    private fun initViews() {
        todoList.addAll(todoDB.todoDao().getAll())
        Log.d(TAG, "TodoFragment - initViews($todoList 룸에서 받아옴) called")

        todoAdapter = TodoAdapter(object: TodoAdapter.OnClickListener {
            override fun onClick(binding: ItemTodoBinding, type: SwipeButton, position: Int) {
                when(type) {
                    SwipeButton.EDIT -> {
                        Log.d(TAG, "TodoFragment - onClick(EDIT) called")
                        val intent = Intent(requireContext(), AddTodoActivity::class.java)
                        intent.putExtra("mode", MODE_EDIT)
                        intent.putExtra("model", todoList[position])
                        intent.putExtra("position", position)
                        startActivityForResult(intent, editModeCode)
                    }
                    SwipeButton.COMPLETE -> {
                        Log.d(TAG, "TodoFragment - onClick(COMPLETE) called")
                    }
                }
            }
        }, todoList)
        binding.nothingToShowView.isGone = true // 로티 임시 제거

        binding.addTodoButton.setOnClickListener {
            val intent = Intent(requireContext(), AddTodoActivity::class.java)
            intent.putExtra("mode", MODE_ADDITION)
            startActivityForResult(intent, additionModeCode)
        }
        binding.menuButton.setOnClickListener {
            // TODO Drawable 메뉴 구성
        }
        binding.todoRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, 1))
            this.adapter = todoAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "TodoFragment - onActivityResult($requestCode, $resultCode, $data) called")
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                additionModeCode -> {
                    data?.getSerializableExtra("newModel").apply {
                        val insertIndex = 0
                        todoList.add(insertIndex, this as TodoModel)
                        todoAdapter.notifyItemInserted(insertIndex)
                        Toast.makeText(context, "${this}받아옴", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "TodoFragment - onActivityResult($this) 결과로 받아옴")
                        Log.d(TAG, "TodoFragment - Data: $todoList called")
                    }
                }
                editModeCode -> {
                    val model = data?.getSerializableExtra("newModel") as TodoModel
                    val position = data.getIntExtra("position", 0)
                    todoList[position] = model
                    todoAdapter.notifyItemChanged(position)
                    Toast.makeText(context, "${model.title}받아옴", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "TodoFragment - onActivityResult($model) 결과로 받아옴")
                    Log.d(TAG, "TodoFragment - Data: $todoList called")
                }
            }
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, "HomeFragment - onDestroyView() called")
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        Log.d(TAG, "HomeFragment - onDestroy() called")
        super.onDestroy()
    }

    companion object {
        const val additionModeCode = 101
        const val editModeCode = 102
    }
}