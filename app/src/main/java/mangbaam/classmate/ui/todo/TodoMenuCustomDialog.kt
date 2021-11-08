package mangbaam.classmate.ui.todo

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.RadioButton
import kotlinx.android.synthetic.main.dialog_todo_menu.*
import mangbaam.classmate.Constants.Companion.TAG
import mangbaam.classmate.PreferenceHelper
import mangbaam.classmate.PreferenceHelper.Companion.DEFAULT_INT_VALUE
import mangbaam.classmate.R
import mangbaam.classmate.database.DB_keys.Companion.CHECKED_SORT_BY_ID
import mangbaam.classmate.database.DB_keys.Companion.CHECKED_SORT_ORDER_ID
import mangbaam.classmate.database.DB_keys.Companion.IS_DISPLAY_COMPLETED
import java.lang.NullPointerException

class TodoMenuCustomDialog(context: Context, todoMenuInterface: TodoMenuInterface) :
    Dialog(context) {
    private var todoMenuInterface: TodoMenuInterface? = null
    private val checkedSortById by lazy { PreferenceHelper.getInt(context, CHECKED_SORT_BY_ID) }
    private val checkedOrderById by lazy { PreferenceHelper.getInt(context, CHECKED_SORT_ORDER_ID) }
    private val isDisplayCompletedChecked by lazy {
        PreferenceHelper.getBoolean(
            context,
            IS_DISPLAY_COMPLETED
        )
    }

    init {
        this.todoMenuInterface = todoMenuInterface
        this.setTitle("Options")
        this.setCanceledOnTouchOutside(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_todo_menu)

        /* 설정값 세팅 */
        try {
            if (checkedSortById != DEFAULT_INT_VALUE) findViewById<RadioButton>(checkedSortById!!).isChecked =
                true
            if (checkedOrderById != DEFAULT_INT_VALUE) findViewById<RadioButton>(checkedOrderById!!).isChecked =
                true
        } catch (exception: NullPointerException) {
            Log.e(TAG, "라디오 버튼 초기화 NullPointerException 발생: $exception")
        }
        completedTodoCheckbox.isChecked = isDisplayCompletedChecked

        completedTodoCheckbox.setOnClickListener {
            if (it is CheckBox) {
                val checked: Boolean = it.isChecked
                Log.d(TAG, "TodoMenuCustomDialog - 목록에 표시 체크됨: $checked")
            }
        }

        applyButton.setOnClickListener {
            this.todoMenuInterface?.onApplyButtonClicked()
        }

        cancelButton.setOnClickListener {
            Log.d(TAG, "취소 버튼 클릭")
            try {
                if (checkedSortById != DEFAULT_INT_VALUE) findViewById<RadioButton>(checkedSortById!!).isChecked =
                    true
                if (checkedOrderById != DEFAULT_INT_VALUE) findViewById<RadioButton>(
                    checkedOrderById!!
                ).isChecked = true
            } catch (exception: NullPointerException) {
                Log.e(TAG, "라디오 버튼 초기화 NullPointerException 발생: $exception")
            }
            this.dismiss()
        }
    }
}