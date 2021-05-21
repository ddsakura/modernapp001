package cc.ddsakura.modernapp001

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import cc.ddsakura.modernapp001.databinding.ActivityMain3Binding

class MainActivity3 : AppCompatActivity() {

    private var _binding: ActivityMain3Binding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMain3Binding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main3)
    }
}