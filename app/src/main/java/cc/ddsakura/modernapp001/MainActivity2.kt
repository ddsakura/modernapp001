package cc.ddsakura.modernapp001

import android.os.Bundle
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // 設置窗口插入監聽器以調整系統欄位的填充
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_view2)) { view, windowInsets ->
            // 獲取系統欄位（狀態欄、導航欄等）的插入
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // 根據插入設置視圖的填充
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            // 表示插入已被消耗
            WindowInsetsCompat.CONSUMED
        }

        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val controller = window.insetsController
            // controller?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}