package org.wysko.autogeokt.gui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.wysko.autogeokt.gui.AppState
import org.wysko.autogeokt.gui.form.FormDetails
import org.wysko.autogeokt.gui.form.Formable
import org.wysko.autogeokt.gui.form.GenerateForm
import org.wysko.autogeokt.gui.form.components.TemporaryOperationDisplay
import org.wysko.autogeokt.operation.Operation
import org.wysko.autogeokt.operation.OperationData
import org.wysko.autogeokt.operation.OperationResult
import org.wysko.autogeokt.operation.details.OperationExecution
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.primaryConstructor

data class OperationInputScreen(val operation: KClass<out Operation<*, *>>) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            OperationInputPage(operation, onCancel = {
                navigator.pop()
            }, openDashboard = {
                navigator.popAll()
                navigator.push(DashboardScreen)
            })
        }
    }
}

@Composable
fun OperationInputPage(operationClass: KClass<out Operation<*, *>>, onCancel: () -> Unit, openDashboard: () -> Unit) {
    val data = remember { mutableStateOf<OperationData?>(null) }
    val result = remember { mutableStateOf<OperationResult?>(null) }

    val isTempDisplayOpen = remember { mutableStateOf(false) }
    val isErrorDisplayOpen = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf<Exception?>(null) }

    @Suppress("UNCHECKED_CAST")
    val form = remember { (operationClass.companionObjectInstance!! as Formable).form } as FormDetails<OperationData>

    LazyColumn {
        item {
            IconButton(onClick = onCancel) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        }
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.width(600.dp),
            ) {
                GenerateForm(form) { title, dataOut ->
                    data.value = dataOut
                    try {
                        val operation = operationClass.primaryConstructor!!.call(dataOut)
                        result.value = operation.result
                        if (title == null) {
                            isTempDisplayOpen.value = true
                        } else {
                            AppState.addOperation(OperationExecution(operation, title))
                            openDashboard()
                        }
                    } catch (e: IllegalArgumentException) {
                        error.value = e
                        isErrorDisplayOpen.value = true
                        e.printStackTrace()
                    }
                }
            }
        }
    }
    if (isTempDisplayOpen.value) {
        Dialog(onDismissRequest = { isTempDisplayOpen.value = false }) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(16.dp)) {
                TemporaryOperationDisplay(data.value!!, result.value!!) {
                    isTempDisplayOpen.value = false
                }
            }
        }
    }
    if (isErrorDisplayOpen.value) {
        val closeErrorDialog = { isErrorDisplayOpen.value = false }
        ErrorDialog(error, closeErrorDialog)
    }
}

@Composable
private fun ErrorDialog(
    error: MutableState<Exception?>,
    closeErrorDialog: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(36.dp))
        },
        title = {
            Text("Error")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("There was an error executing the operation:")
                OutlinedTextField(
                    value = "${error.value!!.message}",
                    onValueChange = {},
                    readOnly = true,
                    textStyle = TextStyle.Default.copy(fontFamily = FontFamily.Monospace),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = closeErrorDialog,
            ) {
                Text("OK")
            }
        },
        onDismissRequest = closeErrorDialog,
    )
}
