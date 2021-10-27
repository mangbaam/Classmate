package mangbaam.classmate.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "lectureTable")
data class Lecture(
    @ColumnInfo(name = "lectureName") var name: String,
    var point: String,
    var timeAndPlace: String,
    var professor: String,
    var classify: String?,
    var electives: String?,
    var department: String?,
    var link: String?,
    @PrimaryKey(autoGenerate = true) val id: Long
): Serializable {
    operator fun set(key: String, value: Any?) {
        when (key) {
            "name" -> this.name = value.toString()
            "point" -> this.point = value.toString()
            "timeAndPlace" -> this.timeAndPlace = value.toString()
            "professor" -> this.professor = value.toString()
            "classify" -> this.classify = value.toString()
            "electives" -> this.electives = value.toString()
            "department" -> this.department = value.toString()
            "link" -> this.link = value.toString()
        }
    }

    constructor(): this("", "", "", "", "", "", "", "", 0)
}