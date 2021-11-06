package mangbaam.classmate.ui.todo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
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
    private lateinit var todoArray: ArrayList<TodoModel>

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
        todoArray.addAll(todoDB.todoDao().getAll())

        val adapter = TodoAdapter(requireContext(), object: TodoAdapter.OnClickListener {
            override fun onClick(binding: ItemTodoBinding, type: SwipeButton) {
                when(type) {
                    SwipeButton.EDIT -> {
                        Log.d(TAG, "TodoFragment - onClick(EDIT) called")
                    }
                    SwipeButton.COMPLETE -> {
                        Log.d(TAG, "TodoFragment - onClick(COMPLETE) called")
                    }
                }
            }
        }, todoArray)
        binding.nothingToShowView.isGone = true // 로티 임시 제거

        binding.addTodoButton.setOnClickListener {
            val intent = Intent(binding.addTodoButton.context, AddTodoActivity::class.java)
            startActivity(intent)
        }
        binding.menuButton.setOnClickListener {
            // TODO Drawable 메뉴 구성
        }
        binding.todoRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, 1))
            this.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()

        todoArray.clear()
        todoDB.todoDao().getAll()
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
}