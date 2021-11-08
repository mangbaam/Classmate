package mangbaam.classmate.ui.todo

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.chauthai.swipereveallayout.SwipeRevealLayout
import kotlinx.android.synthetic.main.dialog_todo_menu.*
import kotlinx.android.synthetic.main.fragment_todo.*
import mangbaam.classmate.Constants.Companion.MODE_ADDITION
import mangbaam.classmate.Constants.Companion.MODE_EDIT
import mangbaam.classmate.Constants.Companion.MODE_VIEW
import mangbaam.classmate.Constants.Companion.TAG
import mangbaam.classmate.PreferenceHelper
import mangbaam.classmate.R
import mangbaam.classmate.adapter.TodoAdapter
import mangbaam.classmate.adapter.TodoSortedAdapter
import mangbaam.classmate.database.DB_keys.Companion.CHECKED_SORT_BY_ID
import mangbaam.classmate.database.DB_keys.Companion.CHECKED_SORT_ORDER_ID
import mangbaam.classmate.database.DB_keys.Companion.IS_DISPLAY_COMPLETED
import mangbaam.classmate.database.DB_keys.Companion.RADIO_ORDER_INDEX
import mangbaam.classmate.database.DB_keys.Companion.RADIO_STANDARD_INDEX
import mangbaam.classmate.database.TodoDB
import mangbaam.classmate.database.getTableDB
import mangbaam.classmate.database.getTodoDB
import mangbaam.classmate.databinding.DialogTodoMenuBinding
import mangbaam.classmate.databinding.FragmentTodoBinding
import mangbaam.classmate.databinding.ItemTodoBinding
import mangbaam.classmate.model.Lecture
import mangbaam.classmate.model.Priority
import mangbaam.classmate.model.SwipeButton
import mangbaam.classmate.model.TodoModel

class TodoFragment : Fragment(), TodoMenuInterface {
    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!
    private var _dBinding: DialogTodoMenuBinding? = null
    private val dBinding get() = _dBinding!!
    private lateinit var todoDB: TodoDB

    private var todoSortedAdapter: TodoSortedAdapter? = null

    private lateinit var menuDialog: TodoMenuCustomDialog
    private val todoList = mutableListOf<TodoModel>()
    private var currentList = listOf<TodoModel>()
    private val categoryNameList = mutableListOf("선택 안함")
    private val categoryIdList = mutableListOf(0)
    private lateinit var lectureData: Array<Lecture>

    private var isDeadline: Boolean = true
    private var isAscend: Boolean = true
    private var completeChecked: Boolean = true
    private var categoryId: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        todoDB = getTodoDB(context)
        menuDialog = TodoMenuCustomDialog(context, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "HomeFragment - onCreateView() called")
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        _dBinding = DialogTodoMenuBinding.inflate(inflater)

        initViews()

        return binding.root
    }

    private fun initViews() {
        val context = requireContext()
        val idStandard = dBinding.deadlineRadioButton.id
        val idAscend = dBinding.ascendRadioButton.id
        isDeadline = idStandard == PreferenceHelper.getInt(context, CHECKED_SORT_BY_ID)
        isAscend = idAscend == PreferenceHelper.getInt(context, CHECKED_SORT_ORDER_ID)
        completeChecked = PreferenceHelper.getBoolean(context, IS_DISPLAY_COMPLETED)

        todoList.addAll(todoDB.todoDao().getAll())
        listFilter()
        Log.d(TAG, "TodoFragment - initViews($todoList 룸에서 받아옴) called")


        todoSortedAdapter = TodoSortedAdapter(object: TodoSortedAdapter.OnClickListener {
            override fun onClick(binding: ItemTodoBinding, type: SwipeButton, position: Int) {
                when (type) {
                    SwipeButton.EDIT -> {
                        Log.d(TAG, "TodoFragment - onClick(EDIT) called")
                        val intent = Intent(requireContext(), AddTodoActivity::class.java)
                        intent.putExtra("mode", MODE_EDIT)
                        intent.putExtra("model", currentList[position])
                        intent.putExtra("position", position)
                        startActivityForResult(intent, editModeCode)
                        todoSortedAdapter?.viewBinderHelper?.closeLayout(currentList[position].id.toString())
                    }
                    SwipeButton.COMPLETE -> {
                        Log.d(TAG, "TodoFragment - onClick(COMPLETE) called")
                        currentList[position].priority = Priority.COMPLETE
                        todoList.forEachIndexed { index, todoModel ->
                            if (todoModel.id == currentList[position].id) {
                                todoList[index].priority = Priority.COMPLETE
                                todoModel.priority = Priority.COMPLETE
                                todoDB.todoDao().update(todoModel) // DB에 완료 처리
                            }
                        }
                        todoSortedAdapter?.viewBinderHelper?.closeLayout(currentList[position].id.toString())
                        refreshAdapter()
                    }
                    SwipeButton.VIEW -> {
                        Log.d(TAG, "${currentList[position]} 선택")
                        val selected = currentList[position]
                        val intent = Intent(requireContext(), AddTodoActivity::class.java)
                        intent.putExtra("mode", MODE_VIEW)
                        intent.putExtra("model", selected)
                        startActivityForResult(intent, viewModeCode)
                    }
                }
            }
        }, onItemClicked = { openViewMode(it) })

        binding.addTodoButton.setOnClickListener {
            val intent = Intent(requireContext(), AddTodoActivity::class.java)
            intent.putExtra("mode", MODE_ADDITION)
            startActivityForResult(intent, additionModeCode)
        }
        binding.sortButton.setOnClickListener {
            menuDialog.show()
        }
        binding.todoRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, 1))
            this.adapter = todoSortedAdapter
        }

        binding.todoSubtitleTextView.setOnClickListener {
            showCategoryDialog(context)
        }

        refreshAdapter()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "TodoFragment - onActivityResult($requestCode, $resultCode, $data) called")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                additionModeCode -> {
                    data?.getSerializableExtra("newModel").apply {
                        todoList.add(this as TodoModel)
                    }
                }
                editModeCode -> {
                    val model = data?.getSerializableExtra("newModel") as TodoModel
                    val position = data.getIntExtra("position", 0)
                    todoList.forEachIndexed { index, todoModel ->
                        if (todoModel.id == currentList[position].id) {
                            todoList[index] = model
                            return@forEachIndexed
                        }
                    }
                    todoDB.todoDao().update(model)
                }
            }
            refreshAdapter()
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, "HomeFragment - onDestroyView() called")
        super.onDestroyView()
        _binding = null
        _dBinding = null
    }

    override fun onDestroy() {
        Log.d(TAG, "HomeFragment - onDestroy() called")
        super.onDestroy()
    }

    companion object {
        const val additionModeCode = 101
        const val editModeCode = 102
        const val viewModeCode = 103
    }

    override fun onApplyButtonClicked() {
        Log.d(TAG, "TodoFragment - onApplyButtonClicked() called")
        val standard = menuDialog.standardRadioGroup.checkedRadioButtonId
        val order = menuDialog.orderRadioGroup.checkedRadioButtonId
        isDeadline = menuDialog.deadlineRadioButton.isChecked
        isAscend = menuDialog.ascendRadioButton.isChecked
        completeChecked = menuDialog.completedTodoCheckbox.isChecked

        // SharedPreference에 저장
        PreferenceHelper.setInt(requireContext(), CHECKED_SORT_BY_ID, standard)
        PreferenceHelper.setInt(requireContext(), CHECKED_SORT_ORDER_ID, order)
        PreferenceHelper.setBoolean(requireContext(), RADIO_STANDARD_INDEX, isDeadline)
        PreferenceHelper.setBoolean(requireContext(), RADIO_ORDER_INDEX, isAscend)
        PreferenceHelper.setBoolean(
            requireContext(),
            IS_DISPLAY_COMPLETED,
            menuDialog.completedTodoCheckbox.isChecked
        )

        Log.d(TAG, "적용 사항 - 마감일 기준: $isDeadline, 오름차순: $isAscend, 목록에 표시: $completeChecked")

        refreshAdapter()
        menuDialog.dismiss()
    }

    private fun listFilter() {
        // 완료된 과제 필터링
        currentList =
            if (completeChecked.not()) todoList.filter { it.priority != Priority.COMPLETE } else todoList
        // 카테고리 필터링
        if (categoryId != 0) currentList =
            currentList.filter { it.category == categoryId }// else currentList
        // 정렬 기준 필터링
        currentList =
            if (isDeadline) currentList.sortedByDescending { it.deadline } else currentList.sortedByDescending { it.priority }
        // 내림차순
        if (isAscend.not()) currentList = currentList.reversed()// else currentList

        binding.nothingToShowView.isGone = currentList.isNotEmpty()
    }

    private fun refreshAdapter() {
        listFilter()
        todoSortedAdapter?.submitList(currentList)
        todoSortedAdapter?.notifyDataSetChanged()
    }

    private fun showCategoryDialog(context: Context) {
        val categoryNameList2 = arrayOf("선택 안함")
        lectureData.forEach {
            categoryNameList2.plus(it.name)
            categoryIdList.add(it.id)
        }
        val listener = DialogInterface.OnClickListener { _, which ->
            categoryId = categoryIdList[which]
            refreshAdapter()
        }
        val arr = arrayOf("1","2","3")
        Log.d(TAG, "TodoFragment - showCategoryDialog(${categoryNameList2}) called")
        val builder = AlertDialog.Builder(context)
            .setTitle("카테고리 선택")
            .setMessage("필터링 할 과목을 선택하세요")
            .setItems(arr, listener)
            .create()
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "TodoFragment - onResume($currentList) called")
        lectureData = getTableDB(requireContext()).tableDao().getAll()
        refreshAdapter()
    }

    private fun openViewMode(position: Int) {
        Log.d(TAG, "${currentList[position]} 선택")
        val selected = currentList[position]
        val intent = Intent(requireContext(), AddTodoActivity::class.java)
        intent.putExtra("mode", MODE_VIEW)
        intent.putExtra("model", selected)
        startActivityForResult(intent, viewModeCode)
    }
}