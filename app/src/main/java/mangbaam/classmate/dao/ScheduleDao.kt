package mangbaam.classmate.dao

import androidx.room.*
import com.islandparadise14.mintable.model.ScheduleEntity
import mangbaam.classmate.model.ScheduleModel

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM ScheduleModel")
    fun getAll(): Array<ScheduleModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg schedule: ScheduleModel)

    @Delete
    fun delete(schedule: ScheduleModel)

    @Query("DELETE FROM ScheduleModel WHERE originId=:origin")
    fun delete(origin: Int)

    @Query("DELETE FROM ScheduleModel")
    fun clear()

    @Query("SELECT COUNT(*) FROM ScheduleModel")
    fun getSize(): Int
}