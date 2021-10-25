package mangbaam.classmate

import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import mangbaam.classmate.adapter.AddCustomLectureAdapter
import mangbaam.classmate.databinding.ActivityAddCustomLectureBinding
import mangbaam.classmate.model.TimeAndPlace
import java.text.SimpleDateFormat
import java.util.*

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
            when (it.id) {
                R.id.dayOfWeek -> {
                    Toast.makeText(this, "요일 선택", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "AddCustomLectureActivity - 요일 선택")
                }
                R.id.startTime -> {
                    Toast.makeText(this, "시작 시간 선택", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "AddCustomLectureActivity - 시작 시간 선택")
                    initDialog(position)
                }
                R.id.endTime -> {
                    Toast.makeText(this, "종료 시간 선택", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "AddCustomLectureActivity - 종료 시간 선택")
                }
                else -> {
                    Toast.makeText(this, "$it 선택", Toast.LENGTH_SHORT).show()
                }
            }
            if (it.id == R.id.dayOfWeek) {
                Toast.makeText(this, "요일 선택", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "AddCustomLectureActivity - 요일 선택")
            } else if (it.id == R.id.endTime) {
                Toast.makeText(this, "종료 시간 선택", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "AddCustomLectureActivity - 종료 시간 선택")
            }
        }, { position ->
            // TODO Snakbar를 띄워 삭제를 취소할 수 있는 기능
            timeAndPlaceList.removeAt(position)
            adapter.notifyItemRemoved(position)
        })

        /* 시간 및 장소 추가*/
        binding.addCustomLectureTextView.setOnClickListener {
            val item = TimeAndPlace("월", 9, 30, 12, 30, "")
            timeAndPlaceList.add(item)
            adapter.submitList(timeAndPlaceList)
            adapter.notifyDataSetChanged()
            Log.d(TAG, "AddCustomLectureActivity - '시간 및 장소 추가 버튼'${timeAndPlaceList.size} 눌림")
        }

        /* RecyclerView 설정 */
        binding.timeAndPlaceRecyclerView.adapter = adapter
        binding.timeAndPlaceRecyclerView.layoutManager = LinearLayoutManager(this)

        val lectureName = binding.lectureNameEditText.text
        val professorName = binding.professorNameEditText.text

    }

    private fun initDialog(position: Int) {
        val cal = Calendar.getInstance()
        val listener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            timeAndPlaceList[position].startHour = hourOfDay
            timeAndPlaceList[position].startMinute = minute
            adapter.notifyItemChanged(position)
        }
        val dialog = TimePickerDialog(this, listener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false)
        dialog.show()
    }

    companion object {
        const val TAG: String = "로그"
    }
}