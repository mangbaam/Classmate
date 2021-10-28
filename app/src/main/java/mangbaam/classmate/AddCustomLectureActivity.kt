package mangbaam.classmate

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.islandparadise14.mintable.model.ScheduleDay
import kotlinx.android.synthetic.main.activity_add_custom_lecture.*
import mangbaam.classmate.adapter.AddCustomLectureAdapter
import mangbaam.classmate.dao.LectureDao
import mangbaam.classmate.database.TableDB
import mangbaam.classmate.database.getTableDB
import mangbaam.classmate.databinding.ActivityAddCustomLectureBinding
import mangbaam.classmate.databinding.DialogDayOfWeekBinding
import mangbaam.classmate.model.TimeAndPlace
import mangbaam.classmate.model.TimeItem

class AddCustomLectureActivity : AppCompatActivity() {
    private var mBinding: ActivityAddCustomLectureBinding? = null
    private val binding get() = mBinding!!
    private val timeAndPlaceList: MutableList<TimeAndPlace> = mutableListOf()
    private lateinit var adapter: AddCustomLectureAdapter
    private lateinit var tableDB: TableDB
    private lateinit var tableDao: LectureDao

    private val verify = Verify()
    private val tools = MyTools()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddCustomLectureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 시간표에 추가된 강의 DB
        tableDB = getTableDB(this)
        tableDao = tableDB.tableDao()

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

        /* 과목명 required!! (추가 버튼 활성화) */
        binding.lectureNameEditText.addTextChangedListener {
            val enbled = binding.lectureNameEditText.text.isNotEmpty()
            binding.updateButton.isEnabled = enbled
        }

        /* 시간 및 장소 추가*/
        binding.addCustomLectureTextView.setOnClickListener {
            val item = TimeAndPlace("월", 9, 30, 12, 20, "")
            timeAndPlaceList.add(item)
            adapter.submitList(timeAndPlaceList)
            adapter.notifyDataSetChanged()
            Log.d(TAG, "AddCustomLectureActivity - '시간 및 장소 추가 버튼'${timeAndPlaceList.size} 눌림")
        }

        /* RecyclerView 설정 */
        binding.timeAndPlaceRecyclerView.adapter = adapter
        binding.timeAndPlaceRecyclerView.layoutManager = LinearLayoutManager(this)

        /* 추가 버튼 */
        binding.updateButton.setOnClickListener {
            timeAndPlaceList.forEach {
                if (it.startHour * 60 + it.startMinute > it.endHour * 60 + it.endMinute) {
                    Snackbar.make(
                        this.timeAndPlaceRecyclerView,
                        "종료 시간이 시작 시간보다 빠를 수 없습니다.",
                        Snackbar.LENGTH_LONG
                    ).show()
                    Log.d(TAG, "AddCustomLectureActivity - 강의 등록 실패: 시작 시간, 종료시간 설정 오류")
                    return@setOnClickListener
                }
            }
            for ((outer, value1) in timeAndPlaceList.withIndex()) {
                for ((inner, value2) in timeAndPlaceList.withIndex()) {
                    if (outer != inner && value1.dayOfWeek == value2.dayOfWeek) {
                        val startTime1 = value1.startHour * 60 + value1.startMinute
                        val endTime1 = value1.endHour * 60 + value1.endMinute
                        val startTime2 = value2.startHour * 60 + value2.startMinute
                        val endTime2 = value2.endHour * 60 + value2.endMinute
                        if (startTime1 in startTime2..endTime2 ||
                            endTime1 in startTime2..endTime2 ||
                            (startTime2 in startTime1..endTime1 && endTime2 in startTime1..endTime1)
                        ) {
                            Snackbar.make(
                                this.timeAndPlaceRecyclerView,
                                "겹치는 시간이 존재합니다. 입력한 시간을 확인하세요",
                                Snackbar.LENGTH_LONG
                            ).show()
                            Log.d(TAG, "AddCustomLectureActivity - 강의 등록 실패: 겹치는 시간 존재")
                            return@setOnClickListener
                        }
                    }
                }
            }
            val originLectures = tableDao.getAll()

            val newCustomLecture = tools.timeAndPlaceToLecture(getLectureName(), getProfessorName(), timeAndPlaceList)
            if(verify.verifyTime(originLectures, newCustomLecture)) {
                Log.d(TAG, "Custom 시간표 추가")
                tableDao.insertLecture(newCustomLecture) // TableDB에 저장, 이미 추가된 강의라면 무시
                finish()
            } else {
                Snackbar.make(
                    this.timeAndPlaceRecyclerView,
                    "기존 시간표와 겹치는 시간이 존재합니다",
                    Snackbar.LENGTH_LONG
                ).show()
                Log.d(TAG, "AddCustomLectureActivity - 강의 등록 실패: 기존 시간표와 겹침")
                return@setOnClickListener
            }
        }
    }
    private fun showDayOfWeekDialog(position: Int) {
        val dayOfWeekArray = resources.getStringArray(R.array.days) // 월요일, 화요일, ... , 토요일
        val dialogView = DialogDayOfWeekBinding.inflate(layoutInflater)
        val picker = dialogView.picker
        with(picker) {
            minValue = 0
            maxValue = dayOfWeekArray.size - 1
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
            setNegativeButton(R.string.cancel) { _, _ -> }
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

    private fun getLectureName(): String = binding.lectureNameEditText.text.toString()
    private fun getProfessorName(): String = binding.professorNameEditText.text.toString()
    private fun ScheduleDay.getDay(day: String): Int {
        return when (day.uppercase()) {
            "월", "월요일", "MONDAY", "MON" -> MONDAY
            "화", "화요일", "TUESDAY", "TUE" -> TUESDAY
            "수", "수요일", "WEDNESDAY", "WED" -> WEDNESDAY
            "목", "목요일", "THURSDAY", "THU" -> THURSDAY
            "금", "금요일", "FRIDAY", "FRI" -> FRIDAY
            "토", "토요일", "SATURDAY", "SAT" -> SATURDAY
            else -> 9999
        }
    }

    companion object {
        const val TAG: String = "로그"
    }
}