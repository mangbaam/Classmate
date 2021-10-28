package mangbaam.classmate

class MyTools {
    fun parseTimeAndPlace(timeAndPlace: String): List<List<String>> {
        val result = mutableListOf<List<String>>()
        if (timeAndPlace.isEmpty()) return listOf(emptyList())
        // 1. 장소 분리
        val myData = timeAndPlace.dropLast(1).split("),")
        for (tmpData in myData) {
            // 2. 장소와 시간 분리
            val tmp = tmpData.split("(")
            val place = tmp[0] // 장소
            val dayAndTime = tmp[1] // 여러 요일, 교시 데이터
            // 3. 여러 요일 분리
            val days = dayAndTime.split(" ")
            for (day in days) {
                // 4.1 요일과 교시 분리
                val dayOfWeek = day[0].toString() // 요일
                val timesOfDay = day.substring(1) // 교시
                val timeList = timesOfDay.split(",") // 교시 분리 3,4,7 -> [3, 4, 7]
                // 4.2 연속되지 않는 교시 분리
                var startIndex = 0;
                var index = 1
                val timeInfos = mutableListOf<List<String>>()
                while (index < timeList.size) {
                    if (timeList[index - 1].toInt() + 1 != timeList[index].toInt()) {
                        println("${timeList[index - 1].toInt() + 1} != ${timeList[index]}")
                        timeInfos.add(listOf(timeList[startIndex], timeList[index - 1]))
                        startIndex = index
                    }
                    index++
                }
                timeInfos.add(listOf(timeList[startIndex], timeList[index - 1]))
                // 4.3 교시 -> 시간 데이터로 변경하여 저장
                for (info in timeInfos) {
                    val startTime = "${info[0].toInt() + 8}:30"
                    val endTime = "${info[1].toInt() + 9}:20"
                    // 4.4 [[장소, 요일, 시작시간, 종료시작], [...], ...] 의 형태로 저장
                    result.add(listOf(place, dayOfWeek, startTime, endTime))
                }
            }
        }
        return result
    }
}