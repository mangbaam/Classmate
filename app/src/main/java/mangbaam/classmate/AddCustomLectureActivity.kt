package mangbaam.classmate

import android.app.AlertDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_add_custom_lecture.*
import mangbaam.classmate.adapter.AddCustomLectureAdapter
import mangbaam.classmate.databinding.ActivityAddCustomLectureBinding
import mangbaam.classmate.databinding.DialogDayOfWeekBinding
import mangbaam.classmate.model.TimeAndPlace
import mangbaam.classmate.model.TimeItem

class AddCustomLectureActivity : AppCompatActivity() {
    private var mBinding: ActivityAddCustomLectureBinding? = null
    private val binding get() = mBinding!!
    private val timeAndPlaceList: MutableList<TimeAndPlace> = mutableListOf()
    private lateinit var adapter: AddCustomLectureAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddCustomLectureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* RecyclerView의 아이템 선택시 처리 */
        adapter = AddCustomLectureAdapter({ position, it ->
            when (it) {
                TimeItem.DAY_OF_WEEK -> showDayOfWeekDialog(position)
                TimeItem.START_TIME -> showStartTimeDialog(position)
                TimeItem.END_TIME -> showEndTimeDialog(position)
            }
        }, { position ->
            // TODO Snakbar를 띄워 삭제를 취소할 수 있는 기능
            timeAndPlaceList.removeAt(position)
            adapter.notifyItemRemoved(position)
        })

        /* 과목명, 교수명 required!! (추가 버튼 활성화) */
        binding.lectureNameEditText.addTextChangedListener {
            val enbled = binding.lectureNameEditText.text.isNotEmpty()
            binding.updateButton.isEnabled = enbled
        }

        /* 시간 및 장소 추가*/
        binding.addCustomLectureTextView.setOnClickListener {
            val item = TimeAndPlace("월", 9, 30, 12, 30, "")
            timeAndPlaceList.add(item)
            adapter.submitList(timeAndPlaceList)
            adapter.notifyDataSetChanged()
            timeAndPlaceRecyclerView.smoothScrollToPosition(timeAndPlaceList.size-1) // 맨 밑으로 이동
            Log.d(TAG, "AddCustomLectureActivity - '시간 및 장소 추가 버튼'${timeAndPlaceList.size} 눌림")
        }

        /* RecyclerView 설정 */
        binding.timeAndPlaceRecyclerView.adapter = adapter
        binding.timeAndPlaceRecyclerView.layoutManager = LinearLayoutManager(this)

    }

    private fun showDayOfWeekDialog(position: Int) {
        val dayOfWeekArray = resources.getStringArray(R.array.days) // 월요일, 화요일, ... , 토요일
        val dialogView = DialogDayOfWeekBinding.inflate(layoutInflater)
        val picker = dialogView.picker
        with(picker) {
            minValue = 0
            maxValue = dayOfWeekArray.size-1
            displayedValues = dayOfWeekArray
            wrapSelectorWheel = false
        }
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this).apply {
            setTitle("요일 선택")
            setMessage("요일을 선택하세요")
            setView(dialogView.root)
            setPositiveButton(R.string.ok) { _, _ ->
                val index = dialogView.picker.value
                timeAndPlaceList[position].dayOfWeek = dayOfWeekArray[index]
                adapter.notifyItemChanged(position)
            }
            setNegativeButton(R.string.cancel) {_, _ ->}
        }
        dialogBuilder.create().show()
    }

    private fun showStartTimeDialog(position: Int) {
        val listener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            timeAndPlaceList[position].startHour = hourOfDay
            timeAndPlaceList[position].startMinute = minute
            adapter.notifyItemChanged(position)
        }
        val dialog = TimePickerDialog(this, listener, 9, 30, false)
        dialog.setTitle("시작 시간 설정")
        dialog.show()
    }

    private fun showEndTimeDialog(position: Int) {
        val listener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            timeAndPlaceList[position].endHour = hourOfDay
            timeAndPlaceList[position].endMinute = minute
            adapter.notifyItemChanged(position)
        }
        val dialog = TimePickerDialog(this, listener, 12, 20, false)
        dialog.setTitle("종료 시간 설정")
        dialog.show()
    }

    companion object {
        const val TAG: String = "로그"
    }
}