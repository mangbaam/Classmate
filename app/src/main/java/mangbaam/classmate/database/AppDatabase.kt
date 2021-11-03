package mangbaam.classmate.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mangbaam.classmate.dao.AlarmDao
import mangbaam.classmate.dao.LectureDao
import mangbaam.classmate.model.AlarmModel
import mangbaam.classmate.model.Lecture

@Database(entities = [Lecture::class, AlarmModel::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun lectureDao(): LectureDao
    abstract fun alarmDao(): AlarmDao
}

fun getAppDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "ClassmateDB"
    ).build()
}