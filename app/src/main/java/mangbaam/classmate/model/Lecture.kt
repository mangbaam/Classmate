package mangbaam.classmate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Lecture(
    val id: Int,
    val name: String,
    val time: String?,
    val place: String?,
    val professor: String?,
    val classify: String?
): Parcelable