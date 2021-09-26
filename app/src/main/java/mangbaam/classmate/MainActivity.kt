package mangbaam.classmate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import mangbaam.classmate.adapter.LectureAdapter
import mangbaam.classmate.databinding.ActivityMainBinding
import mangbaam.classmate.model.Lecture

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var addLectureFragment: Fragment

    private lateinit var adapter: LectureAdapter

    private val addLectureButton: Button by lazy { binding.addLectureButton }

    private lateinit var db: FirebaseFirestore

    private var lectureList: ArrayList<Lecture> = arrayListOf()

    private val lecturesRecyclerView: RecyclerView by lazy {
        binding.lecturesRecyclerView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initLectureRecyclerView()

        binding.noLectureTextView.text = "강의를 등록해라!!"

        initAddLectureFragment()

        addLectureButton.setOnClickListener {
            if (lecturesRecyclerView.isVisible) {
                lecturesRecyclerView.isGone = true
                addLectureButton.text = "강의 등록"
            } else {
                lecturesRecyclerView.isGone = false
                addLectureButton.text = "닫기"
            }
        }

        connectDB()
    }

    private fun connectDB() {
        db = Firebase.firestore
        db.collection("USW_2021_2")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    var lecture = Lecture(
                        document.id.toInt(),
                        document.data["name"].toString(),
                        document.data["time"].toString(),
                        document.data["place"].toString(),
                        document.data["professor"].toString(),
                        document.data["classify"].toString()
                    )
                    lectureList.add(lecture)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun initAddLectureFragment() {
        addLectureFragment = AddLectureFragment()
        val transaction = supportFragmentManager.beginTransaction()

    }

    private fun initLectureRecyclerView() {
        adapter = LectureAdapter()
        adapter.submitList(lectureList)
        lecturesRecyclerView.layoutManager = LinearLayoutManager(this)
        lecturesRecyclerView.adapter = adapter
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}