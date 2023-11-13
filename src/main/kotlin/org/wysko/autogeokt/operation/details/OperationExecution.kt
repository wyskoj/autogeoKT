package org.wysko.autogeokt.operation.details

import kotlinx.serialization.Serializable
import org.wysko.autogeokt.operation.Operation
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Represents the execution of an operation.
 *
 * @property operation The operation that was executed.
 * @property title The user-entered title of the operation.
 * @property timestamp The timestamp of the operation's execution.
 */
@Serializable
data class OperationExecution(
    val operation: Operation<*, *>,
    val title: String,
    val timestamp: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
)
