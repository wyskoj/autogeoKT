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
import org.wysko.autogeokt.gui.form.InputError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputWrapper(
    isShowError: Boolean,
    errors: Set<InputError>?,
    descriptionText: String,
    isOptional: Boolean,
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FieldLabelText(title)
            if (isOptional) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary,
                ) {
                    Text("Optional".uppercase(), fontWeight = FontWeight.Bold)
                }
            }

            ErrorTooltip(if (isShowError) (errors ?: emptySet()) else emptySet())
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()

        }
        DescriptionText(descriptionText)
    }
}