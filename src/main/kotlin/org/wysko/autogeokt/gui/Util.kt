package org.wysko.autogeokt.gui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.window.FrameWindowScope
import java.awt.Cursor

/**
 * Centers the window in the screen.
 *
 * This method is used to center the window in the screen by setting its location relative to null.
 */
@Composable
fun FrameWindowScope.centerWindow() {
    LaunchedEffect(Unit) {
        window.setLocationRelativeTo(null)
    }
}

val PointerIcon.Companion.Wait: PointerIcon
    get() = PointerIcon(Cursor(Cursor.WAIT_CURSOR))
