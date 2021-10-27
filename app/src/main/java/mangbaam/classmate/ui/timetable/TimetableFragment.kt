package mangbaam.classmate.ui.timetable

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.islandparadise14.mintable.MinTimeTableView
import com.islandparadise14.mintable.model.ScheduleDay
import com.islandparadise14.mintable.model.ScheduleEntity
import mangbaam.classmate.AddLectureActivity
import mangbaam.classmate.R
import mangbaam.classmate.dao.LectureDao
import mangbaam.classmate.database.TableDB
import mangbaam.classmate.database.getTableDB
import mangbaam.classmate.databinding.FragmentTimetableBinding


class TimetableFragment : Fragment() {

    private var mBinding: FragmentTimetableBinding? = null
    private val binding get() = mBinding!!
    private lateinit var tableDB: TableDB
    private val schedules = arrayListOf<ScheduleEntity>()
    private lateinit var table: MinTimeTableView
    private lateinit var tableDao: LectureDao
    private var tableSize = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)

        tableDB = getTableDB(context)
        tableDao = tableDB.tableDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "TimetableFragment - onCreateView() called")
        mBinding = FragmentTimetableBinding.inflate(inflater, container, false)

        initTimeTable()

        binding.addLectureButton.setOnClickListener {
            Log.d(TAG, "TimetableFragment - onCreateView() called : 과목 추가 버튼 눌림")
            val intent = Intent(context, AddLectureActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (tableDao.getSize() != tableSize) {
            updateSchedules()
            Log.d(TAG, "TimetableFragment - 시간표 업데이트 called")
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, "TimetableFragment - onDestroyView() called")
        super.onDestroyView()
        mBinding = null
    }

    override fun onDestroy() {
        Log.d(TAG, "TimetableFragment - onDestroy() called")
        super.onDestroy()
    }

    private fun initTimeTable() {
        val day = resources.getStringArray(R.array.days)
        table = binding.timetableView
        table.initTable(day)
        updateSchedules()
    }

    private fun updateSchedules() {
        val colors = resources.getStringArray(R.array.cell_colors)
        val myLectures = tableDao.getAll()
        var index = 0
        schedules.clear()
        myLectures.forEach { lecture ->
            val timeAndPlaceData = parseTimeAndPlace(lecture.timeAndPlace)
            index++
            timeAndPlaceData.forEach { timeInfo ->
                val schedule = ScheduleEntity(
                    lecture.id.toInt(),
                    lecture.name,
                    timeInfo[0],
                    ScheduleDay.getDay(timeInfo[1]),
                    timeInfo[2],
                    timeInfo[3],
                    colors[(colors.size-1)% index]
                )
                schedules.add(schedule)
            }
        }
        tableSize = tableDao.getSize()
        table.updateSchedules(schedules)
    }

    private fun parseTimeAndPlace(timeAndPlace: String): List<List<String>> {
        Log.d(TAG, "시간표: $timeAndPlace")
        val result = mutableListOf<List<String>>()
        if (timeAndPlace.isEmpty()) return listOf(emptyList())
        // 1. 장소 분리
        val myData = timeAndPlace.dropLast(1).split("),")
        for (tmpData in myData) {
            // 2. 장소와 시간 분리
            val tmp = tmpData.split("(")
            val place = tmp[0] // 장소
            val dayAndTime = tmp[1] // 여러 요일, 교시 데이터
            // 3. 여러 요일 분리
            val days = dayAndTime.split(" ")
            for (day in days) {
                // 4.1 요일과 교시 분리
                val dayOfWeek = day[0].toString() // 요일
                val timesOfDay = day.substring(1) // 교시
                val timeList = timesOfDay.split(",") // 교시 분리 3,4,7 -> [3, 4, 7]
                // 4.2 연속되지 않는 교시 분리
                var startIndex = 0;
                var index = 1
                val timeInfos = mutableListOf<List<String>>()
                while (index < timeList.size) {
                    if (timeList[index-1].toInt()+1 != timeList[index].toInt()) {
                        println("${timeList[index-1].toInt()+1} != ${timeList[index]}")
                        timeInfos.add(listOf(timeList[startIndex], timeList[index-1]))
                        startIndex=index
                    }
                    index++
                }
                timeInfos.add(listOf(timeList[startIndex], timeList[index-1]))
                // 4.3 교시 -> 시간 데이터로 변경하여 저장
                for (info in timeInfos) {
                    val startTime = "${info[0].toInt() + 8}:30"
                    val endTime = "${info[1].toInt() + 9}:20"
                    // 4.4 [[장소, 요일, 시작시간, 종료시작], [...], ...] 의 형태로 저장
                    result.add(listOf(place, dayOfWeek, startTime, endTime))
                }
            }
        }
        Log.d(TAG, "시간표: ->$result")
        return result
    }

    // 확장 함수
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