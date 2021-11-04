package mangbaam.classmate.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import mangbaam.classmate.BaseActivity
import mangbaam.classmate.Constants.Companion.NOTIFICATION_CHANNEL_ID
import mangbaam.classmate.Constants.Companion.NOTIFICATION_CODE_START
import mangbaam.classmate.Constants.Companion.TAG
import mangbaam.classmate.MyTools
import mangbaam.classmate.MyTools.Companion.DAYms
import mangbaam.classmate.MyTools.Companion.MINUITEms
import mangbaam.classmate.MyTools.Companion.checkMillis
import mangbaam.classmate.MyTools.Companion.getCurrentTime
import mangbaam.classmate.MyTools.Companion.lastTimeMillis
import mangbaam.classmate.PreferenceHelper
import mangbaam.classmate.R
import mangbaam.classmate.database.DB_keys.Companion.ALARM_MINUTE
import mangbaam.classmate.model.AlarmModel
import java.lang.NullPointerException

class NotificationHelper {

    companion object {

        fun createNotificationChannel(context: Context) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val notificationChannel = NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        "수업시작 알림",
                        NotificationManager.IMPORTANCE_HIGH
                    )

                    notificationChannel.description = "수업 시작 전 알림을 띄웁니다."
                    notificationChannel.enableLights(true) // 화면 활성화
                    notificationChannel.vibrationPattern = longArrayOf(0, 300, 300, 300)
                    notificationManager.createNotificationChannel(notificationChannel)
                }
            } catch (nullException: NullPointerException) {
                Toast.makeText(
                    context,
                    "푸시 알림 채널 생성에 실패했습니다. 앱을 재실행하거나 재설치해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
                nullException.printStackTrace()
            }
        }

        fun createNotification(
            context: Context,
            requestCode: Int,
            lectureName: String?
        ) {
            val intent = Intent(context, BaseActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) // 대기열에 있다면 MainActivity 가 아닌 앱 활성화
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            notificationBuilder
                .setSmallIcon(R.drawable.ic_logo)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true) // 클릭시 Notification 제거

            val pendingIntent = PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            notificationBuilder
                .setContentTitle("${lectureName}이(가) 곧 시작합니다")
                .setContentText("수업 준비하세요")
                .setContentIntent(pendingIntent)
            notificationManager.notify(NOTIFICATION_CODE_START, notificationBuilder.build())

            Log.d(TAG, "NotificationHelper - createNotification(${getCurrentTime()}) called")
        }

        fun isNotificationChannelCreated(context: Context): Boolean? {
            return try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    return notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) != null
                }
                true
            } catch (nullException: NullPointerException) {
                Toast.makeText(context, "푸시 알림 기능에 문제가 발생했습니다. 앱을 재실행해주세요.", Toast.LENGTH_SHORT)
                    .show()
                false
            }
        }

        // 알람을 활성화 하지만 AlarmDB에는 Activity/Fragment에서 갱신 필요
        fun activateAllAlarms(context: Context, alarms: List<AlarmModel>) {
            val minutes = PreferenceHelper.getInt(context, ALARM_MINUTE) ?: 30
            alarms.forEach { alarm ->
                registerAlarm(context, alarm, minutes)
            }
        }

        // 알람을 비활성화 하지만 AlarmDB 에는 Activity/Fragment 에서 갱신 필요
        fun removeAllAlarms(context:Context, alarms: List<AlarmModel>) {
            alarms.forEach { alarm ->
                removeAlarm(context, alarm)
            }
        }

        // 알람을 등록해도 AlarmDB 에는 Activity/Fragment 에서 추가 필요
        private fun registerAlarm(context: Context, item: AlarmModel, previousMinutes: Int) {
            Log.d(TAG, "[${item.id}] ${item.name} - ${item.hour}:${item.minute} ${previousMinutes}분 전 알람 생성")
            val lectureName = item.name
            val place = item.place
            val hour = item.hour
            val minute = item.minute
            val lastMillis = lastTimeMillis(item.weekDay, hour, minute)
            var lastMillisToStart = lastMillis - previousMinutes * MINUITEms
            if (lastMillisToStart < 0) {
                lastMillisToStart += DAYms * 7
            }
            Log.d(
                MyTools.TAG,
                "-> 강의 시간(${item.weekDay}요일 ${hour}:${minute})까지 ${checkMillis(lastMillis)}만큼 남음. ${previousMinutes}분 전 알림"
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra("id", item.id)
            intent.putExtra("lectureName", lectureName)
            intent.putExtra("place", place)
            intent.putExtra("time", "${hour}시 ${minute}분")

            val pendingIntent = PendingIntent.getBroadcast(
                context, item.id, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                lastMillisToStart,
                pendingIntent
            )
            Log.d(
                MyTools.TAG,
                "$lectureName 알람 ${item.weekDay}요일 ${checkMillis(lastMillisToStart)}후에 울리도록 등록됨"
            )
        }

        fun checkAlarm(context: Context, alarmModel: AlarmModel): AlarmModel {
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmModel.id,
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE
            )
            if ((pendingIntent == null) and alarmModel.onOff) {
                // 알람은 꺼져있는데 데이터는 켜져있는 경우 -> 데이터 수정
                alarmModel.onOff = false

            } else if ((pendingIntent != null) and alarmModel.onOff.not()) {
                // 알람은 켜져있는데 데이터는 꺼진 경우 -> 알람을 취소
                pendingIntent.cancel()
            }
            return alarmModel
        }

        private fun removeAlarm(context: Context, alarmModel: AlarmModel) {
            Log.d(TAG, "알람 제거 -> [${alarmModel.id}] ${alarmModel.name}")
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmModel.id,
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE
            )
            pendingIntent?.cancel()
        }
    }
}