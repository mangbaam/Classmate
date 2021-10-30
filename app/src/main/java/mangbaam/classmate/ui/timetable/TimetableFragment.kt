package mangbaam.classmate.ui.timetable

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.islandparadise14.mintable.MinTimeTableView
import com.islandparadise14.mintable.model.ScheduleDay
import com.islandparadise14.mintable.model.ScheduleEntity
import com.islandparadise14.mintable.tableinterface.OnScheduleClickListener
import com.islandparadise14.mintable.tableinterface.OnScheduleLongClickListener
import com.islandparadise14.mintable.tableinterface.OnTimeCellClickListener
import kotlinx.coroutines.selects.select
import mangbaam.classmate.AddLectureActivity
import mangbaam.classmate.MyTools
import mangbaam.classmate.R
import mangbaam.classmate.dao.LectureDao
import mangbaam.classmate.database.TableDB
import mangbaam.classmate.database.getTableDB
import mangbaam.classmate.databinding.FragmentTimetableBinding
import mangbaam.classmate.model.Lecture


class TimetableFragment : Fragment() {

    private var mBinding: FragmentTimetableBinding? = null
    private val binding get() = mBinding!!
    private lateinit var tableDB: TableDB
    private val schedules = arrayListOf<ScheduleEntity>()
    private lateinit var table: MinTimeTableView
    private lateinit var tableDao: LectureDao
    private lateinit var myLectures: Array<Lecture>
    private var tableSize = 0
    private val tools = MyTools()

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

        initTimeTable() // 시간표 초기화 (시간표 로딩 및 이벤트 리스너 부착)

        binding.addLectureButton.setOnClickListener {
            Log.d(TAG, "TimetableFragment - onCreateView() called : 과목 추가 버튼 눌림")
            val intent = Intent(context, AddLectureActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "TimetableFragment - onResume() called")
        if (tableDao.getSize() != tableSize) {
            Log.d(TAG, "TimetableFragment - 시간표 업데이트 called : tableSize: $tableSize")
            updateSchedules()
        }
        table.updateSchedules(schedules)
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
        /* 시간표 Room에서 불러와 초기화 */
        updateSchedules()
        /* 클릭 리스너 */
        table.setOnScheduleClickListener(
            object: OnScheduleClickListener {
                override fun scheduleClicked(entity: ScheduleEntity) {
                    Log.d(TAG, "${entity.scheduleName} 클릭")
                    Toast.makeText(context, entity.scheduleName, Toast.LENGTH_SHORT).show()
                    showTableDetailDialog(entity.originId)
                }
            }
        )
        /* 롱클릭 리스너 */
        table.setOnScheduleLongClickListener(
            object: OnScheduleLongClickListener {
                override fun scheduleLongClicked(entity: ScheduleEntity) {
                    Log.d(TAG, "${entity.scheduleName} 롱클릭")
                    Toast.makeText(context, "${entity.scheduleName}, ${entity.roomInfo}", Toast.LENGTH_SHORT).show()
                    showTableMenuDialog(entity)
                }
            }
        )
        /* 셀 클릭 리스너 */
        table.setOnTimeCellClickListener(
            object: OnTimeCellClickListener {
                override fun timeCellClicked(scheduleDay: Int, time: Int) {
                    Log.d(TAG, "scheduleDay: ${scheduleDay}, time: $time")
                }
            }
        )
    }

    private fun updateSchedules() {
        val colors = resources.getStringArray(R.array.cell_colors)
        myLectures = tableDao.getAll()
        var index = 0
        schedules.clear()
        myLectures.forEach { lecture ->
            val timeAndPlaceData = tools.parseTimeAndPlace(lecture.timeAndPlace)
            Log.d(TAG, "시간표 업데이트: [${lecture.id}]${lecture.name} - $timeAndPlaceData")
            index++
            timeAndPlaceData.forEach { timeInfo ->
                if(timeInfo.isNotEmpty()) {
                    val schedule = ScheduleEntity(
                        lecture.id,
                        lecture.name,
                        timeInfo[0],
                        ScheduleDay.getDay(timeInfo[1]),
                        timeInfo[2],
                        timeInfo[3],
                        colors[index % (colors.size - 1)]
                    )
                    schedules.add(schedule)
                }
            }
        }
        tableSize = tableDao.getSize()
        table.updateSchedules(schedules)
    }

    /* Schedule clicked */
    private fun showTableDetailDialog(id: Int) {
        val selectedLecture = tools.findLectureById(id, myLectures)
        Log.d(TAG, "[${selectedLecture.id}]$selectedLecture show")
        val message = "id: ${selectedLecture.id}\n" +
                "과목명: ${selectedLecture.name}\n" +
                "교수명: ${selectedLecture.professor}\n" +
                "시간 및 장소: ${selectedLecture.timeAndPlace}"
        val dialog = AlertDialog.Builder(context).apply {
            setTitle("강의 세부 정보")
            setMessage(message)
            setNeutralButton("닫기") { dialog, which -> dialog?.dismiss() }
        }
        dialog.create().show()
    }
    /* Schedule long clicked */
    private fun showTableMenuDialog(entity: ScheduleEntity) {

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