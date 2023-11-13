package org.wysko.autogeokt.gui

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.wysko.autogeokt.operation.details.OperationExecution
import java.io.File

/**
 * Stores the application state.
 */
object AppState {
    private val OPERATIONS_FILE = File("operations.json")
    private val protoBuf = Json { encodeDefaults = true }
    private var operations = mutableListOf<OperationExecution>().apply {
        OPERATIONS_FILE.readText().let { string ->
            if (string.isNotEmpty()) {
                protoBuf.decodeFromString<List<OperationExecution>>(
                    string,
                ).forEach {
                    add(it)
                }
            }
        }
    }

    /**
     * Adds an [OperationExecution] to the list of operation executions.
     */
    fun addOperation(operation: OperationExecution) {
        operations.add(operation)
        writeOperationsToDisk()
    }

    /**
     * Returns the list of operation executions.
     */
    fun operations(): List<OperationExecution> = operations

    private fun writeOperationsToDisk() {
        OPERATIONS_FILE.writeText(
            protoBuf.encodeToString(
                operations,
            ),
        )
    }
}
