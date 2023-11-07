package org.wysko.autogeokt.gui.form.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import org.wysko.autogeokt.geospatial.Cartesian2D
import org.wysko.autogeokt.geospatial.Circle
import org.wysko.autogeokt.gui.form.InputError
import org.wysko.autogeokt.gui.form.components.InputWrapper

@Composable
fun CircleInput(
    value: Circle?,
    onValueChange: (Circle?) -> Unit,
    errors: Set<InputError>?,
    setErrors: (Set<InputError>) -> Unit,
    label: String = "",
    descriptionText: String = "",
    isShowError: Boolean = false,
    isOptional: Boolean = false,
    setIsMutated: (Boolean) -> Unit,
) {
    val x = remember { mutableStateOf(value?.center?.x?.toString() ?: "") }
    val y = remember { mutableStateOf(value?.center?.y?.toString() ?: "") }

    val r = remember { mutableStateOf(value?.radius?.toString() ?: "") }

    // On value change, update the value
    LaunchedEffect(listOf(x.value, y.value, r.value)) {
        val validate = RealNumberInputError.validate(r.value).plus(StationInputError.validate(x.value, y.value))
        setErrors(validate)
        if (validate.isEmpty()) {
            onValueChange(
                Circle(
                    center = Cartesian2D(x.value.toDouble(), y.value.toDouble()),
                    radius = r.value.toDouble(),
                ),
            )
        } else {
            onValueChange(null)
        }

        if (x.value.isNotBlank() || y.value.isNotBlank() || r.value.isNotBlank()) {
            setIsMutated(true)
        } else {
            setIsMutated(false)
        }
    }

    InputWrapper(
        isShowError = isShowError,
        errors = errors,
        descriptionText = descriptionText,
        isOptional = isOptional,
        title = label,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StationInput(x, y, errors, isShowError)
            NumberInput(r, errors, isShowError, "Radius")
        }
    }
}
