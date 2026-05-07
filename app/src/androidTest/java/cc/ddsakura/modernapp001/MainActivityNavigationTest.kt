package cc.ddsakura.modernapp001

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class MainActivityNavigationTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun bottomNavigation_switchesBetweenPrimaryScreens() {
        composeTestRule.onNodeWithText("Item: 0").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Contacts", useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithContentDescription(NavRoutes.Contacts.route, useUnmergedTree = true).assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Favorites", useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithContentDescription(NavRoutes.Favorites.route, useUnmergedTree = true).assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Home", useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithText("Item: 0").assertIsDisplayed()
    }
}
