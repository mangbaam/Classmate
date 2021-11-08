package mangbaam.classmate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import mangbaam.classmate.MyTools
import mangbaam.classmate.R
import mangbaam.classmate.databinding.ItemTodoBinding
import mangbaam.classmate.model.Priority
import mangbaam.classmate.model.SwipeButton
import mangbaam.classmate.model.TodoModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

class TodoSortedAdapter(private val listener: OnClickListener) : ListAdapter<TodoModel, TodoSortedAdapter.ViewHolder>(diffUtil) {
    val viewBinderHelper = object: ViewBinderHelper() {}

    inner class ViewHolder(
        val binding: ItemTodoBinding,
        private val listener: OnClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(item: TodoModel) {
            val sdFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val context = binding.categoryTextView.context
            with(binding) {
                todoTitleTextView.text = item.title
                deadlineTextView.text =
                    if (item.deadline > 0) sdFormat.format(item.deadline) else "마감일 없음"
                priorityColorView.setBackgroundColor(
                    when (item.priority) {
                        Priority.HIGH -> ContextCompat.getColor(context, R.color.priority_high)
                        Priority.MID -> ContextCompat.getColor(context, R.color.priority_mid)
                        Priority.LOW -> ContextCompat.getColor(context, R.color.priority_low)
                        Priority.COMPLETE -> ContextCompat.getColor(
                            context,
                            R.color.priority_complete
                        )
                    }
                )
                if (item.deadline >= 0) {
                    val dDay =
                        item.deadline.minus(System.currentTimeMillis()).div(MyTools.DAYms).toInt()
                    dDayTextView.text = if (dDay > 0) "D-$dDay" else "D+${dDay.absoluteValue}"
                } else dDayTextView.text = "유효하지 않은 날짜"

                categoryTextView.text = item.categoryName
                editButton.setOnClickListener(this@ViewHolder)
                completeButton.setOnClickListener(this@ViewHolder)
            }
        }

        override fun onClick(v: View?) {
            with(binding) {
                when (v) {
                    editButton -> listener.onClick(this, SwipeButton.EDIT, adapterPosition)
                    completeButton -> listener.onClick(this, SwipeButton.COMPLETE, adapterPosition)
                    todoItem -> listener.itemClick(this, adapterPosition)
                    todoTitleTextView -> listener.itemClick(this, adapterPosition)
                    itemTodoRoot -> listener.itemClick(this, adapterPosition)
                }
            }
        }
    }

    interface OnClickListener {
        fun onClick(binding: ItemTodoBinding, type: SwipeButton, position: Int)
        fun itemClick(binding: ItemTodoBinding, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
        viewBinderHelper.bind(holder.binding.root, currentList[position].id.toString())
        viewBinderHelper.setOpenOnlyOne(true)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<TodoModel>() {
            override fun areItemsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}