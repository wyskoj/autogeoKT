package org.wysko.autogeokt.gui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.*

/**
 * Internationalization.
 */
object I18n {
    private var _currentLocale by mutableStateOf(Locale.getDefault())
    private var _strings: MutableState<ResourceBundle> = mutableStateOf(getStringsFromResourceBundle(_currentLocale))

    /**
     * Gets the string associated with the given key.
     */
    operator fun get(key: String): State<String> = derivedStateOf { _strings.value.getString(key) }

    private fun getStringsFromResourceBundle(locale: Locale): ResourceBundle =
        ResourceBundle.getBundle("autogeo", locale)
}
