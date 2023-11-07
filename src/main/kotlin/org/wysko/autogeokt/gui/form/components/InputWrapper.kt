package org.wysko.autogeokt.gui.form.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.wysko.autogeokt.gui.form.InputError

/**
 * A composable function that wraps an input component with additional styling and error handling.
 *
 * @param isShowError Whether to show any errors for the input.
 * @param errors The set of input errors, if any.
 * @param descriptionText The description text for the input.
 * @param isOptional Whether the input is optional.
 * @param title The title of the input.
 * @param content The composable function that represents the input component.
 */
@Composable
fun InputWrapper(
    isShowError: Boolean,
    errors: Set<InputError>?,
    descriptionText: String,
    isOptional: Boolean,
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FieldLabelText(title)
            if (isOptional) {
                OptionalBadge()
            }
            ErrorTooltip(if (isShowError) (errors ?: emptySet()) else emptySet())
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
        DescriptionText(descriptionText)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionalBadge() {
    Badge(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.secondary,
    ) {
        Text("Optional".uppercase(), fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DescriptionText(text: String) {
    Text(text = text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
}

@Composable
private fun FieldLabelText(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium)
}
