package mangbaam.classmate

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import mangbaam.classmate.DB_keys.Companion.LECTURES
import mangbaam.classmate.DB_keys.Companion.SCHOOL_NAME
import mangbaam.classmate.DB_keys.Companion.수원대
import mangbaam.classmate.dao.LectureDao
import mangbaam.classmate.databinding.ActivityBaseBinding
import mangbaam.classmate.model.Lecture
import java.text.SimpleDateFormat
import java.util.*

class BaseActivity : AppCompatActivity() {

    private var mBinding: ActivityBaseBinding? = null
    private val binding get() = mBinding!!
    private lateinit var appDB: AppDatabase
    private lateinit var lectureDAO: LectureDao
    private val sharedPreference by lazy {
        getSharedPreferences("APP_SETTINGS", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDB = getAppDatabase(this)
        lectureDAO = appDB.lectureDao()

        val term = sharedPreference.getString("TERM", "").toString()
        Log.d(TAG, "SharedPreference - $term")
        if (term != getThisTerm()) { // if (term != getThisTerm())
            Log.d(TAG, "새로운 강의 업데이트 중...")
            updateLectures()
            Log.d(TAG, "업데이트 완료!")
            sharedPreference.edit(true) {
                putString("TERM", getThisTerm())
            }
        } else {
            Log.d(TAG, "업데이트 항목이 없습니다")
        }

        initNavigation()
    }

    private fun initNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavView, navController)
    }

    private fun updateLectures() {
        val schoolName = sharedPreference.getString(SCHOOL_NAME, 수원대).toString()
        Log.d(TAG, ">> SchoolName: $schoolName")
        Log.d(TAG, ">> Term: ${getThisTerm()}")

        val lectureDB = Firebase.database.reference
            .child(schoolName)
            .child(LECTURES)
            .child(getThisTerm())

        // Room에 있던 데이터 모두 제거
        Log.d(TAG, "기존 데이터 삭제...")
        Thread {
            appDB.lectureDao().clear()
        }

        lectureDB.get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.addOnSuccessListener { snapShot ->
                    for (snapshot in snapShot.children) {
                        val map = snapshot.value as HashMap<String, Any>
                        val lecture = lectureBinding(map)
                        Log.d(TAG, "BaseActivity - updateLectures($lecture) called")
                        Thread {
                            lectureDAO.insertLecture(lecture)
                        }.start()
                    }
                }
            } else {
                Log.d(TAG, "BaseActivity -  lectureDB Fail: $it")
            }
        }
    }

    private fun lectureBinding(map: HashMap<String, Any>): Lecture {
        val lecture = Lecture()
        for (key in map.keys) {
            lecture[key] = map[key]
        }
        return lecture
    }

    private fun getThisTerm(): String {
        val now = System.currentTimeMillis()
        val year = SimpleDateFormat("yyyy", Locale.KOREAN).format(now).toString()
        val month = SimpleDateFormat("MM", Locale.KOREAN).format(now).toInt()
        val term = if (month in 3..7) 1 else 2
        return "${year}_${term}"
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

    companion object {
        const val TAG: String = "로그"
    }
}