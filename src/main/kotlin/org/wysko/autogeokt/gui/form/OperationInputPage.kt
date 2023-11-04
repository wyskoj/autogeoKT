package org.wysko.autogeokt.gui.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.skiko.Cursor
import org.wysko.autogeokt.gui.Wait
import org.wysko.autogeokt.gui.form.components.OperationDisplay
import org.wysko.autogeokt.operation.Operation
import org.wysko.autogeokt.operation.OperationData
import org.wysko.autogeokt.operation.OperationResult
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.primaryConstructor

data class OperationInputScreen(val operation: KClass<out Operation<*, *>>) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            OperationInputPage(operation) {
                navigator.pop()
            }
        }
    }
}

@Composable
fun OperationInputPage(operation: KClass<out Operation<*, *>>, onCancel: () -> Unit) {
    val data = remember { mutableStateOf<OperationData?>(null) }
    val result = remember { mutableStateOf<OperationResult?>(null) }

    val isTempDisplayOpen = remember { mutableStateOf(false) }

    @Suppress("UNCHECKED_CAST")
    val form = remember { (operation.companionObjectInstance!! as Formable).form } as FormDetails<OperationData>

    LazyColumn {
        item {
            IconButton(onClick = onCancel) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        }
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.width(600.dp)
            ) {
                GenerateForm(form) { title, dataOut ->
                    if (title == null) {
                        // Temporary operation
                        data.value = dataOut
                        try {
                            result.value = operation.primaryConstructor!!.call(dataOut).result
                            isTempDisplayOpen.value = true
                        } catch (e: Exception) {
                            println(e)
                        }
                    }
                }
            }
        }
    }
    if (isTempDisplayOpen.value) {
        Dialog(onDismissRequest = { isTempDisplayOpen.value = false }) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(16.dp)) {
                OperationDisplay(data.value!!, result.value!!) {
                    isTempDisplayOpen.value = false
                }
            }
        }
    }
}