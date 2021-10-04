package mangbaam.classmate.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import mangbaam.classmate.R
import mangbaam.classmate.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "HomeFragment - onCreateView() called")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lectureInfoTextView.text = "강의가 없습니다"
        return binding.root
    }

    override fun onDestroyView() {
        Log.d(TAG, "HomeFragment - onDestroyView() called")
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        Log.d(TAG, "HomeFragment - onDestroy() called")
        super.onDestroy()
    }

    companion object {
        const val TAG: String = "로그"
    }
}