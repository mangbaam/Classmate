package mangbaam.classmate

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mangbaam.classmate.dao.LectureDao
import mangbaam.classmate.model.Lecture
import mangbaam.classmate.model.LectureData

@Database(entities = [Lecture::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun lectureDao(): LectureDao
}

fun getAppDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "ClassmateDB"
    ).build()
}