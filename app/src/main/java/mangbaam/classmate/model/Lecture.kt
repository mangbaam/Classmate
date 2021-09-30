package mangbaam.classmate.model

import java.io.Serializable

data class Lecture(
    val id: Int,
    val name: String,
    val time: String?,
    val place: String?,
    val professor: String?,
    val classify: String?
): Serializable