package mangbaam.classmate.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mangbaam.classmate.OnItemClick
import mangbaam.classmate.TimetableActivity
import mangbaam.classmate.databinding.ItemLectureBinding
import mangbaam.classmate.model.Lecture

class AddLectureAdapter: ListAdapter<Lecture, AddLectureAdapter.LectureItemViewHolder>(diffUtil) {

    inner class LectureItemViewHolder(private val binding: ItemLectureBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(lectureModel: Lecture) {
            binding.lectureName.text = lectureModel.name
            binding.lectureTime.text = lectureModel.time
            binding.lecturePlace.text = lectureModel.place
            binding.professor.text = lectureModel.professor

            binding.itemView.setOnClickListener {
                // TODO 선택된 과목 Room에 저장, List에 추가
            }
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureItemViewHolder {
        return LectureItemViewHolder(ItemLectureBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: LectureItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<Lecture>() {
            override fun areItemsTheSame(oldItem: Lecture, newItem: Lecture): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Lecture, newItem: Lecture): Boolean {
                return oldItem == newItem
            }
        }

        const val TAG = "AddLectureAdapter"
    }
}