package org.wysko.autogeokt.operation.details

import kotlinx.serialization.Serializable
import org.wysko.autogeokt.operation.Operation
import java.time.LocalDateTime
import java.time.ZoneOffset

@Serializable
data class OperationExecution(
    val operation: Operation<*, *>,
    val title: String,
    val timestamp: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
)
