package org.wysko.autogeokt.gui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.wysko.autogeokt.gui.material.AppTheme
import org.wysko.autogeokt.gui.screen.DashboardScreen
import org.wysko.autogeokt.gui.screen.TabFactory

fun main() = application {
    Window(
        state = WindowState(size = DpSize(1600.dp, 800.dp)),
        onCloseRequest = ::exitApplication,
    ) {
        centerWindow()
        AppTheme(useDarkTheme = false) {
            Navigator(DashboardScreen) { navigator ->
                Row {
                    NavigationRail {
                        Spacer(Modifier.weight(1f))
                        TabFactory.tabs.forEach {
                            NavigationRailItem(
                                selected = true,
                                onClick = {
                                    navigator.run {
                                        popAll()
                                        push(it.screen)
                                    }
                                },
                                icon = {
                                    if (navigator.lastItem == it.screen) {
                                        Icon(it.filledIcon, contentDescription = it.i18nKey)
                                    } else {
                                        Icon(it.outlinedIcon, contentDescription = it.i18nKey)
                                    }
                                },
                                label = { Text(it.i18nKey) },
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                        Spacer(Modifier.weight(1f))
                    }
                    Divider(modifier = Modifier.fillMaxHeight().width(1.dp))
                    SlideTransition(navigator)
                }
            }
        }
    }
}
