package mangbaam.classmate.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LectureData (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "lectureName") val name: String,
    @ColumnInfo(name = "place") val place: String?,
    @ColumnInfo(name = "time") val time: String?,
    @ColumnInfo(name = "professor") val professor: String?,
    @ColumnInfo(name = "classify") val classify: String?,
    @ColumnInfo(name = "link") val link: String?
)