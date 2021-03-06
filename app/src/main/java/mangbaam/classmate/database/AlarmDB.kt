package mangbaam.classmate.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mangbaam.classmate.dao.AlarmDao
import mangbaam.classmate.model.AlarmModel

@Database(entities = [AlarmModel::class], version = 1)
abstract class AlarmDB: RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}

fun getAlarmDB(context: Context): AlarmDB {
    return Room.databaseBuilder(
        context,
        AlarmDB::class.java,
        "AlarmDB"
    ).allowMainThreadQueries().build()
}