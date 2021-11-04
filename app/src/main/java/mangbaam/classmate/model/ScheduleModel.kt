package mangbaam.classmate.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ScheduleModel(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var originId: Int,
    var scheduleName: String,
    var roomInfo: String,
    var scheduleDay: Int,
    var startTime: String,
    var endTime: String,
    var backgroundColor: String = "#dddddd",
    var textColor: String = "#ffffff"
)