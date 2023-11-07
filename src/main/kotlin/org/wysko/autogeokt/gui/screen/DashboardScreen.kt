package org.wysko.autogeokt.gui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.wysko.autogeokt.gui.AppState
import org.wysko.autogeokt.gui.form.components.BulletSpacer
import org.wysko.autogeokt.gui.form.components.OperationDisplay
import java.time.LocalDateTime
import java.time.ZoneOffset

object DashboardScreen : Screen {
    private fun readResolve(): Any = DashboardScreen

    @Composable
    override fun Content() {
        Column {
            AppState.operations().forEach {
                Card(Modifier.padding(16.dp)) {
                    Box(Modifier.padding(16.dp)) {
                        Column {
                            Text(it.title, style = MaterialTheme.typography.headlineMedium)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(it.operation::class.simpleName.toString(), style = MaterialTheme.typography.titleSmall)
                                BulletSpacer()
                                Text(
                                    LocalDateTime.ofEpochSecond(it.timestamp, 0, ZoneOffset.UTC).toString(),
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            }
                            OperationDisplay(it.operation.data, it.operation.result)
                        }
                    }
                }
            }
        }
    }
}
