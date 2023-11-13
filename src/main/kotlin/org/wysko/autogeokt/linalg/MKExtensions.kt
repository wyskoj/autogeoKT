@file:Suppress("UnusedReceiverParameter")

package org.wysko.autogeokt.linalg

import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set

/**
 * Takes two 2-dimensional NDArrays and stacks them vertically.
 *
 * For example, given two 2x2 NDArrays:
 *
 * ```
 * [ 1, 2 ]
 * [ 3, 4 ]
 * ```
 *
 * ```
 * [ 5, 6 ]
 * [ 7, 8 ]
 * ```
 *
 * The result would be a 4x2 NDArray:
 *
 * ```
 * [ 1, 2 ]
 * [ 3, 4 ]
 * [ 5, 6 ]
 * [ 7, 8 ]
 * ```
 *
 * @param m1 The first 2-dimensional NDArray.
 * @param m2 The second 2-dimensional NDArray.
 * @return A 2-dimensional NDArray with the two input NDArrays stacked vertically.
 */
fun mk.vStack(m1: D2Array<Double>, m2: NDArray<Double, D2>): NDArray<Double, D2> {
    val result = mk.d2array(m1.shape[0] + m2.shape[0], m1.shape[1]) {
        0.0
    }
    for (i in 0 until m1.shape[0]) {
        for (j in 0 until m1.shape[1]) {
            result[i, j] = m1[i, j]
        }
    }
    for (i in 0 until m2.shape[0]) {
        for (j in 0 until m2.shape[1]) {
            result[i + m1.shape[0], j] = m2[i, j]
        }
    }
    return result
}

/**
 * Takes a collection of 2-dimensional NDArrays and stacks them vertically.
 *
 * For example, given two 2x2 NDArrays:
 *
 * ```
 * [ 1, 2 ]
 * [ 3, 4 ]
 * ```
 *
 * ```
 * [ 5, 6 ]
 * [ 7, 8 ]
 * ```
 *
 * The result would be a 4x2 NDArray:
 *
 * ```
 * [ 1, 2 ]
 * [ 3, 4 ]
 * [ 5, 6 ]
 * [ 7, 8 ]
 * ```
 *
 * @param arrays The collection of 2-dimensional NDArrays.
 * @return A 2-dimensional NDArray with the input NDArrays stacked vertically.
 */
fun mk.vStack(arrays: Collection<NDArray<Double, D2>>): NDArray<Double, D2> {
    val result = mk.d2array(arrays.sumOf { it.shape[0] }, arrays.first().shape[1]) {
        0.0
    }
    var offset = 0
    for (array in arrays) {
        for (i in 0 until array.shape[0]) {
            for (j in 0 until array.shape[1]) {
                result[i + offset, j] = array[i, j]
            }
        }
        offset += array.shape[0]
    }
    return result
}

/**
 * Takes two 2-dimensional NDArrays and stacks them diagonally.
 *
 * For example, given two 2x2 NDArrays:
 *
 * ```
 * [ 1, 2 ]
 * [ 3, 4 ]
 * ```
 *
 * ```
 * [ 5, 6 ]
 * [ 7, 8 ]
 * ```
 *
 * The result would be a 4x4 NDArray:
 *
 * ```
 * [ 1, 2, 0, 0 ]
 * [ 3, 4, 0, 0 ]
 * [ 0, 0, 5, 6 ]
 * [ 0, 0, 7, 8 ]
 * ```
 *
 * @param m1 The first 2-dimensional NDArray.
 * @param m2 The second 2-dimensional NDArray.
 * @return A 2-dimensional NDArray with the two input NDArrays stacked diagonally.
 */
fun mk.dStack(m1: D2Array<Double>, m2: NDArray<Double, D2>): NDArray<Double, D2> {
    val result = mk.d2array(m1.shape[0] + m2.shape[0], m1.shape[1] + m2.shape[1]) {
        0.0
    }
    for (i in 0 until m1.shape[0]) {
        for (j in 0 until m1.shape[1]) {
            result[i, j] = m1[i, j]
        }
    }
    for (i in 0 until m2.shape[0]) {
        for (j in 0 until m2.shape[1]) {
            result[i + m1.shape[0], j + m1.shape[1]] = m2[i, j]
        }
    }
    return result
}

/**
 * Takes a collection of 2-dimensional NDArrays and stacks them diagonally.
 *
 * For example, given two 2x2 NDArrays:
 *
 * ```
 * [ 1, 2 ]
 * [ 3, 4 ]
 * ```
 *
 * ```
 * [ 5, 6 ]
 * [ 7, 8 ]
 * ```
 *
 * The result would be a 4x4 NDArray:
 *
 * ```
 * [ 1, 2, 0, 0 ]
 * [ 3, 4, 0, 0 ]
 * [ 0, 0, 5, 6 ]
 * [ 0, 0, 7, 8 ]
 * ```
 *
 * @param arrays The collection of 2-dimensional NDArrays.
 * @return A 2-dimensional NDArray with the input NDArrays stacked diagonally.
 */
fun mk.dStack(arrays: Collection<NDArray<Double, D2>>): NDArray<Double, D2> {
    val result = mk.d2array(
        arrays.sumOf { it.shape[0] },
        arrays.sumOf { it.shape[1] },
    ) {
        0.0
    }
    var offset = 0
    for (array in arrays) {
        for (i in 0 until array.shape[0]) {
            for (j in 0 until array.shape[1]) {
                result[i + offset, j + offset] = array[i, j]
            }
        }
        offset += array.shape[0]
    }
    return result
}

/**
 * Takes two 2-dimensional NDArrays and stacks them horizontally.
 *
 * For example, given two 2x2 NDArrays:
 *
 * ```
 * [ 1, 2 ]
 * [ 3, 4 ]
 * ```
 *
 * ```
 * [ 5, 6 ]
 * [ 7, 8 ]
 * ```
 *
 * The result would be a 2x4 NDArray:
 *
 * ```
 * [ 1, 2, 5, 6 ]
 * [ 3, 4, 7, 8 ]
 * ```
 *
 * @param m1 The first 2-dimensional NDArray.
 * @param m2 The second 2-dimensional NDArray.
 * @return A 2-dimensional NDArray with the two input NDArrays stacked horizontally.
 */
fun mk.hStack(m1: D2Array<Double>, m2: NDArray<Double, D2>): NDArray<Double, D2> {
    val result = mk.d2array(m1.shape[0], m1.shape[1] + m2.shape[1]) {
        0.0
    }
    for (i in 0 until m1.shape[0]) {
        for (j in 0 until m1.shape[1]) {
            result[i, j] = m1[i, j]
        }
    }
    for (i in 0 until m2.shape[0]) {
        for (j in 0 until m2.shape[1]) {
            result[i, j + m1.shape[1]] = m2[i, j]
        }
    }
    return result
}

/**
 * Multiply two 2-dimensional arrays element-wise and return the result.
 * The arrays must have compatible dimensions for matrix multiplication.
 *
 * @param other The other array to multiply with.
 * @return the result of multiplying the two arrays
 */
operator fun NDArray<Double, D2>.times(other: NDArray<Double, D2>): NDArray<Double, D2> {
    val result = mk.d2array(shape[0], other.shape[1]) {
        0.0
    }
    for (i in 0 until shape[0]) {
        for (j in 0 until other.shape[1]) {
            for (k in 0 until shape[1]) {
                result[i, j] += this[i, k] * other[k, j]
            }
        }
    }
    return result
}
