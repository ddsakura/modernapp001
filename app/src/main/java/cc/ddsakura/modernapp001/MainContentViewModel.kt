package cc.ddsakura.modernapp001

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel

class MainContentViewModel : ViewModel() {
    private fun createNotificationChannel(context: Context, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "This is channel name"
            val descriptionText = "This is channel description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, name, importance)
            mChannel.description = descriptionText
            val notificationManager =
                context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun sendNotification(context: Context) {
        val channelId = "CHANNEL_ID"
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.cross)
            .setContentTitle("This is Title")
            .setContentText("This is Content")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0))

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(1001, builder.build())
        }
    }

    fun createIfNeedAndSendNotification(context: Context) {
        val channelId = "CHANNEL_ID"
        createNotificationChannel(context, channelId)
        sendNotification(context)
    }
}