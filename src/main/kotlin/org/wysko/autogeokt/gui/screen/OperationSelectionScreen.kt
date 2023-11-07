package org.wysko.autogeokt.gui.screen

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import org.wysko.autogeokt.gui.Wait
import org.wysko.autogeokt.operation.OPERATION_DETAILS
import org.wysko.autogeokt.operation.Operation
import org.wysko.autogeokt.operation.details.OperationCategory
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

/**
 * The operation selection screen.
 */
object OperationSelectionScreen : Screen {
    private fun readResolve(): Any = OperationSelectionScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val isLoading = remember { mutableStateOf(false) }
        Box(
            modifier = Modifier.fillMaxSize().pointerHoverIcon(
                if (isLoading.value) PointerIcon.Wait else PointerIcon.Default,
            ),
        ) {
            OperationSelectionScreenContent(isLoading.value) {
                CoroutineScope(Default).launch {
                    isLoading.value = true
                    // The following line takes a long time to execute, so we run it in a coroutine
                    // to prevent the UI from freezing.
                    it.companionObjectInstance!!
                    navigator.push(OperationInputScreen(it))
                    isLoading.value = false
                }
            }
        }
    }
}

/**
 * Displays the operation selection screen.
 */
@Composable
@Preview
fun OperationSelectionScreenContent(isLoading: Boolean, onOperationSelect: (KClass<out Operation<*, *>>) -> Unit) {
    Column {
        OperationCategory.entries.forEach { category ->
            OperationCategoryCard(category, isLoading) {
                onOperationSelect(it)
            }
        }
    }
}

@Composable
fun OperationCategoryCard(
    category: OperationCategory,
    isLoading: Boolean,
    onOperationSelect: (KClass<out Operation<*, *>>) -> Unit,
) {
    Card(
        modifier = Modifier.width(400.dp).padding(16.dp),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface,
        elevation = 1.dp,
    ) {
        Column {
            OperationCategoryHeader(category.title, painterResource(category.icon))
            OPERATION_DETAILS.entries.filter { it.value.category == category }.forEach {
                Divider(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurfaceVariant)
                OperationSelectionButton(it.value.title, painterResource(it.value.icon), isLoading) {
                    onOperationSelect(it.key)
                }
            }
        }
    }
}

/**
 * Displays the header of an operation category.
 */
@Composable
fun OperationCategoryHeader(title: String, icon: Painter) {
    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp),
            )
        }
    }
}

/**
 * Displays an operation selection.
 */
@Composable
fun OperationSelectionButton(title: String, icon: Painter, isLoading: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.clickable(enabled = !isLoading) {
            onClick()
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}
