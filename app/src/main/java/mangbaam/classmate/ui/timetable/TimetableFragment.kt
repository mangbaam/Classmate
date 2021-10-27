package mangbaam.classmate.ui.timetable

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.tlaabs.timetableview.Schedule
import com.github.tlaabs.timetableview.Time
import com.islandparadise14.mintable.MinTimeTableView
import com.islandparadise14.mintable.model.ScheduleDay
import com.islandparadise14.mintable.model.ScheduleEntity
import mangbaam.classmate.AddLectureActivity
import mangbaam.classmate.AppDatabase
import mangbaam.classmate.R
import mangbaam.classmate.databinding.FragmentTimetableBinding
import mangbaam.classmate.getAppDatabase


class TimetableFragment : Fragment() {

    private var mBinding: FragmentTimetableBinding? = null
    private val binding get() = mBinding!!
    private lateinit var appDB: AppDatabase
    private val schedules = arrayListOf<ScheduleEntity>()
    private lateinit var table: MinTimeTableView

    override fun onAttach(context: Context) {
        super.onAttach(context)

        appDB = getAppDatabase(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "TimetableFragment - onCreateView() called")
        mBinding = FragmentTimetableBinding.inflate(inflater, container, false)

        // TODO 테스트 과목 추가. 나중에 제거
        val schedule = ScheduleEntity(
            32,
            "Database",
            "IT-302",
            ScheduleDay.TUESDAY,
            "8:20",
            "10:30",
            "#73FCAE68",
            "#000000"
        )
        schedules.add(schedule)
        initTimeTable()

        binding.addLectureButton.setOnClickListener {
            Log.d(TAG, "TimetableFragment - onCreateView() called : 과목 추가 버튼 눌림")
            val intent = Intent(context, AddLectureActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun initTimeTable() {
        val day = resources.getStringArray(R.array.days)
        table = binding.timetableView
        table.initTable(day)
        table.updateSchedules(schedules)
    }

    private fun updateTimeTable(schedules: ArrayList<ScheduleEntity>) {
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

    companion object {
        const val TAG: String = "로그"
    }
}