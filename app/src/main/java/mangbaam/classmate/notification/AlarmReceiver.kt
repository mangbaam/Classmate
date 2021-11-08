package mangbaam.classmate.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import mangbaam.classmate.Constants.Companion.TAG
import mangbaam.classmate.model.AlarmModel
import mangbaam.classmate.notification.NotificationHelper.Companion.createNotification
import mangbaam.classmate.notification.NotificationHelper.Companion.registerAlarm

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "AlarmReceiver - onReceive() called")
        val id = intent.getIntExtra("id", 0)
        val lectureName = intent.getStringExtra("lectureName")
        val hour = intent.getIntExtra("hour", 12)
        val minute = intent.getIntExtra("minute", 30)

        createNotification(context, id, lectureName, "${hour}:${minute}")
    }
}