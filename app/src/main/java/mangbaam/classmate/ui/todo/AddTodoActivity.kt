package mangbaam.classmate.ui.todo

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.islandparadise14.mintable.utils.dpToPx
import mangbaam.classmate.MyTools.Companion.pxToDp
import mangbaam.classmate.R
import mangbaam.classmate.databinding.ActivityAddTodoBinding
import mangbaam.classmate.model.Priority
import java.util.*

class AddTodoActivity : AppCompatActivity() {

    private var _binding: ActivityAddTodoBinding? = null
    private val binding get() = _binding!!
    private val context get() = applicationContext

    private lateinit var popupWindow: PopupWindow

    private lateinit var priorityHighTextView: TextView
    private lateinit var priorityMidTextView: TextView
    private lateinit var priorityLowTextView: TextView
    private lateinit var priorityCompleteTextView: TextView

    private var title = ""
    private var priority = Priority.COMPLETE
    private var deadline = ""
    private var detailContent = ""

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initViews() {
        /* 우선순위 선택 팝업 초기화 */
        setPopUpWindow()
        /* 과제 제목 Required -> 추가 버튼 활성화 */
        binding.todoTitleEditText.addTextChangedListener {
            binding.updateButton.isEnabled = binding.todoTitleEditText.text.isNotEmpty()
        }

        binding.todoPriorityButton.setOnClickListener {
            popupWindow.showAsDropDown(
                it,
                dpToPx(context, 30F).toInt(),
                -dpToPx(context, 48F).toInt()
            )
        }

        binding.todoDeadlineButton.setOnClickListener {
            // TODO DatePicker 띄우기
            showDatePicker()
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
            binding.todoPriorityButton.setBackgroundColor(ContextCompat.getColor(context, R.color.priority_high))
            priority = Priority.HIGH
            popupWindow.dismiss()
        }
        priorityMidTextView.setOnClickListener {
            binding.todoPriorityButton.setBackgroundColor(ContextCompat.getColor(context, R.color.priority_mid))
            priority = Priority.MID
            popupWindow.dismiss()
        }
        priorityLowTextView.setOnClickListener {
            binding.todoPriorityButton.setBackgroundColor(ContextCompat.getColor(context, R.color.priority_low))
            priority = Priority.LOW
            popupWindow.dismiss()
        }
        priorityCompleteTextView.setOnClickListener {
            binding.todoPriorityButton.setBackgroundColor(ContextCompat.getColor(context, R.color.priority_complete))
            priority = Priority.COMPLETE
            popupWindow.dismiss()
        }
        /* 팝업 윈도우 생성 */
        popupWindow = PopupWindow(view, 600, RelativeLayout.LayoutParams.WRAP_CONTENT, true)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePicker() {
        val listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

        }
        val dialog =
            DatePickerDialog(this, listener, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH)
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}