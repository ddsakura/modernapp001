package cc.ddsakura.modernapp001

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cc.ddsakura.modernapp001.network.APIClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.function.BiFunction

// xml way
// class MainActivity : AppCompatActivity(R.layout.activity_main)

// compose way
class MainActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = APIClient.apiService.get200()
                println("api: " + result.message())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        setContent {
            // Navigating with Compose: https://developer.android.com/jetpack/compose/navigation
            // https://developer.android.com/jetpack/compose/navigation#bottom_navigation
            val navController = rememberNavController()
            val currentBackStack by navController.currentBackStackEntryAsState()
            val currentDestination = currentBackStack?.destination
            val currentScreen = NavBarItems.BarItems.find { it.route == currentDestination?.route }
                ?: NavBarItems.BarItems[0]
            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = {
                        Text(text = currentScreen.title)
                    })
                },
                // https://stackoverflow.com/questions/72084865/content-padding-parameter-it-is-not-used
                content = { padding ->
                    Column(
                        modifier = Modifier
                            .padding(padding)
                    ) {
                        NavigationHost(TAG, navController = navController)
                    }
                },
                bottomBar = { BottomNavigationBar(navController = navController) }
            )
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

// https://kotlinlang.org/docs/sealed-classes.html
sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Contacts : NavRoutes("contacts")
    object Favorites : NavRoutes("favorites")
}

data class BarItem(
    val title: String,
    val image: ImageVector,
    val route: String
)

object NavBarItems {
    val BarItems = listOf(
        BarItem(
            title = "Home",
            image = Icons.Filled.Home,
            route = NavRoutes.Home.route
        ),
        BarItem(
            title = "Contacts",
            image = Icons.Filled.Face,
            route = NavRoutes.Contacts.route
        ),
        BarItem(
            title = "Favorites",
            image = Icons.Filled.Favorite,
            route = NavRoutes.Favorites.route
        )
    )
}

@Composable
fun NavigationHost(tag: String, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route,
    ) {
        composable(NavRoutes.Home.route) {
            MainContent(tag)
        }
        composable(NavRoutes.Contacts.route) {
            Contacts()
        }
        composable(NavRoutes.Favorites.route) {
            Favorites(tag)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        NavBarItems.BarItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(navItem.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = navItem.image,
                        contentDescription = navItem.title
                    )
                },
                label = {
                    Text(text = navItem.title)
                }
            )
        }
    }
}

@Composable
fun Contacts() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Filled.Face,
            contentDescription = NavRoutes.Contacts.route,
            tint = Color.Blue,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
fun Favorites(tag: String) {
    // https://io.google/2022/program/5c6a8dbb-7ac2-4c31-a707-0a16e8424970/
    // https://developer.android.com/reference/kotlin/androidx/activity/compose/package-summary#BackHandler(kotlin.Boolean,kotlin.Function0)
    BackHandler(enabled = true) {
        Log.d(tag, "BackHandler")
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = NavRoutes.Favorites.route,
            tint = Color.Blue,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
private fun MainContent(tag: String, mainContentViewModel: MainContentViewModel = viewModel()) {
    val context = LocalContext.current
    val permissionState = remember { mutableStateOf(false) }
    val showAlertDialog = remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionState.value = isGranted
        if (isGranted) {
            // Permission Accepted: Do something
            Log.d(tag, "PERMISSION GRANTED")
            mainContentViewModel.createIfNeedAndSendNotification(context)
        } else {
            // Permission Denied: Do something
            Log.d(tag, "PERMISSION DENIED")

        }
    }

    if (showAlertDialog.value) {
        AlertDialog(
            onDismissRequest = { showAlertDialog.value = false },
            title = { Text("Notification Permission Required") },
            text = { Text("This app needs permission to show notifications.") },
            confirmButton = {
                Button(onClick = {
                    launcher.launch(POST_NOTIFICATIONS)
                    showAlertDialog.value = false
                }) {
                    Text("OK")
                }
            }
        )
    }

    LazyColumn {
        item {
            MyFunctionButton(resId = R.string.open_activity, onClick = {
                context.startActivity(Intent(context, MainActivity2::class.java))
            })
        }
        item {
            MyFunctionButton(resId = R.string.java8_desugar_test, onClick = {
                val func = BiFunction { a: Int, b: Int -> a + b }
                val result = "Desugaring ${func.apply(1, 2)}"
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                Log.d(tag, result)
            })
        }
        item {
            MyFunctionButton(resId = R.string.heads_up_notification, onClick = {
                if (ContextCompat.checkSelfPermission(
                        context,
                        POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    // show a custom message explaining why the app needs the notification permission
                    showAlertDialog.value = true
                } else {
                    // notification permission is granted, show a Snackbar to let the user know
                    mainContentViewModel.createIfNeedAndSendNotification(context)
                }


//                when {
//                    ContextCompat.checkSelfPermission(
//                        context,
//                        POST_NOTIFICATIONS
//                    ) == PackageManager.PERMISSION_GRANTED -> {
//                        mainContentViewModel.createIfNeedAndSendNotification(context)
//                    }
//                    context.getActivity()?.let {
//                        ActivityCompat.shouldShowRequestPermissionRationale(
//                            it, POST_NOTIFICATIONS
//                        )
//                    } == true -> {
//                        Log.d(tag, "shouldShowRequestPermissionRationale")
//                        val builder = AlertDialog.Builder(context)
//                        // Set the alert dialog title
//                        builder.setTitle("Permission required")
//                        // Display a message on alert dialog
//                        builder.setMessage("This app needs permission to send notifications.")
//                        // Set a positive button and its click listener on alert dialog
//                        builder.setPositiveButton("OK") { _, _ ->
//                            // Do something when user press the positive button
//                            Log.d(tag, "OK")
//                            launcher.launch(POST_NOTIFICATIONS)
//                        }
//                        // Display a negative button on alert dialog
//                        builder.setNegativeButton("Cancel") { _, _ ->
//                            Log.d(tag, "Cancel")
//                        }
//                        // Finally, make the alert dialog using builder
//                        val dialog: AlertDialog = builder.create()
//                        // Display the alert dialog on app interface
//                        dialog.show()
//                    }
//                    context.getActivity()?.let {
//                        ActivityCompat.shouldShowRequestPermissionRationale(
//                            it, POST_NOTIFICATIONS
//                        )
//                    } == false -> {
//                        val builder = AlertDialog.Builder(context)
//                        // Set the alert dialog title
//                        builder.setTitle("Permission required")
//                        // Display a message on alert dialog
//                        builder.setMessage("This app needs permission to send notifications. Go to settings to allow it.")
//                        // Set a positive button and its click listener on alert dialog
//                        builder.setPositiveButton("OK") { _, _ ->
//                            // Do something when user press the positive button
//                            Log.d(tag, "OK")
//                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            val uri: Uri = Uri.fromParts("package", "cc.ddsakura.modernapp001", null)
//                            intent.data = uri
//                            context.startActivity(intent)
//                        }
//                        // Display a negative button on alert dialog
//                        builder.setNegativeButton("Cancel") { _, _ ->
//                            Log.d(tag, "Cancel")
//                        }
//                        // Finally, make the alert dialog using builder
//                        val dialog: AlertDialog = builder.create()
//                        // Display the alert dialog on app interface
//                        dialog.show()
//                    }
//                    else -> {
//                        // Asking for permission
//                        launcher.launch(POST_NOTIFICATIONS)
//                    }
//                }
            })
        }
        item {
            MyFunctionButton(resId = R.string.open_activity, onClick = {
                context.startActivity(Intent(context, MainActivity3::class.java))
            })
        }
        item {
            MyFunctionButton(resId = R.string.open_webview_activity, onClick = {
                context.startActivity(Intent(context, WebviewActivity::class.java))
            })
        }
        items(25) { index ->
            Surface(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(text = "Item: $index")
                }
            }
        }
    }
}

@Composable
private fun MyFunctionButton(@StringRes resId: Int, onClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
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

@Preview(showBackground = true, name = "MyFunctionButton")
@Composable
fun PreviewMyFunctionButton() {
    MyFunctionButton(resId = R.string.open_activity) {}
}

@Preview(showBackground = true, name = "MainContent")
@Composable
fun PreviewMainContent() {
    MainContent(tag = "MainContentPreview")
}

@Preview(showBackground = true, name = "BottomNavigationBar")
@Composable
fun PreviewBottomNavigationBar() {
    BottomNavigationBar(navController = rememberNavController())
}