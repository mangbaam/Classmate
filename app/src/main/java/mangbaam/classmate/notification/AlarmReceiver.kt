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
        val model = intent.getSerializableExtra("model") as AlarmModel

        val id = model.id
        val lectureName = model.name
        val hour = model.hour
        val minute = model.minute

        Log.d(TAG, "AlarmReceiver - onReceive() called")
        createNotification(context, id, lectureName, "${hour}:${minute}")
//        registerAlarm(context, model)
    }
}