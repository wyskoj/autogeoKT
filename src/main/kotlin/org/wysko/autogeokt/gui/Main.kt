package org.wysko.autogeokt.gui

import androidx.compose.foundation.layout.fillMaxSize
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
import org.wysko.autogeokt.gui.screen.OperationSelectionScreen

fun main() = application {
    Window(
        state = WindowState(size = DpSize(1600.dp, 800.dp)),
        onCloseRequest = ::exitApplication
    ) {
        centerWindow()
        AppTheme(useDarkTheme = true) {
            Scaffold(modifier = Modifier.fillMaxSize()) {
                PermanentNavigationDrawer({
                    ModalDrawerSheet {
                        NavigationDrawerItem(
                            label = { Text("Operation Selection") },
                            selected = true,
                            onClick = {}
                        )
                    }
                }) {
                    Navigator(OperationSelectionScreen) {
                        SlideTransition(it)
                    }
                }

            }
        }
    }
}