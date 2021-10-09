package mangbaam.classmate.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mangbaam.classmate.OnLectureItemClick
import mangbaam.classmate.databinding.ItemLectureBinding
import mangbaam.classmate.model.Lecture

class AddLectureAdapter(onItemClick: OnLectureItemClick) :
    ListAdapter<Lecture, AddLectureAdapter.LectureItemViewHolder>(diffUtil) {

    private var onItemClick: OnLectureItemClick? = null

    init {
        this.onItemClick = onItemClick
    }

    inner class LectureItemViewHolder(
        private val binding: ItemLectureBinding,
        onItemClick: OnLectureItemClick
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private var onItemClick: OnLectureItemClick? = null

        init {
            binding.itemView.setOnClickListener(this)
            this.onItemClick = onItemClick
        }

        override fun onClick(v: View?) {
            Log.d(TAG, "LectureItemViewHolder - onClick() called")
            this.onItemClick?.onLectureClicked(currentList[adapterPosition])
        }

        fun bind(lectureModel: Lecture) {
            binding.lectureName.text = lectureModel.name
            binding.lectureTime.text = lectureModel.time
            binding.lecturePlace.text = lectureModel.place
            binding.professor.text = lectureModel.professor
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureItemViewHolder {
        return LectureItemViewHolder(
            ItemLectureBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            this.onItemClick!!
        )
    }

    override fun onBindViewHolder(holder: LectureItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        const val TAG: String = "로그"

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