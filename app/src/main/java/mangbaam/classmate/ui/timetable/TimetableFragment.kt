package mangbaam.classmate.ui.timetable

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.islandparadise14.mintable.MinTimeTableView
import com.islandparadise14.mintable.model.ScheduleDay
import com.islandparadise14.mintable.model.ScheduleEntity
import com.islandparadise14.mintable.tableinterface.OnScheduleClickListener
import com.islandparadise14.mintable.tableinterface.OnScheduleLongClickListener
import com.islandparadise14.mintable.tableinterface.OnTimeCellClickListener
import mangbaam.classmate.AddLectureActivity
import mangbaam.classmate.Constants.Companion.TAG
import mangbaam.classmate.MyTools.Companion.findLectureById
import mangbaam.classmate.MyTools.Companion.getWeekDay
import mangbaam.classmate.MyTools.Companion.parseTimeAndPlace
import mangbaam.classmate.PreferenceHelper
import mangbaam.classmate.R
import mangbaam.classmate.dao.AlarmDao
import mangbaam.classmate.dao.LectureDao
import mangbaam.classmate.dao.ScheduleDao
import mangbaam.classmate.database.*
import mangbaam.classmate.database.DB_keys.Companion.ALARM_ON
import mangbaam.classmate.database.DB_keys.Companion.TERM
import mangbaam.classmate.databinding.FragmentTimetableBinding
import mangbaam.classmate.model.AlarmModel
import mangbaam.classmate.model.Lecture
import mangbaam.classmate.model.ScheduleModel
import mangbaam.classmate.notification.NotificationHelper.Companion.activateAllAlarms
import mangbaam.classmate.notification.NotificationHelper.Companion.removeAllAlarms


class TimetableFragment : Fragment() {

    private var mBinding: FragmentTimetableBinding? = null
    private val binding get() = mBinding!!
    private lateinit var tableDB: TableDB
    private lateinit var scheduleDB: ScheduleDB
    private lateinit var alarmDB: AlarmDB
    private lateinit var table: MinTimeTableView
    private lateinit var tableDao: LectureDao
    private lateinit var scheduleDao: ScheduleDao
    private lateinit var alarmDao: AlarmDao
    private lateinit var myLectures: Array<Lecture> // Room 에 저장된 나의 시간표

    private var schedules = arrayListOf<ScheduleEntity>()
    private var tableSize = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)

        /* Init DBs */
        tableDB = getTableDB(context)
        tableDao = tableDB.tableDao()
        scheduleDB = getScheduleDB(context)
        scheduleDao = scheduleDB.scheduleDao()
        alarmDB = getAlarmDB(context)
        alarmDao = alarmDB.alarmDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "TimetableFragment - onCreateView() called")
        mBinding = FragmentTimetableBinding.inflate(inflater, container, false)

        initTimeTable() // 시간표 초기화 (시간표 로딩 및 이벤트 리스너 부착)

        binding.termTextView.text = PreferenceHelper.getString(binding.termTextView.context, TERM)
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
            Log.d(TAG, "TimetableFragment - 시간표 업데이트 called : 원래 tableSize: $tableSize, 스케줄개수: ${schedules.size}")
            updateSchedules()
//            synchronizeSchedules()
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
        val days = resources.getStringArray(R.array.days)
        table = binding.timetableView
        table.initTable(days)
        /* 시간표 Room에서 불러와 초기화 */
        updateSchedules()
        /* 클릭 리스너 */
        table.setOnScheduleClickListener(
            object : OnScheduleClickListener {
                override fun scheduleClicked(entity: ScheduleEntity) {
                    showTableDetailDialog(entity.originId)
                }
            }
        )
        /* 롱클릭 리스너 */
        table.setOnScheduleLongClickListener(
            object : OnScheduleLongClickListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun scheduleLongClicked(entity: ScheduleEntity) {
                    Log.d(TAG, "${entity.scheduleName} 롱클릭")
                    showTableMenuDialog(entity)
                }
            }
        )
        /* 셀 클릭 리스너 */
        table.setOnTimeCellClickListener(
            object : OnTimeCellClickListener {
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
            val timeAndPlaceData = parseTimeAndPlace(lecture.timeAndPlace)
            Log.d(TAG, "시간표 업데이트: [${lecture.id}]${lecture.name} - $timeAndPlaceData")
            index++
            timeAndPlaceData.forEach { timeInfo ->
                if (timeInfo.isNotEmpty()) {
                    val schedule = ScheduleEntity(
                        lecture.id,
                        lecture.name,
                        timeInfo[0],
                        ScheduleDay.getDay(timeInfo[1]),
                        timeInfo[2],
                        timeInfo[3],
                        colors[index % (colors.size - 1)].toString()
                    )
                    schedules.add(schedule)
                }
            }
        }
        tableSize = tableDao.getSize()
        table.updateSchedules(schedules)

        synchronize()
    }

    /* Schedule clicked */
    private fun showTableDetailDialog(id: Int) {
        val selectedLecture = findLectureById(id, myLectures)
        Log.d(TAG, "[${selectedLecture.id}]$selectedLecture show")
        val message = "과목명: ${selectedLecture.name}\n" +
                "교수명: ${selectedLecture.professor}\n" +
                "시간 및 장소: ${selectedLecture.timeAndPlace}\n" +
                "학점: ${selectedLecture.point}\n" +
                "이수 구분: ${selectedLecture.classify}\n"
        val dialog = AlertDialog.Builder(context).apply {
            setTitle("강의 세부 정보")
            setMessage(message)
            setNeutralButton("닫기") { dialog, _ -> dialog?.dismiss() }
        }
        dialog.create().show()
    }

    /* Schedule long clicked */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun showTableMenuDialog(entity: ScheduleEntity) {
        val dialog = AlertDialog.Builder(context).apply {
            setTitle("과목 삭제")
            setMessage("삭제하시겠습니까?")
            setNegativeButton(getString(R.string.delete)) { _, _ ->
                deleteSchedule(entity)
                Log.d(TAG, "$entity 삭제 완료")
            }
            setNeutralButton("닫기") { dialog, _ -> dialog?.dismiss() }
        }
        dialog.create().show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun deleteSchedule(entity: ScheduleEntity) {
        scheduleDao.delete(entity.originId) // Schedule DB 에서 제거
        tableDao.deleteLecture(findLectureById(entity.originId, myLectures)) // Table DB 에서 제거
        myLectures = tableDao.getAll()
        schedules.removeIf { it.originId == entity.originId }
        alarmDao.delete(entity.originId)
        table.updateSchedules(schedules)
    }

    private fun synchronize() {
        val onOff = PreferenceHelper.getBoolean(table.context, ALARM_ON)
        scheduleDao.clear()
        alarmDao.clear()
        schedules.forEach {
            val scheduleModel = ScheduleModel(
                0,
                it.originId,
                it.scheduleName,
                it.roomInfo,
                it.scheduleDay,
                it.startTime,
                it.endTime,
                it.backgroundColor,
                it.textColor
            )
            scheduleDao.insert(scheduleModel)
        }
        scheduleDao.getAll().forEach {
            val timeData = it.startTime.split(":")
            val alarmModel = AlarmModel(
                it.id,
                it.originId,
                it.scheduleName,
                it.roomInfo,
                getWeekDay(it.scheduleDay),
                timeData[0].toInt(),
                timeData[1].toInt(),
                onOff
            )
            alarmDao.insert(alarmModel)
        }
        if (onOff) {
            removeAllAlarms(table.context, alarmDao.getAll())
            activateAllAlarms(table.context, alarmDao.getAll())
        }
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
}