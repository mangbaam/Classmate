package mangbaam.classmate.ui.timetable

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.tlaabs.timetableview.Schedule
import com.github.tlaabs.timetableview.Time
import com.github.tlaabs.timetableview.TimetableView
import kotlinx.android.synthetic.main.fragment_timetable.*
import mangbaam.classmate.AddLectureActivity
import mangbaam.classmate.AppDatabase
import mangbaam.classmate.BaseActivity
import mangbaam.classmate.adapter.MyLectureAdapter
import mangbaam.classmate.databinding.FragmentTimetableBinding
import mangbaam.classmate.getAppDatabase
import mangbaam.classmate.model.Lecture


class TimetableFragment : Fragment() {

    private var mBinding: FragmentTimetableBinding? = null
    private val binding get() = mBinding!!
    private lateinit var appDB: AppDatabase
    private val schedules = arrayListOf<Schedule>()

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

        val testSchedule = Schedule()
        testSchedule.classTitle = "테스트 과목"
        testSchedule.classPlace = "IT-102"
        testSchedule.professorName = "김대엽"
        testSchedule.startTime = Time(10, 30)
        testSchedule.endTime = Time(13, 20)
        testSchedule.day = 2
        schedules.add(testSchedule)

        binding.timetableView.add(schedules)
        binding.timetableView.setHeaderHighlight(1)

        binding.addLectureButton.setOnClickListener {
            Log.d(TAG, "TimetableFragment - onCreateView() called : 과목 추가 버튼 눌림")
            val intent = Intent(context, AddLectureActivity::class.java)
            startActivity(intent)
        }

        return binding.root
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