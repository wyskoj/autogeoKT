package org.wysko.autogeokt.gui.form.components.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.wysko.autogeokt.gui.form.InputError
import org.wysko.autogeokt.gui.form.components.InputWrapper

@Composable
fun TitleInput(
    value: String?,
    onValueChange: (String?) -> Unit,
    errors: Set<InputError>?,
    setErrors: (Set<InputError>) -> Unit,
    isShowError: Boolean,
    canBeTemporary: Boolean,
) {
    LaunchedEffect(value) {
        if (value.isNullOrBlank() && !canBeTemporary) {
            setErrors(setOf(NoTitleError))
        } else {
            setErrors(emptySet())
        }
    }
    InputWrapper(
        isShowError = isShowError,
        errors = errors,
        descriptionText = if (canBeTemporary) "Use a name that uniquely identifies this operation. If you would like to make this a temporary operation (the results are not saved), you can leave this field blank." else "Use a name that uniquely identifies this operation.",
        isOptional = false,
        title = "Title"
    ) {
        TextField(
            value = value ?: "",
            onValueChange = onValueChange,
            isError = isShowError && !errors.isNullOrEmpty(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

data object NoTitleError : InputError {
    override val message: String = "Missing title"
}