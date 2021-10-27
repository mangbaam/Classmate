package mangbaam.classmate.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mangbaam.classmate.dao.LectureDao
import mangbaam.classmate.model.Lecture

@Database(entities = [Lecture::class], version = 1)
abstract class TableDB: RoomDatabase() {
    abstract fun tableDao(): LectureDao
}

fun getTableDB(context: Context): TableDB {
    return Room.databaseBuilder(
        context,
        TableDB::class.java,
        "TableDB"
    ).allowMainThreadQueries().build()
}