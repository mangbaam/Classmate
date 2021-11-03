package mangbaam.classmate.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlarmModel(
    @PrimaryKey val id: Int,
    val weekDay: String,
    val hour: Int,
    val minute: Int,
    var onOff: Boolean
)