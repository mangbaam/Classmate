package mangbaam.classmate.ui.timetable

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimeTableViewModel: ViewModel() {

    private var _currentTimeTableText = MutableLiveData("2021_2")
    val currentTimeTableText: MutableLiveData<String>
        get() = _currentTimeTableText

    init {
        currentTimeTableText.value = "2021_2"
    }

    override fun onCleared() {
        super.onCleared()
    }

}