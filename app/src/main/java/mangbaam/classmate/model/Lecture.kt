package mangbaam.classmate.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lectureTable")
data class Lecture(
    @ColumnInfo(name = "lectureName") var name: String,
    var point: String,
    var timeAndPlace: String?,
    var professor: String?,
    var classify: String?,
    var electives: String?,
    var department: String?,
    var link: String?,
    @PrimaryKey(autoGenerate = true) val id: Long
) {
    operator fun set(key: String, value: Any?) {
        when (key) {
            "point" -> this.point = value.toString()
            "timeAndPlace" -> this.timeAndPlace = value.toString()
            "professor" -> this.professor = value.toString()
            "classify" -> this.professor = value.toString()
            "electives" -> this.electives = value.toString()
            "department" -> this.department = value.toString()
            "link" -> this.link = value.toString()
        }
    }

    constructor(): this("", "", "", "", "", "", "", "", 0)
}