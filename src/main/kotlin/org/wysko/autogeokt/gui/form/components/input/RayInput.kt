package org.wysko.autogeokt.gui.form.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.wysko.autogeokt.geospatial.Cartesian2D
import org.wysko.autogeokt.geospatial.DegreesMinutesSeconds
import org.wysko.autogeokt.geospatial.Ray
import org.wysko.autogeokt.gui.form.InputError
import org.wysko.autogeokt.gui.form.components.InputWrapper

@Composable
fun RayInput(
    value: Ray?,
    onValueChange: (Ray?) -> Unit,
    errors: Set<InputError>?,
    setErrors: (Set<InputError>) -> Unit,
    label: String = "",
    descriptionText: String = "",
    isShowError: Boolean = false,
    isOptional: Boolean = false,
    setIsMutated: (Boolean) -> Unit
) {
    val d = remember { mutableStateOf(value?.direction?.degrees?.toString() ?: "") }
    val m = remember { mutableStateOf(value?.direction?.minutes?.toString() ?: "") }
    val s = remember { mutableStateOf(value?.direction?.seconds?.toString() ?: "") }

    val x = remember { mutableStateOf(value?.point?.x?.toString() ?: "") }
    val y = remember { mutableStateOf(value?.point?.y?.toString() ?: "") }

    // On value change, update the value
    LaunchedEffect(listOf(d.value, m.value, s.value, x.value, y.value)) {
        val validate = DegreesMinutesSecondsInputError.validate(d.value, m.value, s.value).plus(
            StationInputError.validate(x.value, y.value)
        )
        setErrors(validate)
        if (validate.isEmpty()) {
            onValueChange(
                Ray(
                    point = Cartesian2D(x.value.toDouble(), y.value.toDouble()),
                    direction = DegreesMinutesSeconds(
                        d.value.toInt(),
                        m.value.toInt(),
                        s.value.toDouble()
                    )
                )
            )
        } else {
            onValueChange(null)
        }
        if (d.value.isNotBlank() || m.value.isNotBlank() || s.value.isNotBlank() || x.value.isNotBlank() || y.value.isNotBlank()) {
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
        title = label
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DMSInput(d, m, s, errors, isShowError)
            StationInput(x, y, errors, isShowError)
        }
    }
}

@Composable
fun StationInput(
    x: MutableState<String>,
    y: MutableState<String>,
    errors: Set<InputError>?,
    isShowError: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = x.value,
            onValueChange = { x.value = it },
            label = { Text("X") },
            modifier = Modifier.weight(1f),
            isError = isShowError && errors?.any() == true,
            singleLine = true
        )
        TextField(
            value = y.value,
            onValueChange = { y.value = it },
            label = { Text("Y") },
            modifier = Modifier.weight(1f),
            isError = isShowError && errors?.any() == true,
            singleLine = true
        )
    }
}

sealed class StationInputError(override val message: String) : InputError {
    data object InvalidX : StationInputError("Invalid X")
    data object InvalidY : StationInputError("Invalid Y")
    companion object {
        fun validate(x: String, y: String): Set<StationInputError> {
            val errors = mutableSetOf<StationInputError>()
            val xDouble = x.toDoubleOrNull()
            val yDouble = y.toDoubleOrNull()
            if (xDouble == null) errors.add(InvalidX)
            if (yDouble == null) errors.add(InvalidY)
            return errors
        }
    }
}
