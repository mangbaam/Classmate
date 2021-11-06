package mangbaam.classmate.ui.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.islandparadise14.mintable.utils.dpToPx
import mangbaam.classmate.Constants.Companion.TODO_DEFAULT_HOUR
import mangbaam.classmate.Constants.Companion.TODO_DEFAULT_MINUTE
import mangbaam.classmate.MyTools.Companion.DAYms
import mangbaam.classmate.R
import mangbaam.classmate.database.TodoDB
import mangbaam.classmate.database.getTableDB
import mangbaam.classmate.database.getTodoDB
import mangbaam.classmate.databinding.ActivityAddTodoBinding
import mangbaam.classmate.model.Priority
import mangbaam.classmate.model.TodoModel
import java.text.SimpleDateFormat
import java.util.*

class AddTodoActivity : AppCompatActivity() {

    private var _binding: ActivityAddTodoBinding? = null
    private val binding get() = _binding!!

    private lateinit var todoDB: TodoDB

    private lateinit var popupWindow: PopupWindow

    private lateinit var priorityHighTextView: TextView
    private lateinit var priorityMidTextView: TextView
    private lateinit var priorityLowTextView: TextView
    private lateinit var priorityCompleteTextView: TextView

    private var priority = Priority.LOW
    private var deadline: Calendar? = null
    private var category: Int? = null

    private var categoryIdList = mutableListOf(0)
    private var categoryNameList = mutableListOf("선택 안함")

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()

        initViews()
    }

    private fun initData() {
        val tableDB = getTableDB(this)
        todoDB = getTodoDB(this)
        val myLectures = tableDB.tableDao().getAll()
        myLectures.forEach {
            categoryIdList.add(it.id)
            categoryNameList.add(it.name)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initViews() {
        /* 우선순위 선택 팝업 초기화 */
        setPopUpWindow()
        binding.todoPriorityButton.setOnClickListener {
            popupWindow.showAsDropDown(
                it,
                dpToPx(this, 30F).toInt(),
                -dpToPx(this, 48F).toInt()
            )
        }
        /* 과제 제목 Required -> 추가 버튼 활성화 */
        binding.todoTitleEditText.addTextChangedListener {
            binding.updateButton.isEnabled = binding.todoTitleEditText.text.isNotEmpty()
        }

        binding.todoCategorySpinner.attachDataSource(categoryNameList)
        binding.todoCategorySpinner.setOnSpinnerItemSelectedListener { _, _, position, _ ->
            Toast.makeText(applicationContext, "id: ${categoryIdList[position]}, position: ${position}, ${categoryNameList[position]}", Toast.LENGTH_SHORT).show()
            category = categoryIdList[position]
        }

        binding.todoDateButton.setOnClickListener {
            showDatePicker()
        }
        binding.todoTimeButton.setOnClickListener {
            showTimePicker()
        }

        binding.updateButton.setOnClickListener {
            val todoModel = TodoModel()
            todoModel.title = binding.todoTitleEditText.text.toString()
            todoModel.priority = priority
            todoModel.detail = binding.todoContentEditText.text.toString()
            todoModel.deadline = deadline?.timeInMillis ?: 0
            todoModel.category = category ?: 0

            Toast.makeText(this, "$todoModel 추가됨", Toast.LENGTH_LONG).show()
            todoDB.todoDao().insert(todoModel)
            finish()
        }
    }

    private fun setPopUpWindow() {
        val inflater =
            applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.select_priority_layout, null)
        priorityHighTextView = view.findViewById(R.id.priorityHigh)
        priorityMidTextView = view.findViewById(R.id.priorityMid)
        priorityLowTextView = view.findViewById(R.id.priorityLow)
        priorityCompleteTextView = view.findViewById(R.id.priorityComplete)
        /* 터치하여 우선순위 변경 */
        priorityHighTextView.setOnClickListener {
            setPriority(Priority.HIGH)
            popupWindow.dismiss()
        }
        priorityMidTextView.setOnClickListener {
            setPriority(Priority.MID)
            popupWindow.dismiss()
        }
        priorityLowTextView.setOnClickListener {
            setPriority(Priority.LOW)
            popupWindow.dismiss()
        }
        priorityCompleteTextView.setOnClickListener {
            setPriority(Priority.COMPLETE)
            popupWindow.dismiss()
        }
        /* 팝업 윈도우 생성 */
        popupWindow = PopupWindow(view, 800, RelativeLayout.LayoutParams.WRAP_CONTENT, true)
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(year, month, dayOfMonth, TODO_DEFAULT_HOUR, TODO_DEFAULT_MINUTE)
            binding.todoDateButton.text = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(cal.time)
            deadline = cal
            binding.todoTimeButton.isEnabled = true
            setPriority(Priority.MID)
        }
        val dialog =
            DatePickerDialog(this, listener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
        dialog.show()
    }

    private fun showTimePicker() {
        val listener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            if (deadline == null) return@OnTimeSetListener
            deadline?.set(Calendar.HOUR_OF_DAY, hour)
            deadline?.set(Calendar.MINUTE, minute)
            binding.todoTimeButton.text = SimpleDateFormat("a hh:mm", Locale.getDefault()).format(deadline!!.time)
        }
        deadline?.let { TimePickerDialog(this, listener, it.get(Calendar.HOUR_OF_DAY), deadline!!.get(Calendar.MINUTE), false).show() }
    }

    private fun setPriority(item: Priority) {
        when (item) {
            Priority.HIGH -> binding.todoPriorityButton.setBackgroundColor(ContextCompat.getColor(this, R.color.priority_high))
            Priority.MID -> binding.todoPriorityButton.setBackgroundColor(ContextCompat.getColor(this, R.color.priority_mid))
            Priority.LOW -> binding.todoPriorityButton.setBackgroundColor(ContextCompat.getColor(this, R.color.priority_low))
            Priority.COMPLETE -> binding.todoPriorityButton.setBackgroundColor(ContextCompat.getColor(this, R.color.priority_complete))
        }
        priority = item
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}