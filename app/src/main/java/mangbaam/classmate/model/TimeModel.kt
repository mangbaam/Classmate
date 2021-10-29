package mangbaam.classmate.model

data class TimeModel(
    val time: String
) {
    val timeText: String
        get() {
            val tmpList = time.split(":")
            val hour = tmpList[0].toInt(); val minute = tmpList[1].toInt()
            val h = "%02d".format(hour)
            val m = "%02d".format(minute)
            return "$h:$m"
        }
}
