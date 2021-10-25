package mangbaam.classmate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mangbaam.classmate.databinding.ItemLectureBinding
import mangbaam.classmate.model.Lecture

class AddLectureAdapter(val onItemClicked: (Lecture) -> Unit) : ListAdapter<Lecture, AddLectureAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemLectureBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lectureModel: Lecture) {
            binding.root.setOnClickListener {
                onItemClicked(lectureModel)
            }
            binding.lectureName.text = lectureModel.name
            binding.lectureTimeAndPlace.text = lectureModel.timeAndPlace
            binding.professor.text = lectureModel.professor
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemLectureBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Lecture>() {
            override fun areItemsTheSame(oldItem: Lecture, newItem: Lecture): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Lecture, newItem: Lecture): Boolean {
                return oldItem == newItem
            }
        }
    }
}