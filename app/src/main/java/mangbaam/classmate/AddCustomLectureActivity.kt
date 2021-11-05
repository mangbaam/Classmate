package mangbaam.classmate

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_custom_lecture.*
import mangbaam.classmate.Constants.Companion.TAG
import mangbaam.classmate.MyTools.Companion.timeAndPlaceToLecture
import mangbaam.classmate.Verify.Companion.verifyTime
import mangbaam.classmate.adapter.AddCustomLectureAdapter
import mangbaam.classmate.dao.LectureDao
import mangbaam.classmate.database.TableDB
import mangbaam.classmate.database.getTableDB
import mangbaam.classmate.databinding.ActivityAddCustomLectureBinding
import mangbaam.classmate.databinding.DialogDayOfWeekBinding
import mangbaam.classmate.databinding.DialogEndTimeBinding
import mangbaam.classmate.databinding.DialogStartTimeBinding
import mangbaam.classmate.model.TimeAndPlace
import mangbaam.classmate.model.TimeItem

class AddCustomLectureActivity : AppCompatActivity() {
    private var mBinding: ActivityAddCustomLectureBinding? = null
    private val binding get() = mBinding!!
    private val places: MutableMap<Int, String> = mutableMapOf()
    private val timeAndPlaceList: MutableList<TimeAndPlace> = mutableListOf()
    private lateinit var adapter: AddCustomLectureAdapter
    private lateinit var tableDB: TableDB
    private lateinit var tableDao: LectureDao

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
            // TODO Snackbar를 띄워 삭제를 취소할 수 있는 기능
            timeAndPlaceList.removeAt(position)
            adapter.notifyItemRemoved(position)
        }, { position, text ->
            places[position] = text
        })

        /* 과목명 required!! (추가 버튼 활성화) */
        binding.lectureNameEditText.addTextChangedListener {
            val enabled = binding.lectureNameEditText.text.isNotEmpty()
            binding.updateButton.isEnabled = enabled
        }

        /* 시간 및 장소 추가*/
        binding.addCustomLectureTextView.setOnClickListener {
            val item = TimeAndPlace("월", "9:30", "12:20", "")
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
                val splitedStartList = it.startTime.split(":")
                val splitedEndList = it.endTime.split(":")
                val startHour = splitedStartList[0].toInt()
                val startMinute = splitedStartList[1].toInt()
                val endHour = splitedEndList[0].toInt()
                val endMinute = splitedEndList[1].toInt()

                if (startHour * 60 + startMinute > endHour * 60 + endMinute) {
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
                        val value1StartList = value1.startTime.split(":")
                        val value2StartList = value2.startTime.split(":")
                        val value1endList = value1.endTime.split(":")
                        val value2EndList = value2.endTime.split(":")
                        val value1StartHour = value1StartList[0].toInt(); val value1StartMinute = value1StartList[1].toInt()
                        val value2StartHour = value2StartList[0].toInt(); val value2StartMinute = value2StartList[1].toInt()
                        val value1EndHour = value1endList[0].toInt(); val value1EndMinute = value1endList[1].toInt()
                        val value2EndHour = value2EndList[0].toInt(); val value2EndMinute = value2EndList[1].toInt()

                        val startTime1 = value1StartHour * 60 + value1StartMinute
                        val endTime1 = value1EndHour * 60 + value1EndMinute
                        val startTime2 = value2StartHour * 60 + value2StartMinute
                        val endTime2 = value2EndHour * 60 + value2EndMinute
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

            places.forEach { (position, text) -> timeAndPlaceList[position].place = text } // 장소 데이터 이 시점에 추가
            val newCustomLecture = timeAndPlaceToLecture(getLectureName(), getProfessorName(), timeAndPlaceList)
            if(verifyTime(originLectures, newCustomLecture)) {
                Log.d(TAG, "Custom 시간표 추가")
                Log.d(TAG, "-> [${newCustomLecture.id}] $newCustomLecture")
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
            setTitle(getString(R.string.day_of_week_choice))
            setMessage(getString(R.string.choose_day_of_week))
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
        val startTimesArray = resources.getStringArray(R.array.start_times)
        val startClocksArray  = resources.getStringArray(R.array.start_clock)
        val dialogView = DialogStartTimeBinding.inflate(layoutInflater)
        val picker = dialogView.startTimePicker
        with(picker) {
            minValue = 0
            maxValue = startTimesArray.size - 1
            displayedValues = startTimesArray
            wrapSelectorWheel = false
        }
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.start_time_choice))
            setMessage(getString(R.string.choose_start_time))
            setView(dialogView.root)
            setPositiveButton(R.string.ok) { _, _ ->
                val index = dialogView.startTimePicker.value
                timeAndPlaceList[position].startTime = startClocksArray[index]
                adapter.notifyItemChanged(position)
            }
            setNegativeButton(R.string.cancel) { _, _ -> }
        }
        dialogBuilder.create().show()
    }

    private fun showEndTimeDialog(position: Int) {
        val endTimesArray = resources.getStringArray(R.array.end_times)
        val endClocksArray = resources.getStringArray(R.array.end_clock)

        val dialogView = DialogEndTimeBinding.inflate(layoutInflater)
        val picker = dialogView.endTimePicker
        with(picker) {
            minValue = 0
            maxValue = endTimesArray.size - 1
            displayedValues = endTimesArray
            wrapSelectorWheel = false
        }
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.end_time_choice))
            setMessage(getString(R.string.choose_end_time))
            setView(dialogView.root)
            setPositiveButton(R.string.ok) { _, _ ->
                val index = dialogView.endTimePicker.value
                timeAndPlaceList[position].endTime = endClocksArray[index]
                adapter.notifyItemChanged(position)
            }
            setNegativeButton(R.string.cancel) { _, _ -> }
        }
        dialogBuilder.create().show()
    }

    private fun getLectureName(): String = binding.lectureNameEditText.text.toString()
    private fun getProfessorName(): String = binding.professorNameEditText.text.toString()
}