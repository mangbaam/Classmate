package mangbaam.classmate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import mangbaam.classmate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val addLectureButton: Button by lazy {
        binding.addLectureButton
    }
    private val lecturesRecyclerView: RecyclerView by lazy {
        binding.lecturesRecyclerView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.noLectureTextView.text = "강의를 등록해라!!"
        addLectureButton.setOnClickListener {
            if (lecturesRecyclerView.isVisible) {
                lecturesRecyclerView.isGone = false
                addLectureButton.text = "닫기"
            } else {
                lecturesRecyclerView.isGone = true
                addLectureButton.text = "강의 등록"
            }
        }

        // connectDB()
    }

    private fun connectDB() {
        val storage = FirebaseStorage.getInstance()
        var storageRef = storage.reference

    }
}