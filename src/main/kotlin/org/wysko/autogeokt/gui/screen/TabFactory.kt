package org.wysko.autogeokt.gui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen

object TabFactory {
    val tabs = listOf(
        Tab(
            screen = DashboardScreen,
            i18nKey = DashboardScreen::class.simpleName!!,
            filledIcon = Icons.Default.Home,
            outlinedIcon = Icons.Outlined.Home,
        ),
        Tab(
            screen = OperationSelectionScreen,
            i18nKey = OperationSelectionScreen::class.simpleName!!,
            filledIcon = Icons.Default.Home,
            outlinedIcon = Icons.Outlined.Home,
        ),
    )

    data class Tab(val screen: Screen, val i18nKey: String, val filledIcon: ImageVector, val outlinedIcon: ImageVector)
}
