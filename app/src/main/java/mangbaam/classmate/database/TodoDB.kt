package mangbaam.classmate.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mangbaam.classmate.dao.TodoDao
import mangbaam.classmate.model.TodoModel

@Database(entities = [TodoModel::class], version = 1)
abstract class TodoDB: RoomDatabase() {
    abstract fun todoDao(): TodoDao
}

fun getTodoDB(context: Context): TodoDB {
    return Room.databaseBuilder(
        context,
        TodoDB::class.java,
        "TodoDB"
    ).allowMainThreadQueries().build()
}