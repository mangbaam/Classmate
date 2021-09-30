package mangbaam.classmate

import androidx.room.Database
import androidx.room.RoomDatabase
import mangbaam.classmate.dao.LectureDao
import mangbaam.classmate.model.Lecture
import mangbaam.classmate.model.LectureData

@Database(entities = [LectureData::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun lectureDao(): LectureDao
}