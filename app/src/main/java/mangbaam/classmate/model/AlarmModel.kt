package mangbaam.classmate.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlarmModel(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val originId: Int,
    val name: String,
    val place: String,
    val weekDay: String,
    val hour: Int,
    val minute: Int,
    var onOff: Boolean
)