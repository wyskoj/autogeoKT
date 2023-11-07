package org.wysko.autogeokt.gui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object I18n {
    operator fun get(key: String): MutableState<String> = mutableStateOf(key) // TODO
}
