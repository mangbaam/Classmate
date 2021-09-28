package mangbaam.classmate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import mangbaam.classmate.databinding.ActivityMainBinding
import mangbaam.classmate.fragment.AddLectureFragment

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()

    }

    private fun initViews() {
        // 상단 강의 정보
        binding.lectureInfoTextView.text = "강의 없음"

        binding.timetableButton.setOnClickListener {
            val intent = Intent(this, TimetableActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}