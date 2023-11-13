package org.wysko.autogeokt.geospatial

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.operations.div
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A point in 3D space.
 *
 * @property x The x coordinate.
 * @property y The y coordinate.
 * @property z The z coordinate.
 */
class Cartesian3D(
    val x: Double,
    val y: Double,
    val z: Double,
) {
    /**
     * The Cartesian3D point as a 3x1 column matrix.
     *
     * For example, the point (1, 2, 3) would be represented as:
     *
     * ```
     * [ 1 ]
     * [ 2 ]
     * [ 3 ]
     * ```
     *
     * This is useful for matrix multiplication.
     */
    val matrix: NDArray<Double, D2> by lazy {
        mk.ndarray(mk[mk[x], mk[y], mk[z]])
    }

    /**
     * Calculates the Euclidean distance between two [Cartesian3D] points.
     *
     * @param other The Cartesian3D point to calculate the distance to.
     * @return The slope distance between this point and the other point.
     */
    infix fun distanceTo(other: Cartesian3D) = sqrt((x - other.x).pow(2) + (y - other.y).pow(2) + (z - other.z).pow(2))
}

/**
 * Calculates the centroid of a list of Cartesian3D points.
 *
 * @receiver The list of Cartesian3D points.
 * @return A 2-dimensional NDArray representing the centroid.
 */
fun List<Cartesian3D>.centroid(): NDArray<Double, D2> =
    map { it.matrix }.reduce { sum, element -> sum + element } / size.toDouble()
