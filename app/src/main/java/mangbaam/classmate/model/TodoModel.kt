package mangbaam.classmate.model

import androidx.room.Entity

@Entity
data class TodoModel(
    var id: Long, // 생성 시간 ms
    var priority: Priority,
    var title: String,
    var category: Int?,
    var content: String?,
    var deadline: String?,
    var d_day: Int?
) {
    constructor() : this(
        System.currentTimeMillis(),
        Priority.LOW,
        "",
        0,
        "",
        "",
        0
    )
}