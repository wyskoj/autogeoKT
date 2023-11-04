package org.wysko.autogeokt.gui.form.components.input

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.wysko.autogeokt.geospatial.DegreesMinutesSeconds
import org.wysko.autogeokt.gui.form.InputError
import org.wysko.autogeokt.gui.form.components.InputWrapper

@Composable
fun DegreesMinutesSecondsInput(
    value: DegreesMinutesSeconds?,
    onValueChange: (DegreesMinutesSeconds?) -> Unit,
    errors: Set<InputError>?,
    setErrors: (Set<InputError>) -> Unit,
    label: String = "",
    descriptionText: String = "",
    isShowError: Boolean = false,
    isOptional: Boolean = false,
    setIsMutated: (Boolean) -> Unit,
) {
    val d = remember { mutableStateOf(value?.degrees?.toString() ?: "") }
    val m = remember { mutableStateOf(value?.minutes?.toString() ?: "") }
    val s = remember { mutableStateOf(value?.seconds?.toString() ?: "") }

    // On value change, update the value
    LaunchedEffect(listOf(d.value, m.value, s.value)) {
        val validate = DegreesMinutesSecondsInputError.validate(d.value, m.value, s.value)
        setErrors(validate)
        if (validate.isEmpty()) {
            onValueChange(
                DegreesMinutesSeconds(
                    d.value.toInt(),
                    m.value.toInt(),
                    s.value.toDouble()
                )
            )
        } else {
            onValueChange(null)
        }
        if (d.value.isNotBlank() || m.value.isNotBlank() || s.value.isNotBlank()) {
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
        DMSInput(d, m, s, errors, isShowError)
    }
}

@Composable
fun DMSInput(
    d: MutableState<String>,
    m: MutableState<String>,
    s: MutableState<String>,
    errors: Set<InputError>?,
    isShowError: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            d.value,
            onValueChange = { d.value = it },
            singleLine = true,
            modifier = Modifier.weight(1f),
            suffix = { Text("°") },
            isError = errors?.any() == true && isShowError
        )
        TextField(
            m.value,
            onValueChange = { m.value = it },
            singleLine = true,
            modifier = Modifier.weight(1f),
            suffix = { Text("'") },
            isError = errors?.any() == true && isShowError
        )
        TextField(
            s.value,
            onValueChange = { s.value = it },
            singleLine = true,
            modifier = Modifier.weight(1f),
            suffix = { Text("\"") },
            isError = errors?.any() == true && isShowError
        )
    }
}

sealed class DegreesMinutesSecondsInputError(override val message: String) : InputError {
    data object InvalidDegrees : DegreesMinutesSecondsInputError("Invalid degrees")
    data object InvalidMinutes : DegreesMinutesSecondsInputError("Invalid minutes")
    data object InvalidSeconds : DegreesMinutesSecondsInputError("Invalid seconds")
    data object DegreesOutOfRange : DegreesMinutesSecondsInputError("Degrees out of range 0..359")
    data object MinutesOutOfRange : DegreesMinutesSecondsInputError("Minutes out of range 0..59")
    data object SecondsOutOfRange : DegreesMinutesSecondsInputError("Seconds out of range 0..<60")
    companion object {
        fun validate(degrees: String, minutes: String, seconds: String): Set<DegreesMinutesSecondsInputError> {
            val errors = mutableSetOf<DegreesMinutesSecondsInputError>()
            val d = degrees.toIntOrNull()
            val m = minutes.toIntOrNull()
            val s = seconds.toDoubleOrNull()
            if (d == null) errors.add(InvalidDegrees)
            if (m == null) errors.add(InvalidMinutes)
            if (s == null) errors.add(InvalidSeconds)
            d?.let {
                if (it !in 0..359) errors.add(DegreesOutOfRange)
            }
            m?.let {
                if (it !in 0..59) errors.add(MinutesOutOfRange)
            }
            s?.let {
                if (it !in 0.0..<60.0) errors.add(SecondsOutOfRange)
            }
            return errors
        }
    }
}
