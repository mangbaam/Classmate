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
import androidx.databinding.DataBindingUtil
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

//    private var mBinding: FragmentTimetableBinding? = null
//    private val binding get() = mBinding!!
    private val binding: FragmentTimetableBinding = DataBindingUtil.setContentView(this, R.layout.fragment_timetable)
    private val viewModel: TimeTableViewModel by lazy {
        TimeTableViewModel()
    }
    private lateinit var tableDB: TableDB
    private lateinit var scheduleDB: ScheduleDB
    private lateinit var alarmDB: AlarmDB
    private lateinit var table: MinTimeTableView
    private lateinit var tableDao: LectureDao
    private lateinit var scheduleDao: ScheduleDao
    private lateinit var alarmDao: AlarmDao
    private lateinit var myLectures: Array<Lecture> // Room ??? ????????? ?????? ?????????

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

        initTimeTable() // ????????? ????????? (????????? ?????? ??? ????????? ????????? ??????)

        binding.termTextView.text = PreferenceHelper.getString(binding.termTextView.context, TERM)
        binding.addLectureButton.setOnClickListener {
            Log.d(TAG, "TimetableFragment - onCreateView() called : ?????? ?????? ?????? ??????")
            val intent = Intent(context, AddLectureActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (tableDao.getSize() != tableSize) {
            Log.d(TAG, "TimetableFragment - ????????? ???????????? called : ?????? tableSize: $tableSize, ???????????????: ${schedules.size}")
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
        val days = resources.getStringArray(R.array.days)
        table = binding.timetableView
        table.initTable(days)
        /* ????????? Room?????? ????????? ????????? */
        updateSchedules()
        /* ?????? ????????? */
        table.setOnScheduleClickListener(
            object : OnScheduleClickListener {
                override fun scheduleClicked(entity: ScheduleEntity) {
                    showTableDetailDialog(entity.originId)
                }
            }
        )
        /* ????????? ????????? */
        table.setOnScheduleLongClickListener(
            object : OnScheduleLongClickListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun scheduleLongClicked(entity: ScheduleEntity) {
                    Log.d(TAG, "${entity.scheduleName} ?????????")
                    showTableMenuDialog(entity)
                }
            }
        )
        /* ??? ?????? ????????? */
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
            Log.d(TAG, "????????? ????????????: [${lecture.id}]${lecture.name} - $timeAndPlaceData")
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
        val message = "?????????: ${selectedLecture.name}\n" +
                "?????????: ${selectedLecture.professor}\n" +
                "?????? ??? ??????: ${selectedLecture.timeAndPlace}\n" +
                "??????: ${selectedLecture.point}\n" +
                "?????? ??????: ${selectedLecture.classify}"
        val dialog = AlertDialog.Builder(context).apply {
            setTitle("?????? ?????? ??????")
            setMessage(message)
            setNeutralButton("??????") { dialog, _ -> dialog?.dismiss() }
        }
        dialog.create().show()
    }

    /* Schedule long clicked */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun showTableMenuDialog(entity: ScheduleEntity) {
        val dialog = AlertDialog.Builder(context).apply {
            setTitle("?????? ??????")
            setMessage("?????????????????????????")
            setNegativeButton(getString(R.string.delete)) { _, _ ->
                deleteSchedule(entity)
                Log.d(TAG, "$entity ?????? ??????")
            }
            setNeutralButton("??????") { dialog, _ -> dialog?.dismiss() }
        }
        dialog.create().show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun deleteSchedule(entity: ScheduleEntity) {
        scheduleDao.delete(entity.originId) // Schedule DB ?????? ??????
        tableDao.deleteLecture(findLectureById(entity.originId, myLectures)) // Table DB ?????? ??????
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

    // ?????? ??????
    private fun ScheduleDay.getDay(day: String): Int {
        return when (day.uppercase()) {
            "???", "?????????", "MONDAY", "MON" -> MONDAY
            "???", "?????????", "TUESDAY", "TUE" -> TUESDAY
            "???", "?????????", "WEDNESDAY", "WED" -> WEDNESDAY
            "???", "?????????", "THURSDAY", "THU" -> THURSDAY
            "???", "?????????", "FRIDAY", "FRI" -> FRIDAY
            "???", "?????????", "SATURDAY", "SAT" -> SATURDAY
            else -> 9999
        }
    }
}