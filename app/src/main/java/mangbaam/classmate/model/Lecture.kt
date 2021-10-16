package mangbaam.classmate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Lecture(
    val id: String, // 과목 코드
    val name: String, // 과목명
    val point: Double?, // 학점
    var timeAndPlace: List<HashMap<String, String>>?, // 시간 및 장소
    var professor: String?, // 교수자
    var classify: String?, // 분류
    var department: String?, // 부서
    var targetGrade: Long? // 개설 학년
    /*
    * 이수구분 o
    * 시간표 //
    * 과목 코드 id
    * 과목명 name
    * 교수자 professor
    * 개설 부서 department
    * 학점 point
    * 개설 학년 targetGrade
    * */
): Parcelable {
    operator fun set(columnName: String?, value: Any?) {
        when (columnName) {
            "timeAndPlace" -> timeAndPlace = value as List<HashMap<String, String>>?
            "professor" -> professor = value as String?
            "classify" -> classify = value as String?
            "department" -> department = value as String?
            "targetGrade" -> targetGrade = value as Long?
        }
    }
}