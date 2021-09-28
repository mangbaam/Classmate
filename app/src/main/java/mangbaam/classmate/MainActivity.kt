package mangbaam.classmate

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

        // 강의 추가 프래그먼트
        setAddLectureFragment()

        // 강의 추가 버튼
        binding.addLectureButton.setOnClickListener {
            if (binding.lecturesRecyclerView.isVisible) {
                closeAddLecture()
            } else {
                binding.addLectureContainer.isGone = false
                binding.addLectureButton.text = "닫기"
            }
        }
    }

    fun closeAddLecture() {
        binding.addLectureContainer.isGone = true
        binding.addLectureButton.text = "강의 등록"
    }

    private fun setAddLectureFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.addLectureContainer, AddLectureFragment()).commit()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}