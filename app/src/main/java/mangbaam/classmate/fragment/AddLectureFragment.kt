package mangbaam.classmate.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mangbaam.classmate.adapter.AddLectureAdapter
import mangbaam.classmate.databinding.FragmentAddLectureBinding
import mangbaam.classmate.model.Lecture

class AddLectureFragment : Fragment() {
    private var mBinding : FragmentAddLectureBinding? = null
    private val binding get() = mBinding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: AddLectureAdapter
    private var lectureList: ArrayList<Lecture> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectDB()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "AddLectureFragment - onCreateView() called")
        mBinding = FragmentAddLectureBinding.inflate(inflater, container, false)
        initViews(binding)
        initLectureRecyclerView()

        return binding.root
    }

    private fun initViews(binding: FragmentAddLectureBinding) {
        // TODO: TimetableFragment의 myLectureRecyclerView에 Lecture 추가
        // TODO: 키보드 내리기
    }

    private fun connectDB() {
        db = Firebase.firestore
        db.collection("USW_2021_2")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val lecture = Lecture(
                        document.id.toInt(),
                        document.data["name"].toString(),
                        document.data["time"].toString(),
                        document.data["place"].toString(),
                        document.data["professor"].toString(),
                        document.data["classify"].toString()
                    )
                    lectureList.add(lecture)
                }

                Log.d(TAG, "DB connect Success")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun initLectureRecyclerView() {
        adapter = AddLectureAdapter()
        adapter.submitList(lectureList)
        binding.resultRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.resultRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        binding.resultRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG: String = "로그"
    }
}