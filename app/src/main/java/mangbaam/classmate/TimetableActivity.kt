package mangbaam.classmate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import mangbaam.classmate.adapter.MyLectureAdapter
import mangbaam.classmate.databinding.ActivityTimetableBinding
import mangbaam.classmate.fragment.AddLectureFragment
import mangbaam.classmate.model.Lecture

class TimetableActivity : AppCompatActivity(), OnItemClick {
    private val binding by lazy { ActivityTimetableBinding.inflate(layoutInflater) }
    private lateinit var db: AppDatabase
    private lateinit var adapter: MyLectureAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
        connectRoomDB()

    }

    private fun initViews() {
        // 강의 추가 프래그먼트
        setAddLectureFragment()

        // 강의 추가 버튼
        binding.addLectureButton.setOnClickListener {
            if (binding.recyclerViewContainer.isVisible.not()) {
                binding.recyclerViewContainer.isGone = false
            }
        }
    }

    private fun connectRoomDB() {
        db = Room.databaseBuilder(
            this, AppDatabase::class.java, "addedLectureDB"
        ).build()
    }

    private fun setAddLectureFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.recyclerViewContainer, AddLectureFragment()).commit()
    }

    private fun initRecyclerView() {
        adapter = MyLectureAdapter()
        binding.myLecturesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.myLecturesRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.myLecturesRecyclerView.adapter = adapter
    }

    private fun addLecture(item: Lecture) {
        adapter.addItem(item)
    }

    fun closeAddLecture() {
        binding.recyclerViewContainer.isGone = true
    }

    override fun onClick(item: Lecture) {
        addLecture(item)
    }
}