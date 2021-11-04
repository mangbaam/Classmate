package mangbaam.classmate.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import mangbaam.classmate.Constants.Companion.TAG
import mangbaam.classmate.notification.NotificationHelper.Companion.createNotification

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "AlarmReceiver - onReceive() called")
        val id = intent?.getIntExtra("id", 0)
        val lectureName = intent?.getStringExtra("lectureName")
        val time = intent?.getStringExtra("time")
        val place = intent?.getStringExtra("place")
        if ((context != null) and (id != null)) {
            createNotification(context!!, id!!, lectureName)
        }
    }
}