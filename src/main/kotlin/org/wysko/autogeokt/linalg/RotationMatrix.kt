package org.wysko.autogeokt.linalg

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import kotlin.math.cos
import kotlin.math.sin

/**
 * Operations on rotation matrices.
 */
object RotationMatrix {
    /**
     * Computes a rotation matrix from three angles.
     *
     * @param beta1 The first angle.
     * @param beta2 The second angle.
     * @param beta3 The third angle.
     */
    fun fromAngles(beta1: Double, beta2: Double, beta3: Double): D2Array<Double> {
        return mk.ndarray(
            mk[
                mk[
                    cos(beta2) * cos(beta3),
                    cos(beta1) * sin(beta3) + sin(beta1) * sin(beta2) * cos(beta3),
                    sin(beta1) * sin(beta3) - cos(beta1) * sin(beta2) * cos(beta3),
                ],
                mk[
                    -cos(beta2) * sin(beta3),
                    cos(beta1) * cos(beta3) - sin(beta1) * sin(beta2) * sin(beta3),
                    sin(beta1) * cos(beta3) + cos(beta1) * sin(beta2) * sin(beta3),
                ],
                mk[
                    sin(beta2),
                    -sin(beta1) * cos(beta2),
                    cos(beta1) * cos(beta2),
                ],
            ],
        )
    }
}
