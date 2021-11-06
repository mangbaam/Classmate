package mangbaam.classmate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import mangbaam.classmate.MyTools.Companion.DAYms
import mangbaam.classmate.R
import mangbaam.classmate.databinding.ItemTodoBinding
import mangbaam.classmate.model.Priority
import mangbaam.classmate.model.SwipeButton
import mangbaam.classmate.model.TodoModel
import java.text.SimpleDateFormat
import java.util.*

class TodoAdapter(
    private val listener: OnClickListener,
    todoList: List<TodoModel>
) :
    RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    private var todos: List<TodoModel> = todoList
    private var viewBinderHelper = ViewBinderHelper()

    inner class ViewHolder(
        private val binding: ItemTodoBinding,
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
                dDayTextView.text = if (item.deadline > 0)
                    "D-${
                        item.deadline.minus(System.currentTimeMillis()).div(DAYms).toInt()
                    }" else "기한 없음"
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
                }
            }
        }
    }

    interface OnClickListener {
        fun onClick(binding: ItemTodoBinding, type: SwipeButton, position: Int)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(todos[position])
        viewBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener
        )
    }

    override fun getItemCount() = todos.size
}
