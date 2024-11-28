package cc.ddsakura.modernapp001

import androidx.compose.runtime.Composable
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
fun MyAppWidgetPreview() {
    MyAppWidget().Content()
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 200, heightDp = 100)
@Composable
fun HelloWorldWidgetPreview() {
    HelloWorldWidget().MyContent()
}
