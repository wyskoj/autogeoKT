package org.wysko.autogeokt.gui.form.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.wysko.autogeokt.gui.form.InputError

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ErrorTooltip(errors: Set<InputError>) {
    AnimatedVisibility(errors.any()) {
        TooltipArea(
            tooltip = {
                Surface(
                    modifier = Modifier.shadow(4.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "This field has errors:\n${errors.joinToString("\n") { "• ${it.message}" }}",
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        ) {
            Icon(
                painterResource("/icons/error.svg"),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (errors.any()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}