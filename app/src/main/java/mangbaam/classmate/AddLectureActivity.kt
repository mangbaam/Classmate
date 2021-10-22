package mangbaam.classmate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mangbaam.classmate.adapter.AddLectureAdapter
import mangbaam.classmate.dao.LectureDao
import mangbaam.classmate.model.Lecture

class AddLectureActivity : AppCompatActivity() {
    private lateinit var adapter: AddLectureAdapter
    private lateinit var appDB: AppDatabase
    private lateinit var lectureDAO: LectureDao

    private var lectureList = mutableListOf<Lecture>()
    private val resultList = mutableListOf<Lecture>()

    private val searchEditText: EditText by lazy {
        findViewById(R.id.searchEditText)
    }
    private val resultRecyclerView: RecyclerView by lazy {
        findViewById(R.id.resultRecyclerView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lecture)

        appDB = getAppDatabase(this)
        lectureDAO = appDB.lectureDao()

        initRecyclerView()
        initSearchEditText()

        val r = Runnable {
            val lectures = lectureDAO.getAll()
            for (lectureModel in lectures) {
                Log.d(TAG, "AddLectureActivity - onCreate($lectureModel) called")
                lectureList.add(lectureModel)
            }
        }
        // 강의 list 초기화
        Thread(r).start()
    }

    private fun initRecyclerView() {
        adapter = AddLectureAdapter(onItemClicked = {
            Log.d(TAG, "AddLectureActivity : $it 선택됨")
        })
        resultRecyclerView.adapter = adapter
        resultRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun search() {
        resultList.clear()
        val keyword = searchEditText.text.toString()
        if (keyword == "") {
            resultList.addAll(lectureList)
        } else {
            lectureList.forEach {
                if (it.name.lowercase().contains(keyword)) {
                    resultList.add(it)
                }
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
                search()
                for (result in resultList) {
                    Log.d(TAG, "afterTextChanged($result) called")
                }
            }
        })
    }

    companion object {
        const val TAG: String = "로그"
    }
}