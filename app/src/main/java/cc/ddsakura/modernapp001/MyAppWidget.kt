package cc.ddsakura.modernapp001

import android.content.Context
import android.util.Log
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class MyAppWidget : GlanceAppWidget() {
    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val count = prefs[countPreferenceKey] ?: 0
            Content(count)
        }
    }

    @Composable
    fun Content(count: Int = 0) {
        Column(
            modifier = GlanceModifier
                .padding(8.dp)
                .background(Color.Yellow),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                text = "Hello World + 1",
                style = TextStyle(color = ColorProvider(Color.Black)),
                onClick = actionRunCallback<UpdateCountActionCallback>(
                    parameters = actionParametersOf(
                        countParamKey to (count + 1)
                    )
                )
            )
            Text(
                text = count.toString(),
                modifier = GlanceModifier.fillMaxWidth(),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = ColorProvider(MaterialTheme.colors.onSurface),
                )
            )
            Spacer(modifier = GlanceModifier.padding(8.dp))
            Button(
                text = "Open Activity",
                style = TextStyle(color = ColorProvider(Color.Black)),
                onClick = actionStartActivity<MainActivity>()
            )
        }
    }
}

class UpdateCountActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("MyAppWidget", "Item with id $glanceId and params $parameters clicked.")
        val count = requireNotNull(parameters[countParamKey]) {
            "Add $countParamKey parameter in the ActionParameters of this action."
        }

        // Update the count in the preferences data store
        updateAppWidgetState(
            context = context,
            definition = PreferencesGlanceStateDefinition,
            glanceId = glanceId
        ) { preferences ->
            preferences.toMutablePreferences()
                .apply {
                    this[countPreferenceKey] = count
                }
        }

        MyAppWidget().update(context, glanceId)
    }
}

class MyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyAppWidget()
}

private val countPreferenceKey = intPreferencesKey("count-key")
private val countParamKey = ActionParameters.Key<Int>("count-key")
