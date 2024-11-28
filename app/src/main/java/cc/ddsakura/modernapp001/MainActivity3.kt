package cc.ddsakura.modernapp001

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.WindowManager.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import cc.ddsakura.modernapp001.databinding.ActivityMain3Binding

class MainActivity3 : AppCompatActivity() {
    private lateinit var binding: ActivityMain3Binding

    private val screenCaptureCallback by lazy {
        ScreenCaptureCallback {
            AlertDialog.Builder(this)
                .setMessage(
                    "You have taken a screenshot of the current screen, be careful to protect your privacy and do not share it with others."
                )
                .setTitle("Warning")
                .show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            registerScreenCaptureCallback(mainExecutor, screenCaptureCallback)
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            unregisterScreenCaptureCallback(screenCaptureCallback)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main3)
        window.setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE)
        // clear flag
        // window.clearFlags(LayoutParams.FLAG_SECURE)
    }
}
