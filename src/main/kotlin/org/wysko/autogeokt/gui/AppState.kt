package org.wysko.autogeokt.gui

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.wysko.autogeokt.operation.details.OperationExecution
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
object AppState {
    private val OPERATIONS_FILE = File("operations.pb")

    private var operations = mutableListOf<OperationExecution>().apply {
        ProtoBuf.decodeFromByteArray<List<OperationExecution>>(
            OPERATIONS_FILE.readBytes(),
        ).forEach {
            add(it)
        }
    }

    fun addOperation(operation: OperationExecution) {
        operations.add(operation)
        writeOperationsToDisk()
    }

    fun operations(): List<OperationExecution> = operations

    private fun writeOperationsToDisk() {
        OPERATIONS_FILE.writeBytes(
            ProtoBuf.encodeToByteArray(
                operations,
            ),
        )
    }
}
