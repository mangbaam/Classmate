package mangbaam.classmate.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_add_lecture.*
import kotlinx.android.synthetic.main.item_lecture.*
import mangbaam.classmate.AppDatabase
import mangbaam.classmate.DB_keys.Companion.LECTURES
import mangbaam.classmate.DB_keys.Companion.T2021_2
import mangbaam.classmate.DB_keys.Companion.수원대
import mangbaam.classmate.OnLectureItemClick
import mangbaam.classmate.adapter.AddLectureAdapter
import mangbaam.classmate.databinding.FragmentAddLectureBinding
import mangbaam.classmate.getAppDatabase
import mangbaam.classmate.model.Lecture
import mangbaam.classmate.model.LectureData
import java.util.*
import kotlin.collections.HashMap

class AddLectureFragment : Fragment() {
    private var mBinding: FragmentAddLectureBinding? = null
    private val binding get() = mBinding!!
    private lateinit var lectureDB: DatabaseReference
    private lateinit var appDB: AppDatabase
    private lateinit var adapter: AddLectureAdapter
    private var lectureList = mutableMapOf<String, Lecture>()
    private lateinit var lectures: MutableMap<String, HashMap<String, Any>>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "AddLectureFragment - onAttach() called")
        appDB = getAppDatabase(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "AddLectureFragment - onCreate() called")
        //connectDB()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "AddLectureFragment - onCreateView() called")
        mBinding = FragmentAddLectureBinding.inflate(inflater, container, false)
        initViews(binding)
        //initLectureRecyclerView()

        return binding.root
    }

    private fun initViews(binding: FragmentAddLectureBinding) {
        // TODO: TimetableFragment의 myLectureRecyclerView에 Lecture 추가
        // TODO: 키보드 내리기
    }

//    private fun connectDB() {
//        /*db.collection("USW_2021_2")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                    val lecture = Lecture(
//                        document.id.toInt(),
//                        document.data["name"].toString(),
//                        document.data["time"].toString(),
//                        document.data["place"].toString(),
//                        document.data["professor"].toString(),
//                        document.data["classify"].toString()
//                    )
//                    lectureList.add(lecture)
//                }
//
//                Log.d(TAG, "DB connect Success")
//            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents.", exception)
//            }*/
//
//        lectureDB = Firebase.database.reference.child("USW").child("2021_2")
//        lectureDB.get().addOnSuccessListener { task ->
//            lectures = task.value as MutableMap<String, HashMap<String, Any>>
//            Log.d(TAG, "Firebase.database - ${lectures.keys}")
//            Log.d(TAG, "Firebase.database - ${lectures.javaClass.name}")
//            Log.d(TAG, "Firebase Connect Successful")
//            bindData(lectures)
//        }.addOnFailureListener {
//            Log.d(TAG, "AddLectureFragment - lectureDB Fail : $it")
//        }
//
//    }

//    private fun bindData(lectures: MutableMap<String, HashMap<String, Any>>) {
//        val lectureList = mutableMapOf<String, Lecture>()
//        val matchingKeys: Map<String, String> = mapOf(
//            "과목 코드" to "id",
//            "과목명" to "name",
//            "학점" to "point",
//            "시간 및 장소" to "timeAndPlace",
//            "교수자" to "professor",
//            "분류" to "classify",
//            "부서" to "department",
//            "개설 학년" to "targetGrade"
//        )
//        lectures.forEach { item ->
//            // Log.d(TAG, "bindData($item) called")
//            val lectureKey = item.key
//            val lectureValue = item.value
//            val lectureItem = Lecture(
//                lectureValue["과목 코드"] as String,
//                lectureValue["과목명"] as String, null,
//                null, null, null, null, null
//            )
//            for (k in matchingKeys.keys) {
//                if (lectureValue.containsKey(k)) {
//                    lectureItem[matchingKeys[k]] = lectureValue[k]
//                }
//            }
//            lectureList[lectureKey] = lectureItem
//        }
//        Log.d(TAG, "AddLectureFragment - bindData() called")
//        Log.d(TAG, "$lectureList")
//        lectureList.forEach {
//            Log.d(TAG, "과목 - $it")
//        }
//    }

    private fun initSearchEditText() {
        binding.searchEditText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                // search(binding.searchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    /*private fun initLectureRecyclerView() {
        adapter = AddLectureAdapter(this)
        adapter.submitList(lectureList)
        binding.resultRecyclerView.adapter = adapter
        binding.resultRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.resultRecyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.resultRecyclerView.refreshDrawableState()
        Log.d(TAG, "AddLectureFragment - initLectureRecyclerView() called")
    }*/

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    /*override fun onLectureClicked(item: Lecture) {
        Log.d(TAG, "AddLectureFragment - onLectureClicked() : $item 선택됨")
        val lecture = LectureData(
            item.id,
            item.name,
            item.place,
            item.time,
            item.professor,
            item.classify,
            null
        )

        // TODO: Room에 이미 저장된 강의 선택하면 앱 죽음
        Thread {
            val isIn = appDB.lectureDao().check(lecture.id)
            if (isIn) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "이미 추가된 과목입니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                appDB.lectureDao().insertLecture(lecture)
            }
        }.start()

        val action =
            AddLectureFragmentDirections.actionNavigationAddLectureToNavigationTimetable(item)

        Navigation.findNavController(requireView()).navigate(action)
    }*/

    companion object {
        const val TAG: String = "로그"
    }

}