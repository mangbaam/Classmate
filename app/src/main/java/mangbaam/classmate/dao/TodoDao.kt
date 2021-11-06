package mangbaam.classmate.dao

import androidx.room.*
import mangbaam.classmate.model.AlarmModel
import mangbaam.classmate.model.TodoModel

@Dao
interface TodoDao {
    @Query("SELECT * FROM AlarmModel")
    fun getAll(): ArrayList<TodoModel>

    @Delete
    fun delete(model: TodoModel)

    @Delete
    fun delete(id: Long)

    @Query("DELETE FROM TodoModel")
    fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg model: TodoModel)

    @Update
    fun update(vararg model: TodoModel)
}