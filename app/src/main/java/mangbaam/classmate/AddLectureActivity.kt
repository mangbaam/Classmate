package mangbaam.classmate

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import mangbaam.classmate.Verify.Companion.verifyTime
import mangbaam.classmate.adapter.AddLectureAdapter
import mangbaam.classmate.dao.LectureDao
import mangbaam.classmate.database.*
import mangbaam.classmate.database.DB_keys.Companion.ALARM_ON
import mangbaam.classmate.model.Lecture
import mangbaam.classmate.notification.NotificationHelper.Companion.registerAlarm

class AddLectureActivity : AppCompatActivity() {
    private lateinit var adapter: AddLectureAdapter
    private lateinit var appDB: AppDatabase
    private lateinit var tableDB: TableDB
    private lateinit var lectureDAO: LectureDao
    private lateinit var tableDao: LectureDao

    private var lectureList = mutableListOf<Lecture>()
    private val resultList = mutableListOf<Lecture>()

    private val searchEditText: EditText by lazy {
        findViewById(R.id.searchEditText)
    }
    private val resultRecyclerView: RecyclerView by lazy {
        findViewById(R.id.resultRecyclerView)
    }

    private val addCustomLectureButton: Button by lazy {
        findViewById(R.id.addCustomLectureButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lecture)

        // 전체 강의 DB
        appDB = getAppDatabase(this)
        lectureDAO = appDB.lectureDao()

        // 시간표에 추가된 강의 DB
        tableDB = getTableDB(this)
        tableDao = tableDB.tableDao()

        addCustomLectureButton.setOnClickListener {
            val intent = Intent(this, AddCustomLectureActivity::class.java)
            startActivity(intent)
        }
        initRecyclerView()
        initSearchEditText()

        val r = Runnable {
            val lectures = lectureDAO.getAll()
            for (lectureModel in lectures) {
                lectureList.add(lectureModel)
            }
        }
        // 강의 list 초기화
        Thread(r).start()
    }

    private fun initRecyclerView() {
        adapter = AddLectureAdapter(onItemClicked = {
            Log.d(TAG, "AddLectureActivity : [${it.id}]$it 추가 중...")
            showAddLectureDialog(it)
        })
        resultRecyclerView.adapter = adapter
        resultRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        resultRecyclerView.addItemDecoration(DividerItemDecoration(this, 1))
    }

    private fun search() {
        resultList.clear()
        val keyword = searchEditText.text.toString()

        lectureList.forEach {
            if (it.name.lowercase().contains(keyword) ||
                it.professor.lowercase().contains(keyword) ||
                it.department?.lowercase()?.contains(keyword) == true
            ) {
                resultList.add(it)
            }
        }
        adapter.submitList(resultList)
        adapter.notifyDataSetChanged()
    }

    private fun initSearchEditText() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    findViewById<LinearLayout>(R.id.informView).isGone = false
                } else {
                    findViewById<LinearLayout>(R.id.informView).isGone = true
                    search()
                }
            }
        })
    }

    private fun showAddLectureDialog(item: Lecture) {
        val listener = DialogInterface.OnClickListener { _, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                Log.d(TAG, "AddLectureActivity - [${item.id}]${item.name} 추가 버튼 클릭")
                val originLectures = tableDao.getAll()
                if (verifyTime(originLectures, item)) {
                    tableDao.insertLecture(item) // TableDB에 저장, 이미 추가된 강의라면 무시
                    // TODO 알람 등록
                    if (PreferenceHelper.getBoolean(this, ALARM_ON)) {
                        if (PreferenceHelper.getBoolean(this, DB_keys.ALARM_BEFORE_10)) {
                            registerAlarm(this, item, 10)
                        }
                        if (PreferenceHelper.getBoolean(this, DB_keys.ALARM_BEFORE_30)) {
                            registerAlarm(this, item, 30)
                        }
                    } else {

                    }
                    finish()
                } else {
                    Snackbar.make(
                        this.resultRecyclerView,
                        "시간이 겹치는 강의가 존재합니다.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

        AlertDialog.Builder(this)
            .setTitle("과목을 추가하시겠습니까")
            .setIcon(R.drawable.ic_library_add)
            .setMessage(
                "id: ${item.id}\n" +
                "과목명: ${item.name}\n" +
                "시간 및 장소: ${item.timeAndPlace}\n" +
                "교수명: ${item.professor}\n" +
                "개설 부서: ${item.department}\n" +
                "이수 구분: ${item.classify}\n" +
                "학점: ${item.point}\n\n" +
                "선택된 과목을 추가하려면 추가 버튼을 누르세요"
            )
            .setPositiveButton("추가", listener)
            .create()
            .show()
    }

    companion object {
        const val TAG: String = "로그"
    }
}
