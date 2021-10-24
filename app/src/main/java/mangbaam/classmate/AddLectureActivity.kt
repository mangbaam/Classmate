package mangbaam.classmate

import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
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
                lectureList.add(lectureModel)
            }
        }
        // 강의 list 초기화
        Thread(r).start()
    }

    private fun initRecyclerView() {
        adapter = AddLectureAdapter(onItemClicked = {
            Log.d(TAG, "AddLectureActivity : $it 선택됨")
            checkAddition(it)
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
                it.professor?.lowercase()?.contains(keyword) == true ||
                it.department?.lowercase()?.contains(keyword) == true) {
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

    private fun addCustomLecture(item: Lecture) {
        // TODO 과목 직접 입력 구현

    }

    private fun checkAddition(item: Lecture) {
        val listener = object: DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Log.d(TAG, "AddLectureActivity - ${item.name} 추가 버튼 클릭")
                    // TODO 나의 과목 Room에 추가
                    finish()
                }
            }
            override fun onCancel(dialog: DialogInterface?) {
                dialog?.cancel()
            }
        }

        AlertDialog.Builder(this)
            .setTitle("과목을 추가하시겠습니까")
            .setIcon(R.drawable.ic_library_add)
            .setMessage("과목명: ${item.name}\n" +
                    "시간 및 장소: ${item.timeAndPlace}\n" +
                    "교수명: ${item.professor}\n" +
                    "개설 부서: ${item.department}\n" +
                    "이수 구분: ${item.classify}\n" +
                    "학점: ${item.point}\n\n" +
                    "선택된 과목을 추가하려면 추가 버튼을 누르세요")
            .setPositiveButton("추가", listener)
            .setOnCancelListener(listener)
            .create()
            .show()
    }

    companion object {
        const val TAG: String = "로그"
    }
}
