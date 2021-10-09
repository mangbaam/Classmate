package mangbaam.classmate

import mangbaam.classmate.model.Lecture

interface OnLectureItemClick {
    fun onLectureClicked(item: Lecture)
}