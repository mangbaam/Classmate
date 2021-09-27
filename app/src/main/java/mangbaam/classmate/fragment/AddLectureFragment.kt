package mangbaam.classmate.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mangbaam.classmate.R
import mangbaam.classmate.adapter.AddLectureAdapter
import mangbaam.classmate.databinding.FragmentAddLectureBinding
import mangbaam.classmate.model.Lecture

class AddLectureFragment : Fragment() {
    private lateinit var adapter: AddLectureAdapter
    private lateinit var binding: FragmentAddLectureBinding
    private lateinit var lectureList: ArrayList<Lecture>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddLectureBinding.inflate(inflater, container, false)
        lectureList = arguments?.getParcelableArrayList<Lecture>("lectureList") as ArrayList<Lecture>
        lectureList.forEach {
            Log.d("AddLectureFragment", it.toString())
        }
        return binding.root
    }

    private fun initRecyclerView() {
        adapter = AddLectureAdapter()
    }
}