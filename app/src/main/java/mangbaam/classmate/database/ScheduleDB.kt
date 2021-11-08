package mangbaam.classmate.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mangbaam.classmate.dao.ScheduleDao
import mangbaam.classmate.model.ScheduleModel

@Database(entities = [ScheduleModel::class], version = 1)
abstract class ScheduleDB: RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}

fun getScheduleDB(context: Context): ScheduleDB {
    return Room.databaseBuilder(
        context,
        ScheduleDB::class.java,
        "ScheduleDB"
    ).allowMainThreadQueries().build()
}