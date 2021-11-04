package mangbaam.classmate.dao

import androidx.room.*
import mangbaam.classmate.model.AlarmModel

@Dao
interface AlarmDao {
    @Query("SELECT * FROM AlarmModel")
    fun getAll(): List<AlarmModel>

    @Delete
    fun delete(model: AlarmModel)

    @Query("DELETE FROM AlarmModel WHERE originId=:id")
    fun delete(id: Int)

    @Query("DELETE FROM AlarmModel")
    fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg model: AlarmModel)

    @Update
    fun update(vararg model: AlarmModel)
}