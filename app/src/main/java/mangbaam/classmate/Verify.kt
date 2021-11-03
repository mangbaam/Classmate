package mangbaam.classmate

import com.islandparadise14.mintable.model.ScheduleDay
import com.islandparadise14.mintable.model.ScheduleEntity
import mangbaam.classmate.MyTools.Companion.parseTimeAndPlace
import mangbaam.classmate.model.Lecture

class Verify {

    companion object {
        private fun timeToMinute(time: String): Int {
            val tmpList = time.split(":")
            return tmpList[0].toInt() * 60 + tmpList[1].toInt()
        }

        fun verifyTime(schedules: List<ScheduleEntity>, newLecture: ScheduleEntity): Boolean {
            schedules.forEach {
                if (it.scheduleDay == newLecture.scheduleDay) {
                    val originStartTime = timeToMinute(it.startTime)
                    val originEndTime = timeToMinute(it.endTime)
                    val newStartTime = timeToMinute(newLecture.startTime)
                    val newEndTime = timeToMinute(newLecture.startTime)

                    if (newStartTime in originStartTime..originEndTime ||
                        newEndTime in originStartTime..originEndTime ||
                        (originStartTime in newStartTime..newEndTime) && (originEndTime in newStartTime..newEndTime)
                    ) {
                        return false
                    }
                }
            }
            return true
        }

        fun verifyTime(schedules: List<ScheduleEntity>, newLecture: Lecture): Boolean {
            val lectureInfoList = parseTimeAndPlace(newLecture.timeAndPlace)
            lectureInfoList.forEach { new ->
                val newStartTime = timeToMinute(new[2])
                val newEndTime = timeToMinute(new[3])
                schedules.forEach {
                    if (it.scheduleDay == ScheduleDay.getDay(new[1])) {
                        val originStartTime = timeToMinute(it.startTime)
                        val originEndTime = timeToMinute(it.endTime)
                        if (newStartTime in originStartTime..originEndTime ||
                            newEndTime in originStartTime..originEndTime ||
                            (originStartTime in newStartTime..newEndTime) && (originEndTime in newStartTime..newEndTime)
                        ) {
                            return false
                        }
                    }
                }
            }
            return true
        }

        fun verifyTime(originLectures: Array<Lecture>, newLecture: Lecture): Boolean {
            val newLectureList = parseTimeAndPlace(newLecture.timeAndPlace)
            newLectureList.forEach { new ->
                val newStartTime = timeToMinute(new[2])
                val newEndTime = timeToMinute(new[3])

                originLectures.forEach { originLecture ->
                    val originData = parseTimeAndPlace(originLecture.timeAndPlace)
                    originData.forEach { origin ->
                        if (ScheduleDay.getDay(origin[1]) == ScheduleDay.getDay(new[1])) {
                            val originStartTime = timeToMinute(origin[2])
                            val originEndTime = timeToMinute(origin[3])

                            if (newStartTime in originStartTime..originEndTime ||
                                newEndTime in originStartTime..originEndTime ||
                                (originStartTime in newStartTime..newEndTime) && (originEndTime in newStartTime..newEndTime)
                            ) {
                                return false
                            }
                        }
                    }
                }
            }

            return true
        }

        private fun ScheduleDay.getDay(day: String): Int {
            return when (day.uppercase()) {
                "월", "월요일", "MONDAY", "MON" -> MONDAY
                "화", "화요일", "TUESDAY", "TUE" -> TUESDAY
                "수", "수요일", "WEDNESDAY", "WED" -> WEDNESDAY
                "목", "목요일", "THURSDAY", "THU" -> THURSDAY
                "금", "금요일", "FRIDAY", "FRI" -> FRIDAY
                "토", "토요일", "SATURDAY", "SAT" -> SATURDAY
                else -> 9999
            }
        }

    }
}