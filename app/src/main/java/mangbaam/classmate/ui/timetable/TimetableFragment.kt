package mangbaam.classmate.ui.timetable

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import mangbaam.classmate.adapter.MyLectureAdapter
import mangbaam.classmate.databinding.FragmentTimetableBinding
import mangbaam.classmate.model.Lecture


class TimetableFragment : Fragment() {

    private var mBinding: FragmentTimetableBinding? = null
    private val binding get() = mBinding!!
    private lateinit var adapter: MyLectureAdapter
    private val myLectureList: ArrayList<Lecture> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        if (selectedLecture != null) myLectureList.add(selectedLecture)
        Log.d(TAG, "TimetableFragment - $selectedLecture 받음!!")
        adapter.submitList(myLectureList)
        binding.timetableRecyclerView.layoutManager = LinearLayoutManager(context)

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