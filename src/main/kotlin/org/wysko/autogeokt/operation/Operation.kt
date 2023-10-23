package org.wysko.autogeokt.operation

/**
 * Abstract class for operations. It is used to store data and result of the operation.
 */
interface Operation<T, R> {
    /**
     * Data used in the operation.
     */
    val data: T

    /**
     * Result of the operation.
     */
    val result: R
}