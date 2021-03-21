package cc.ddsakura.modernapp001

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import cc.ddsakura.modernapp001.databinding.FragmentBlankBinding
import java.util.function.BiFunction


class BlankFragment : Fragment(R.layout.fragment_blank) {
    private var _binding: FragmentBlankBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBlankBinding.bind(view)
        binding.myButton.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity2::class.java))
        }
        binding.myButton2.setOnClickListener {
            val func = BiFunction { a: Int, b: Int -> a + b }
            val result = "Desugaring ${func.apply(1, 2)}"
            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
            val d = Log.d(TAG, result)
        }
        binding.myButton3.setOnClickListener {
            val channelId = "CHANNEL_ID"
            createNotificationChannel(channelId)
            var builder = NotificationCompat.Builder(requireContext(), channelId)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("This is Title")
                    .setContentText("This is Content")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(longArrayOf(0))

            with(NotificationManagerCompat.from(requireContext())) {
                // notificationId is a unique int for each notification that you must define
                notify(1001, builder.build())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "This is channel name"
            val descriptionText = "This is channel description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, name, importance)
            mChannel.description = descriptionText
            val notificationManager = requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    companion object {
        private const val TAG = "BlankFragment"
    }
}