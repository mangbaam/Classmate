package mangbaam.classmate

import android.app.Application
import android.util.Log
import mangbaam.classmate.notification.NotificationHelper

class InitApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "InitApplication - onCreate() called")
        NotificationHelper.createNotificationChannel(applicationContext)
    }

    companion object {
        const val TAG: String = "로그"
    }
}