package mangbaam.classmate

import androidx.room.Database
import androidx.room.RoomDatabase
import mangbaam.classmate.dao.LectureDao
import mangbaam.classmate.model.Lecture

@Database(entities = [Lecture::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun lectureDao(): LectureDao
}