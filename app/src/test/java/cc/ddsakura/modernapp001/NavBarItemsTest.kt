package cc.ddsakura.modernapp001

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavBarItemsTest {
    @Test
    fun barItems_includeEachRouteExactlyOnceInDisplayOrder() {
        val routes = NavBarItems.BarItems.map { it.route }

        assertEquals(
            listOf(
                NavRoutes.Home.route,
                NavRoutes.Contacts.route,
                NavRoutes.Favorites.route
            ),
            routes
        )
        assertEquals(routes.size, routes.toSet().size)
    }

    @Test
    fun barItems_haveVisibleTitlesAndRoutes() {
        NavBarItems.BarItems.forEach { item ->
            assertTrue(item.title.isNotBlank())
            assertTrue(item.route.isNotBlank())
        }
    }
}
