package mangbaam.classmate.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import mangbaam.classmate.Constants.Companion.ALARM_BEFORE_LECTURE
import mangbaam.classmate.notification.NotificationHelper.Companion.createNotification

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "AlarmReceiver - onReceive() called")
        val lectureName = intent?.getStringExtra("lectureName")
        val time = intent?.getStringExtra("time")
        val place = intent?.getStringExtra("place")
        if (context != null) {
            createNotification(context, ALARM_BEFORE_LECTURE, lectureName, place)
        }
    }

    companion object {
        const val TAG: String = "로그"
    }
}