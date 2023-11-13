package org.wysko.autogeokt.gui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen

/**
 * Factory for creating tabs.
 */
object TabFactory {
    /**
     * The list of tabs.
     */
    val tabs = listOf(
        Tab(
            screen = DashboardScreen,
            i18nKey = "screen_dashboard",
            filledIcon = Icons.Default.Home,
            outlinedIcon = Icons.Outlined.Home,
        ),
        Tab(
            screen = OperationSelectionScreen,
            i18nKey = "screen_operation_selection",
            filledIcon = Icons.Default.AddCircle,
            outlinedIcon = Icons.Default.Add,
        ),
    )

    /**
     * A tab in the application.
     *
     * @property screen The screen to display when the tab is selected.
     * @property i18nKey The i18n key for the tab.
     * @property filledIcon The filled icon for the tab.
     * @property outlinedIcon The outlined icon for the tab.
     */
    data class Tab(val screen: Screen, val i18nKey: String, val filledIcon: ImageVector, val outlinedIcon: ImageVector)
}
