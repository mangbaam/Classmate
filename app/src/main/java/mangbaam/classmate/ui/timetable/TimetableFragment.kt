package mangbaam.classmate.ui.timetable

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import mangbaam.classmate.BaseActivity
import mangbaam.classmate.databinding.FragmentTimetableBinding


class TimetableFragment : Fragment() {

    private var mBinding: FragmentTimetableBinding? = null
    private val binding get() = mBinding!!

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