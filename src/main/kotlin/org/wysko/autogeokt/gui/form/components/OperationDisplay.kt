package org.wysko.autogeokt.gui.form.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.wysko.autogeokt.gui.form.components.result.Property
import org.wysko.autogeokt.operation.OperationData
import org.wysko.autogeokt.operation.OperationResult
import org.wysko.autogeokt.operation.PropertyTitle
import java.util.Optional
import kotlin.reflect.full.findAnnotation

@Composable
fun <D : OperationData, R : OperationResult> OperationDisplay(data: D, result: R, closeDialog: () -> Unit) {
    Box(Modifier.padding(16.dp)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Operation result", style = MaterialTheme.typography.headlineMedium)
                IconButton(onClick = { closeDialog() }) {
                    Icon(
                        painterResource("/icons/close.svg"),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Row {
                Column(
                    modifier = Modifier.weight(0.5f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Data", style = MaterialTheme.typography.titleLarge)
                    data.propertyOrder.forEach { property ->
                        Property(
                            property.findAnnotation<PropertyTitle>()?.value ?: property.name,
                            formatPropertyValue(property.call(data))
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.weight(0.5f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Result", style = MaterialTheme.typography.titleLarge)
                    result.propertyOrder.forEach { property ->
                        Property(
                            property.findAnnotation<PropertyTitle>()?.value ?: property.name,
                            formatPropertyValue(property.call(result))
                        )
                    }
                }
            }
            Text(
                "This is a temporary operation. To save it to your list of operations, enter a title before submitting.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

fun formatPropertyValue(value: Any?): String {
    val unk = "----"

    fun formatOptional(optional: Optional<*>) =
        if (optional.isPresent) formatPropertyValue(optional.get()) else unk

    return value?.let {
        when {
            it is Optional<*> -> formatOptional(it)
            else -> it.toString()
        }
    } ?: unk
}