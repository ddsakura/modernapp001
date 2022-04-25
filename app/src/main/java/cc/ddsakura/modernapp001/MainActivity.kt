package cc.ddsakura.modernapp001

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.function.BiFunction

// xml way
// class MainActivity : AppCompatActivity(R.layout.activity_main)

// compose way
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                val context = LocalContext.current
                MyFunctionButton(resId = R.string.open_activity, onClick = {
                    context.startActivity(Intent(context, MainActivity2::class.java))
                })
                MyFunctionButton(resId = R.string.java8_desugar_test, onClick = {
                    val func = BiFunction { a: Int, b: Int -> a + b }
                    val result = "Desugaring ${func.apply(1, 2)}"
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, result)
                })
                MyFunctionButton(resId = R.string.heads_up_notification, onClick = {
                    val channelId = "CHANNEL_ID"
                    createNotificationChannel(channelId)
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
                })
                MyFunctionButton(resId = R.string.open_activity, onClick = {
                    context.startActivity(Intent(context, MainActivity3::class.java))
                })
                MyFunctionButton(resId = R.string.open_webview_activity, onClick = {
                    context.startActivity(Intent(context, WebviewActivity::class.java))
                })
            }
        }
    }

    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "This is channel name"
            val descriptionText = "This is channel description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, name, importance)
            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

@Composable
private fun MyFunctionButton(@StringRes resId: Int, onClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colors.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(24.dp)
        ) {
            Button(
                onClick = onClick
            ) {
                Text(
                    // a string with arguments
                    text = stringResource(resId)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Text preview")
@Composable
fun DefaultPreview() {
    MyFunctionButton(resId = R.string.open_activity, {})
}