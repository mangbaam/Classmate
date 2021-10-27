package mangbaam.classmate.dao

import androidx.room.*
import mangbaam.classmate.model.Lecture

@Dao
interface LectureDao {
    @Query("SELECT * FROM lectureTable")
    fun getAll(): Array<Lecture>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLecture(vararg lecture: Lecture)

    @Delete
    fun deleteLecture(lecture: Lecture)

    @Query("DELETE FROM lectureTable")
    fun clear()

    @Query("SELECT COUNT(*) FROM LectureTable")
    fun getSize(): Int
}