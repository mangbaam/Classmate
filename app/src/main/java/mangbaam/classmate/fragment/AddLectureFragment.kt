package mangbaam.classmate.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_add_lecture.*
import kotlinx.android.synthetic.main.item_lecture.*
import mangbaam.classmate.AppDatabase
import mangbaam.classmate.OnLectureItemClick
import mangbaam.classmate.adapter.AddLectureAdapter
import mangbaam.classmate.databinding.FragmentAddLectureBinding
import mangbaam.classmate.getAppDatabase
import mangbaam.classmate.model.Lecture
import mangbaam.classmate.model.LectureData

class AddLectureFragment : Fragment(), OnLectureItemClick {
    private var mBinding: FragmentAddLectureBinding? = null
    private val binding get() = mBinding!!
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var appDB: AppDatabase
    private lateinit var adapter: AddLectureAdapter
    private var lectureList: ArrayList<Lecture> = arrayListOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "AddLectureFragment - onAttach() called")
        appDB = getAppDatabase(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "AddLectureFragment - onCreate() called")
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

    private fun search(keyword: String) {
        db.collection("USW_2021_2").addSnapshotListener { value, error ->
            lectureList.clear()

            for (snapshot in value!!.documents) {
                val item = snapshot.toObject(Lecture::class.java)
                lectureList.add(item!!)
            }
        }
    }

    private fun initSearchEditText() {
        binding.searchEditText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    private fun initLectureRecyclerView() {
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
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    override fun onLectureClicked(item: Lecture) {
        Log.d(TAG, "AddLectureFragment - onLectureClicked() : $item 선택됨")
        val lecture = LectureData(item.id, item.name, item.place, item.time, item.professor, item.classify, null)

        // TODO: Room에 item 저장
        Thread {
            appDB.lectureDao().insertLecture(lecture)
        }.start()

        val action = AddLectureFragmentDirections.actionNavigationAddLectureToNavigationTimetable(item)

        Navigation.findNavController(requireView()).navigate(action)
    }

    companion object {
        const val TAG: String = "로그"
    }

}