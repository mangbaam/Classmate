package mangbaam.classmate.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import mangbaam.classmate.model.LectureData

@Dao
interface LectureDao {
    @Query("SELECT * FROM LectureData")
    fun getAll(): List<LectureData>

    @Insert
    fun insertLecture(lecture: LectureData)

    @Delete
    fun deleteLecture(lecture: LectureData)
}