@file:UseSerializers(D1Serializer::class, D2Serializer::class)

package org.wysko.autogeokt.geospatial

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import org.wysko.autogeokt.linalg.RotationMatrix
import org.wysko.autogeokt.linalg.times
import org.wysko.autogeokt.serialization.D1ArraySerializer
import org.wysko.autogeokt.serialization.D1Serializer
import org.wysko.autogeokt.serialization.D2ArraySerializer
import org.wysko.autogeokt.serialization.D2Serializer

private const val CARTESIAN_AXES_COUNT = 3

/**
 * A 7-parameter transformation.
 *
 * @property translation The translation vector.
 * @property rotation The rotation components (beta 1, beta 2, beta 3).
 * @property scaleFactor The scale factor.
 */
@Serializable
data class HelmertTransformation(
    @Serializable(with = D2ArraySerializer::class)
    val translation: D2Array<Double>,
    @Serializable(with = D1ArraySerializer::class)
    val rotation: D1Array<Double>,
    val scaleFactor: Double,
) {
    init {
        require(translation.shape[0] == CARTESIAN_AXES_COUNT && translation.shape[1] == 1) {
            "Translation vector must be a 3x1 matrix."
        }
    }

    /**
     * Computes the rotation matrix from the rotation components.
     */
    @Transient
    val rotationMatrix = RotationMatrix.fromAngles(rotation[0], rotation[1], rotation[2])
}

/**
 * Transforms a list of [Cartesian3D] points using the Helmert transformation.
 *
 * @receiver The Helmert transformation.
 * @param points The points to transform.
 */
fun HelmertTransformation.transformPoints(points: List<Cartesian3D>): List<Cartesian3D> = points.map { point ->
    transformPoint(point)
}

/**
 * Transforms a [Cartesian3D] point using the Helmert transformation.
 *
 * @receiver The Helmert transformation.
 * @param point The point to transform.
 */
fun HelmertTransformation.transformPoint(point: Cartesian3D): Cartesian3D =
    (translation + scaleFactor * rotationMatrix * point.matrix).let {
        Cartesian3D(
            it[0, 0],
            it[1, 0],
            it[2, 0],
        )
    }
