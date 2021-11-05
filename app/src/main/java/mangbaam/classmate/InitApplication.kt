package mangbaam.classmate

import android.app.Application
import android.util.Log
import mangbaam.classmate.Constants.Companion.TAG
import mangbaam.classmate.notification.NotificationHelper

class InitApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "InitApplication - onCreate() called")
        NotificationHelper.createNotificationChannel(applicationContext)
    }
}