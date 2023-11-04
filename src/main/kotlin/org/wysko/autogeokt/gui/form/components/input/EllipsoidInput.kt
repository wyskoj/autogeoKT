package org.wysko.autogeokt.gui.form.components.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.wysko.autogeokt.geospatial.Ellipsoid
import org.wysko.autogeokt.gui.form.InputError
import org.wysko.autogeokt.gui.form.components.ExposedDropDownMenu
import org.wysko.autogeokt.gui.form.components.InputWrapper
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.declaredMemberProperties

@Composable
fun EllipsoidInput(
    value: Ellipsoid?,
    onValueChange: (Ellipsoid?) -> Unit,
    label: String,
    descriptionText: String,
    errors: Set<InputError>?,
    setErrors: (Set<InputError>) -> Unit,
    isShowError: Boolean = false,
    setIsMutated: (Boolean) -> Unit
) {
    val ellipsoids = Ellipsoid::class.companionObjectInstance
        ?.let { companionInstance ->
            companionInstance::class.declaredMemberProperties.mapNotNull { it.call(companionInstance) as? Ellipsoid }
        }!!

    LaunchedEffect(value) {
        if (value == null) {
            setErrors(setOf(NoEllipsoidSelectedError()))
            setIsMutated(false)
        } else {
            setErrors(emptySet())
            setIsMutated(true)
        }
    }

    InputWrapper(
        isShowError, errors, descriptionText, false, label
    ) {
        ExposedDropDownMenu(
            items = listOf(null, *ellipsoids.toTypedArray()),
            title = "",
            displayText = {
                it?.name ?: "Select an ellipsoid"
            },
            selectedItem = value,
            onItemSelected = onValueChange,
            secondaryText = {
                it?.let {
                    "a = ${it.a}, b = ${it.b}"
                } ?: "No ellipsoid selected"
            },
            modifier = Modifier.fillMaxWidth(),
            isError = errors?.any() == true && isShowError
        )
    }
}

data class NoEllipsoidSelectedError(
    override val message: String = "No ellipsoid selected"
) : InputError