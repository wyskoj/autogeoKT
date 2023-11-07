package org.wysko.autogeokt.gui.form.components.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.wysko.autogeokt.gui.form.InputError
import org.wysko.autogeokt.gui.form.components.InputWrapper

@Composable
fun RealNumberInput(
    value: Double?,
    onValueChange: (Double?) -> Unit,
    errors: Set<InputError>?,
    setErrors: (Set<InputError>) -> Unit,
    label: String = "",
    descriptionText: String = "",
    isShowError: Boolean = false,
    isOptional: Boolean = false,
    setIsMutated: (Boolean) -> Unit,
    validators: Set<(String) -> InputError> = emptySet(),
) {
    val v = remember { mutableStateOf(value?.toString() ?: "") }

    // On value change, update the value
    LaunchedEffect(listOf(v.value)) {
        val validate = validators.plus {
            if (v.value.toDoubleOrNull() == null) {
                RealNumberInputError.InvalidNumber
            } else {
                null
            }
        }.mapNotNull { it(v.value) }
        setErrors(validate.toSet())
        if (validate.isEmpty()) {
            onValueChange(v.value.toDouble())
        } else {
            onValueChange(null)
        }
        setIsMutated(v.value.isNotBlank())
    }

    InputWrapper(
        isShowError = isShowError,
        errors = errors,
        descriptionText = descriptionText,
        isOptional = isOptional,
        title = label,
    ) {
        NumberInput(v, errors, isShowError)
    }
}

@Composable
fun NumberInput(
    v: MutableState<String>,
    errors: Set<InputError>?,
    isShowError: Boolean,
    placeholder: String = "",
) {
    TextField(
        value = v.value,
        onValueChange = { v.value = it },
        isError = isShowError && errors?.isNotEmpty() == true,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
    )
}

sealed class RealNumberInputError(override val message: String, val validator: (String) -> Boolean) : InputError {
    data object InvalidNumber : RealNumberInputError("Invalid number", { it.toDoubleOrNull() == null })
    data object NotPositive : RealNumberInputError("Number must be positive", { (it.toDoubleOrNull() ?: 0.0) <= 0.0 })

    companion object {
        fun validate(value: String): Set<RealNumberInputError> = mutableSetOf<RealNumberInputError>().apply {
            if (InvalidNumber.validator(value)) {
                add(InvalidNumber)
            }
        }
    }
}
