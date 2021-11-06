package mangbaam.classmate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import mangbaam.classmate.MyTools.Companion.DAYms
import mangbaam.classmate.R
import mangbaam.classmate.databinding.ItemTodoBinding
import mangbaam.classmate.model.Priority
import mangbaam.classmate.model.SwipeButton
import mangbaam.classmate.model.TodoModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TodoAdapter(
    val context: Context,
    private val listener: OnClickListener,
    todoList: ArrayList<TodoModel>
) :
    RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    private var todos = todoList

    inner class ViewHolder(
        private val binding: ItemTodoBinding,
        private val listener: OnClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(item: TodoModel) {
            val sdFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            with(binding) {
                todoTitleTextView.text = item.title
                categoryTextView.text = todos[item.category].toString()
                deadlineTextView.text = sdFormat.format(item.deadline)
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
            }
        }

        override fun onClick(v: View?) {
            with(binding) {
                when (v) {
                    editButton -> listener.onClick(this, SwipeButton.EDIT)
                    completeButton -> listener.onClick(this, SwipeButton.COMPLETE)
                }
            }
        }
    }

    interface OnClickListener {
        fun onClick(binding: ItemTodoBinding, type: SwipeButton)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(todos[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener
        )
    }

    override fun getItemCount() = todos.size
}
