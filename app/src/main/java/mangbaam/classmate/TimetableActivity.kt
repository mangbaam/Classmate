package mangbaam.classmate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isGone
import androidx.core.view.isVisible
import mangbaam.classmate.databinding.ActivityTimetableBinding
import mangbaam.classmate.fragment.AddLectureFragment

class TimetableActivity : AppCompatActivity() {
    private val binding by lazy { ActivityTimetableBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()

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

    private fun setAddLectureFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.recyclerViewContainer, AddLectureFragment()).commit()
    }

    fun closeAddLecture() {
        binding.recyclerViewContainer.isGone = true
    }
}