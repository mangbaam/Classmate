package mangbaam.classmate.model

data class TimeAndPlace(
    var dayOfWeek: String,
    var startHour: Int,
    var startMinute: Int,
    var endHour: Int,
    var endMinute: Int,
    var place: String
)