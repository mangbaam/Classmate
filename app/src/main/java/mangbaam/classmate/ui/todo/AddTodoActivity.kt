package mangbaam.classmate.ui.todo

import android.app.DatePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import mangbaam.classmate.R
import mangbaam.classmate.databinding.ActivityAddTodoBinding
import java.util.*

class AddTodoActivity : AppCompatActivity() {

    private var _binding: ActivityAddTodoBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initViews() {
        /* 과제 제목 Required -> 추가 버튼 활성화 */
        binding.todoTitleEditText.addTextChangedListener {
            binding.updateButton.isEnabled = binding.todoTitleEditText.text.isNotEmpty()
        }

        binding.todoPriorityButton.setOnClickListener {
            // TODO 우선순위 선택 다이얼로그 띄우기
        }
        binding.todoDeadlineButton.setOnClickListener {
            // TODO DatePicker 띄우기
            showDatePicker()
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePicker() {
        val listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

        }
        val dialog = DatePickerDialog(this, listener, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH)
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}