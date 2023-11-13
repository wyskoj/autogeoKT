package org.wysko.autogeokt.gui.form.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.wysko.autogeokt.gui.I18n
import org.wysko.autogeokt.operation.OperationData
import org.wysko.autogeokt.operation.OperationResult
import org.wysko.autogeokt.operation.PropertyTitle
import java.util.*
import kotlin.reflect.full.findAnnotation

private const val ONE_HALF = 0.5f

/**
 * Displays both the operation data and result in a dialog. This is used for temporary operations.
 *
 * @param D The type of the operation data.
 * @param R The type of the operation result.
 * @param data The operation data.
 * @param result The operation result.
 * @param closeDialog A function to close the dialog.
 */
@Composable
fun <D : OperationData, R : OperationResult> TemporaryOperationDisplay(data: D, result: R, closeDialog: () -> Unit) {
    Box(Modifier.padding(16.dp)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(I18n["operation_result"].value, style = MaterialTheme.typography.headlineMedium)
                IconButton(onClick = closeDialog) {
                    Icon(
                        painterResource("/icons/close.svg"),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            OperationDisplay(data, result)
            Text(
                I18n["operation_temporary_hint"].value,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = ONE_HALF),
            )
        }
    }
}

/**
 * Displays both the operation data and result in a dialog.
 *
 * @param D The type of the operation data.
 * @param R The type of the operation result.
 * @param data The operation data.
 * @param result The operation result.
 */
@Composable
fun <D : OperationData, R : OperationResult> OperationDisplay(data: D, result: R) {
    Row {
        Column(
            modifier = Modifier.weight(ONE_HALF),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Data", style = MaterialTheme.typography.titleLarge)
            data.propertyOrder.forEach { property ->
                Property(
                    property.findAnnotation<PropertyTitle>()?.value ?: property.name,
                    formatPropertyValue(property.call(data)),
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(ONE_HALF),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Result", style = MaterialTheme.typography.titleLarge)
            result.propertyOrder.forEach { property ->
                Property(
                    property.findAnnotation<PropertyTitle>()?.value ?: property.name,
                    formatPropertyValue(property.call(result)),
                )
            }
        }
    }
}

@Composable
private fun Property(name: String, value: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(name, style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = value, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth())
    }
}

/**
 * Formats an object into a string for display in the operation result dialog. If the object is an [Optional], it will
 * be formatted as a string, or "----" if it is empty.
 *
 * @param value The value to format.
 */
fun formatPropertyValue(value: Any?): String {
    val notPresentValue = "----"

    fun formatOptional(optional: Optional<*>): String =
        optional.map(::formatPropertyValue).orElse(notPresentValue)

    return when (value) {
        is Optional<*> -> formatOptional(value)
        null -> notPresentValue
        else -> value.toString()
    }
}
