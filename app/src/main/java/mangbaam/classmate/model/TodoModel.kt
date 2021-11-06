package mangbaam.classmate.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class TodoModel(
    @PrimaryKey var id: Long, // 생성 시간 ms
    var priority: Priority,
    var title: String,
    var category: Int, // 과목의 id
    var categoryName: String, // 과목명
    var detail: String,
    var deadline: Long // ms
):Serializable {
    constructor() : this(
        System.currentTimeMillis(),
        Priority.LOW,
        "",
        0,
        "",
        "",
        0L
    )
}