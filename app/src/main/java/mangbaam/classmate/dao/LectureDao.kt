package mangbaam.classmate.dao

import androidx.room.*
import mangbaam.classmate.model.Lecture

@Dao
interface LectureDao {
    @Query("SELECT * FROM lectureTable")
    fun getAll(): Array<Lecture>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLecture(vararg lecture: Lecture)

    @Delete
    fun deleteLecture(lecture: Lecture)

    @Query("SELECT * FROM lectureTable WHERE lectureName LIKE '%'+:keyword+'%'")
    fun search(keyword: String): List<Lecture>

    @Query("DELETE FROM lectureTable")
    fun clear()
}