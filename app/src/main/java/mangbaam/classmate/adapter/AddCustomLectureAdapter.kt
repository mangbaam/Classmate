package mangbaam.classmate.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mangbaam.classmate.databinding.ItemTimeAndPlaceBinding
import mangbaam.classmate.model.TimeAndPlace
import mangbaam.classmate.model.TimeItem
import mangbaam.classmate.model.TimeModel

class AddCustomLectureAdapter(
    private val onItemClicked: (Int, TimeItem) -> Unit,
    private val onCloseButtonClicked: (Int) -> (Unit)
) : ListAdapter<TimeAndPlace, AddCustomLectureAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemTimeAndPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: TimeAndPlace) {
            binding.dayOfWeek.text = item.dayOfWeek
            binding.startTime.text = TimeModel(item.startHour, item.startMinute).timeText
            binding.endTime.text = TimeModel(item.endHour, item.endMinute).timeText

            binding.dayOfWeek.setOnClickListener {
                onItemClicked(layoutPosition, TimeItem.DAY_OF_WEEK)
            }
            binding.startTime.setOnClickListener {
                onItemClicked(layoutPosition, TimeItem.START_TIME)
            }
            binding.endTime.setOnClickListener {
                onItemClicked(layoutPosition, TimeItem.END_TIME)
            }
            binding.closeButton.setOnClickListener {
                onCloseButtonClicked(layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemTimeAndPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<TimeAndPlace>() {
            override fun areItemsTheSame(oldItem: TimeAndPlace, newItem: TimeAndPlace): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: TimeAndPlace, newItem: TimeAndPlace): Boolean {
                return oldItem == newItem
            }
        }
    }
}