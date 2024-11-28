package cc.ddsakura.modernapp001

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class HelloWorldWidget : GlanceAppWidget() {
    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            // create your AppWidget here
            MyContent()
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    fun MyContent() {
        Scaffold(
            titleBar = {
                TitleBar(
                    textColor = GlanceTheme.colors.onSurface,
                    startIcon = ImageProvider(R.drawable.ic_launcher_foreground),
                    title = "Hello!",
                    actions = {
                        CircleIconButton(
                            imageProvider = ImageProvider(R.drawable.refresh_icon),
                            contentDescription = "Refresh",
                            contentColor = GlanceTheme.colors.secondary,
                            // transparent
                            backgroundColor = null,
                            onClick = {
                                // do something
                            }
                        )
                    }
                )
            },
            backgroundColor = GlanceTheme.colors.widgetBackground,
            modifier = GlanceModifier.fillMaxSize()
        ) {
            Column(
                modifier =
                GlanceModifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.Yellow)
            ) {
                Text(
                    text = "Hello World",
                    style = TextStyle(color = ColorProvider(Color.Black))
                )
            }
        }
    }
}

class HelloWorldWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HelloWorldWidget()
}
