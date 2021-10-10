package mangbaam.classmate.ui.timetable

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_timetable.*
import mangbaam.classmate.AppDatabase
import mangbaam.classmate.adapter.MyLectureAdapter
import mangbaam.classmate.databinding.FragmentTimetableBinding
import mangbaam.classmate.getAppDatabase
import mangbaam.classmate.model.Lecture


class TimetableFragment : Fragment() {

    private var mBinding: FragmentTimetableBinding? = null
    private val binding get() = mBinding!!
    private lateinit var adapter: MyLectureAdapter
    private lateinit var appDB: AppDatabase
    private val myLectureList: ArrayList<Lecture> = arrayListOf()

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


        binding.addLectureButton.setOnClickListener {
            Log.d(TAG, "TimetableFragment - onCreateView() called : 과목 추가 버튼 눌림")
            val action = TimetableFragmentDirections.actionNavigationTimetableToNavigationAddLecture()
            findNavController().navigate(action)
        }

        val selectedLecture = TimetableFragmentArgs.fromBundle(requireArguments()).selectedLecture
        adapter = MyLectureAdapter()
        binding.timetableRecyclerView.adapter = adapter
        binding.timetableRecyclerView.layoutManager = LinearLayoutManager(context)

        // TODO: 파라미터로 받아오는 것이 아닌 Room에 저장된 값을 불러오는 방식으로 변경
        Thread {
            val myLectures = appDB.lectureDao().getAll().toMutableList()
            Log.d(TAG, "TimetableFragment - $myLectures 받아오니...?")
            activity?.runOnUiThread {
                adapter.submitList(myLectures)
                adapter.notifyDataSetChanged()
            }
        }.start()

        // TODO: 뷰모델에 저장해서 라이프사이클과 관계없이 사용할 수 있도록 변경
        if (selectedLecture != null) myLectureList.add(selectedLecture)
        Log.d(TAG, "TimetableFragment - $selectedLecture 받음!!")

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