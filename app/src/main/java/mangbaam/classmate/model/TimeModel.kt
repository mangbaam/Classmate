package mangbaam.classmate.model

data class TimeModel(
    val hour: Int,
    val minute: Int
) {
    val timeText: String
        get() {
            val h = "%02d".format(hour)
            val m = "%02d".format(minute)
            return "$h:$m"
        }
}
