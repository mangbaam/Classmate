package mangbaam.classmate

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import mangbaam.classmate.model.Lecture
import mangbaam.classmate.model.ScheduleModel
import mangbaam.classmate.model.TimeAndPlace
import mangbaam.classmate.notification.AlarmReceiver
import mangbaam.classmate.notification.NotificationHelper
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale


class MyTools {

    companion object {
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
                    var startIndex = 0
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
            return result // [[장소, 요일, 시작시간, 종료시작], [...], ...] 의 형태
        }

        fun timeAndPlaceToLecture(
            lectureName: String,
            professor: String,
            tapList: List<TimeAndPlace>
        ): Lecture {
            val tapBlocks = mutableListOf<List<String>>()
            tapList.forEach { tapInfo ->
                tapBlocks.add(
                    listOf(
                        tapInfo.place,
                        tapInfo.dayOfWeek,
                        tapInfo.startTime,
                        tapInfo.endTime
                    )
                )
            }

            val placeGroup = mutableMapOf<String, MutableList<List<String>>>()
            tapBlocks.forEach {
                if (placeGroup.containsKey(it[0])) {
                    placeGroup[it[0]]?.add(it.subList(1, 4))
                } else {
                    placeGroup[it[0]] = mutableListOf(it.subList(1, 4))
                }
            }

            val resultList = mutableListOf<String>()

            placeGroup.forEach { (place, timeList) ->
                val dayGroup = mutableMapOf<String, MutableList<Int>>()
                timeList.forEach { timeInfo ->
                    val day = timeInfo[0]
                    val startTime = timeInfo[1].split(":")[0].toInt() - 8
                    val endTime = timeInfo[2].split(":")[0].toInt() - 9

                    if (dayGroup.containsKey(day)) {
                        dayGroup[day]?.addAll(startTime..endTime)
                    } else {
                        dayGroup[day] = (startTime..endTime).toMutableList()
                    }
                }
                val placeGroupStringList = mutableListOf<String>()
                dayGroup.forEach { (day, times) ->
                    placeGroupStringList.add("$day${times.joinToString(separator = ",")}")
                }
                val placeGroupString = "$place${
                    placeGroupStringList.joinToString(
                        prefix = "(",
                        postfix = ")",
                        separator = " "
                    )
                }"
                resultList.add(placeGroupString)
            }
            val timeAndPlaceValue = resultList.joinToString(separator = ",")
            Log.d(TAG, "MyTools - $timeAndPlaceValue")

            val resultLecture = Lecture(0, "", "", "", "", "", "", "", "")
            resultLecture["name"] = lectureName
            resultLecture["timeAndPlace"] = timeAndPlaceValue
            resultLecture["professor"] = professor

            Log.d(TAG, "timeAndPlaceToLecture - $resultLecture 추가")
            return resultLecture
        }

        fun findLectureById(id: Int, lectureList: Array<Lecture>): Lecture {
            for (item in lectureList) {
                if (item.id == id) {
                    return item
                }
            }
            Log.d(TAG, "MyTools - id가 ${id}인 강의를 발견하지 못했습니다.")
            return Lecture()
        }

        fun lastTimeMillis(targetWeekday: String, targetHour: Int, targetMinute: Int): Long {
            val format = SimpleDateFormat("EE HH mm", Locale.KOREA)
            val today = format.format(Date(System.currentTimeMillis())) // 현재 시간 (요일 시간 분)

            val dateInfo = today.split(" ")
            val dayOfWeek = dateInfo[0] // 요일
            val currentHour = dateInfo[1].toInt() // 시
            val currentMinute = dateInfo[2].toInt() // 분
            Log.d(TAG, "현재 : ${dayOfWeek}요일 $currentHour : $currentMinute")
            val dayDiffMs = weekDayDiff(dayOfWeek, targetWeekday) * DAYms
            val timeDiffMs = timeDiff(currentHour, currentMinute, targetHour, targetMinute)
            return dayDiffMs + timeDiffMs
        }

        fun lastTimeMillis(targetWeekday: Int, targetHour: Int, targetMinute: Int): Long {
            val format = SimpleDateFormat("EE HH mm", Locale.KOREA)
            val today = format.format(Date(SystemClock.elapsedRealtime())) // 현재 시간 (요일 시간 분)

            val dateInfo = today.split(" ")
            val dayOfWeek = dateInfo[0] // 요일
            val currentHour = dateInfo[1].toInt() // 시
            val currentMinute = dateInfo[2].toInt() // 분
            Log.d(TAG, "현재 : ${dayOfWeek}요일 $currentHour : $currentMinute")
            val dayDiffMs = weekDayDiff(dayOfWeek, targetWeekday) * DAYms
            val timeDiffMs = timeDiff(currentHour, currentMinute, targetHour, targetMinute)
            return dayDiffMs + timeDiffMs
        }

        private fun weekDayDiff(from: String, to: String): Int {
            val days = listOf("월", "화", "수", "목", "금", "토", "일")
            return (days.indexOf(to) - days.indexOf(from) + 7) % 7
        }

        private fun weekDayDiff(from: String, to: Int): Int = weekDayDiff(from, getWeekDay(to)+7) % 7

        private fun timeDiff(fromHour: Int, fromMinute: Int, toHour: Int, toMinute: Int): Long {
            val fHour = fromHour * HOURms
            val fMinute = fromMinute * MINUITEms
            val tHour = toHour * HOURms
            val tMinute = toMinute * MINUITEms
            return (tHour + tMinute) - (fHour + fMinute).toLong()
        }

        fun checkMillis(millis: Long): String {
            /* 밀리초 검증 */
            var lastMillis = millis
            val days = lastMillis / DAYms
            lastMillis %= DAYms
            val hours = lastMillis / HOURms
            lastMillis %= HOURms
            val minutes = lastMillis / MINUITEms
            lastMillis %= MINUITEms
            val seconds = lastMillis / 1000
            lastMillis %= 1000
            return "${days}일 ${hours}시간 ${minutes}분 ${seconds}초"
        }

        fun getCurrentTime(): String {
            val format = SimpleDateFormat("yy/MM/DD EE요일 HH:mm:ss", Locale.KOREA)
            return format.format(Date(SystemClock.elapsedRealtime())) // 현재 시간 (O요일 시간:분:초)
        }

        fun getWeekDay(index: Int): String = arrayOf("월","화","수","목","금","토","일")[index]

        // 상수 값들
        const val TAG: String = "로그"
        const val MINUITEms = 60 * 1000
        const val HOURms = 60 * 60 * 1000
        const val DAYms = 24 * 60 * 60 * 1000
    }
}