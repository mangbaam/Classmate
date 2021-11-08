package mangbaam.classmate.ui.todo

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.list_todo_category.*
import mangbaam.classmate.R

class TodoCategoryDialog(context: Context, categoryList: List<String>, todoCategoryInterface: TodoCategoryInterface): Dialog(context) {
    private var todoCategoryInterface: TodoCategoryInterface? = null
    private var categoryArray: Array<String>? = null
    init {
        this.todoCategoryInterface = todoCategoryInterface
        categoryList.forEach {
            categoryArray?.plus(it)
        }
        setTitle("카테고리 선택")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_todo_category)
        val listView = findViewById<ListView>(R.id.categoryListView)
//        val adapter = ArrayAdapter<String>(context, R.layout.list_todo_category, categoryArray)

    }
}