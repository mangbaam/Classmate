package mangbaam.classmate

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import mangbaam.classmate.database.DB_keys.Companion.LECTURES
import mangbaam.classmate.database.DB_keys.Companion.SCHOOL_NAME
import mangbaam.classmate.database.DB_keys.Companion.TERM
import mangbaam.classmate.dao.LectureDao
import mangbaam.classmate.database.AppDatabase
import mangbaam.classmate.database.getAppDatabase
import mangbaam.classmate.databinding.ActivityBaseBinding
import mangbaam.classmate.model.Lecture
import mangbaam.classmate.notification.NotificationHelper
import java.text.SimpleDateFormat
import java.util.*

class BaseActivity : AppCompatActivity() {

    private var mBinding: ActivityBaseBinding? = null
    private val binding get() = mBinding!!
    private lateinit var appDB: AppDatabase
    private lateinit var lectureDAO: LectureDao

    private val animationContainer: RelativeLayout by lazy {
        findViewById(R.id.splashAnimationContainer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler().postDelayed({animationContainer.isGone = true}, 5000)

        appDB = getAppDatabase(this)
        lectureDAO = appDB.lectureDao()

        val term = PreferenceHelper.getString(this, TERM).toString()
        Log.d(TAG, "SharedPreference - $term")
        if (term != getThisTerm()) {
            Log.d(TAG, "새로운 강의 업데이트 중...")
            updateLectures()
            PreferenceHelper.setString(this, TERM, getThisTerm())
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
        val schoolName = PreferenceHelper.getString(this, SCHOOL_NAME).toString()
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
                PreferenceHelper.setString(this, TERM, "")
                Toast.makeText(this, "과목 업데이트에 실패했습니다. 네트워크 연결을 확인하세요", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun lectureBinding(map: HashMap<String, Any>): Lecture {
        val lecture = Lecture(0, "", "", "", "", "", "", "", "")
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