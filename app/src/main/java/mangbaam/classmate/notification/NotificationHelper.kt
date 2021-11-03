package mangbaam.classmate.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import mangbaam.classmate.BaseActivity
import mangbaam.classmate.Constants.Companion.ALARM_AFTER_LECTURE
import mangbaam.classmate.Constants.Companion.ALARM_BEFORE_LECTURE
import mangbaam.classmate.Constants.Companion.NOTIFICATION_CHANNEL_ID
import mangbaam.classmate.Constants.Companion.NOTIFICATION_CODE_END
import mangbaam.classmate.Constants.Companion.NOTIFICATION_CODE_START
import mangbaam.classmate.Constants.Companion.TAG
import mangbaam.classmate.MyTools
import mangbaam.classmate.MyTools.Companion.DAYms
import mangbaam.classmate.MyTools.Companion.checkMillis
import mangbaam.classmate.MyTools.Companion.getCurrentTime
import mangbaam.classmate.MyTools.Companion.lastTimeMillis
import mangbaam.classmate.MyTools.Companion.parseTimeAndPlace
import mangbaam.classmate.R
import mangbaam.classmate.model.AlarmModel
import mangbaam.classmate.model.Lecture
import java.lang.NullPointerException

class NotificationHelper {

    companion object {

        fun createNotificationChannel(context: Context) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "수업시작 알림", NotificationManager.IMPORTANCE_HIGH)

                    notificationChannel.description = "수업 시작 전 알림을 띄웁니다."
                    notificationChannel.enableLights(true) // 화면 활성화
                    notificationChannel.vibrationPattern = longArrayOf(0, 300, 300, 300)
                    notificationManager.createNotificationChannel(notificationChannel)
                }
            } catch (nullException: NullPointerException) {
                Toast.makeText(context, "푸시 알림 채널 생성에 실패했습니다. 앱을 재실행하거나 재설치해주세요.", Toast.LENGTH_SHORT).show()
                nullException.printStackTrace()
            }
        }

        fun createNotification(context: Context, workName: String, lectureName: String?, place: String?) {
            val intent = Intent(context, BaseActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) // 대기열에 있다면 MainActivity가 아닌 앱 활성화
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            notificationBuilder
                .setSmallIcon(R.drawable.ic_logo)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true) // 클릭시 Notification 제거

            if (workName == ALARM_BEFORE_LECTURE) {
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    NOTIFICATION_CODE_START,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
                notificationBuilder
                    .setContentTitle("${lectureName}이(가) 곧 시작합니다")
                    .setContentText("수업 준비하세요")
                    .setContentIntent(pendingIntent)
                notificationManager.notify(NOTIFICATION_CODE_START, notificationBuilder.build())
            } else if (workName == ALARM_AFTER_LECTURE) {
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    NOTIFICATION_CODE_END,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
                notificationBuilder
                    .setContentTitle("수업이 끝났습니다")
                    .setContentText("수업 잘 들으셨나요?")
                    .setContentIntent(pendingIntent)
                notificationManager.notify(NOTIFICATION_CODE_END, notificationBuilder.build())
            }
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

        fun registerAlarm(context: Context, item: Lecture, previousMinutes: Int) {
            val lectureName = item.name
            val tapList = parseTimeAndPlace(item.timeAndPlace)
            tapList.forEach {
                val place = it[0]
                val hour = it[2].split(":")[0].toInt()
                val minute = it[2].split(":")[1].toInt()
                val lastMillis = lastTimeMillis(it[1], hour, minute)
                var lastMillisToStart = lastMillis - previousMinutes * MyTools.MINUITEms
                if (lastMillisToStart < 0) {
                    lastMillisToStart += DAYms * 7
                }
                Log.d(MyTools.TAG, "-> ${it[1]}요일 ${hour}:${minute}까지 ${checkMillis(lastMillis)}만큼 남음. ${previousMinutes}분 전 알림")
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, AlarmReceiver::class.java)
                intent.putExtra("lectureName", lectureName)
                intent.putExtra("place", place)
                intent.putExtra("time", "${hour}시 ${minute}분")

                val pendingIntent = PendingIntent.getBroadcast(
                    context, NOTIFICATION_CODE_START, intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
                alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    lastMillisToStart,
                    pendingIntent
                )
                Log.d(MyTools.TAG, "$lectureName 알람 ${it[1]}요일 ${hour}:${minute-previousMinutes}에 등록됨")
            }
        }

        fun checkAlarm(context: Context, alarmModel: AlarmModel):AlarmModel {
            val pendingIntent = PendingIntent.getBroadcast(context, alarmModel.id, Intent(context, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE)
            if ((pendingIntent == null) and alarmModel.onOff) {
                // 알람은 꺼져있는데 데이터는 켜져있는 경우 -> 데이터 수정
                alarmModel.onOff = false

            } else if ((pendingIntent != null) and alarmModel.onOff.not()){
                // 알람은 켜져있는데 데이터는 꺼진 경우 -> 알람을 취소
                pendingIntent.cancel()
            }
            return alarmModel
        }

        fun removeAlarm(context: Context, alarmModel: AlarmModel) {
            val pendingIntent = PendingIntent.getBroadcast(context, alarmModel.id, Intent(context, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE)
            pendingIntent?.cancel()
        }
    }
}