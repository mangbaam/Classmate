package mangbaam.classmate.model

data class Lecture(
    val id: Int,
    val name: String,
    val time: String?,
    val place: String?,
    val professor: String?,
    val classify: String?
)