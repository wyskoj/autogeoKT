package org.wysko.autogeokt.operation

import org.jetbrains.kotlinx.multik.api.d1array
import org.jetbrains.kotlinx.multik.api.identity
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import org.wysko.autogeokt.geospatial.Cartesian3D
import org.wysko.autogeokt.geospatial.HelmertTransformation
import org.wysko.autogeokt.geospatial.centroid
import org.wysko.autogeokt.geospatial.transformPoints
import org.wysko.autogeokt.linalg.GeneralLeastSquares
import org.wysko.autogeokt.linalg.LeastSquaresDivergenceException
import org.wysko.autogeokt.linalg.dStack
import org.wysko.autogeokt.linalg.times
import org.wysko.autogeokt.linalg.vStack
import kotlin.math.cos
import kotlin.math.sin
import kotlin.reflect.KProperty

// Parameter indices
private const val BETA_1 = 0
private const val BETA_2 = 1
private const val BETA_3 = 2
private const val SCALE_FACTOR = 3

private const val TRANSLATION_X = 4
private const val TRANSLATION_Y = 5
private const val TRANSLATION_Z = 6

private const val COMPONENTS_PER_POINT = 3
private const val ABSOLUTE_ORIENTATION_MINIMUM_POINT_COUNT = 3

/**
 * Calculates absolute orientation.
 */
class AbsoluteOrientation(override val data: AbsoluteOrientationData) :
    Operation<AbsoluteOrientationData, AbsoluteOrientationResult>() {

    /**
     * @throws LeastSquaresDivergenceException if the algorithm diverges.
     */
    override val result: AbsoluteOrientationResult by lazy {
        val pointCount = data.groundPoints.size

        // Guard clauses
        require(pointCount == data.arbitraryPoints.size) {
            "The number of ground points must equal the number of arbitrary points."
        }
        require(pointCount >= ABSOLUTE_ORIENTATION_MINIMUM_POINT_COUNT) {
            "At least three points are required to perform absolute orientation."
        }

        // Approximation of 7-parameter transformation
        val initialParameters = approximateTransformation().let { (t, r, s) ->
            mk.ndarray(
                mk[
                    mk[r[0]],
                    mk[r[1]],
                    mk[r[2]],
                    mk[s],
                    mk[t[0, 0]],
                    mk[t[1, 0]],
                    mk[t[2, 0]],
                ],
            )
        }
        val initialObservation = data.arbitraryPoints.map { it.matrix }.reduce { sum, element ->
            mk.vStack(sum, element)
        }

        val adjustedParameters = GeneralLeastSquares(
            a = { _, parameters ->
                mk.dStack(List(pointCount) { aMatrix(createTransformation(parameters)) })
            },
            b = { observations, parameters ->
                mk.vStack(
                    List(pointCount) {
                        bMatrix(
                            createTransformation(parameters),
                            Cartesian3D(
                                observations[it * COMPONENTS_PER_POINT + 0, 0],
                                observations[it * COMPONENTS_PER_POINT + 1, 0],
                                observations[it * COMPONENTS_PER_POINT + 2, 0],
                            ),
                        )
                    },
                )
            },
            f = { observations, parameters ->
                mk.vStack(
                    List(pointCount) {
                        fMatrix(
                            data.groundPoints[it],
                            Cartesian3D(
                                observations[it * COMPONENTS_PER_POINT + 0, 0],
                                observations[it * COMPONENTS_PER_POINT + 1, 0],
                                observations[it * COMPONENTS_PER_POINT + 2, 0],
                            ),
                            createTransformation(parameters),
                        )
                    },
                )
            },
            initialization = GeneralLeastSquares.Initialization(
                observations = initialObservation,
                parameters = initialParameters,
            ),
            weightMatrix = mk.identity(pointCount * COMPONENTS_PER_POINT),
        ).solve()

        val transformation = createTransformation(adjustedParameters)
        val transformedPoints = transformation.transformPoints(data.arbitraryPoints)

        AbsoluteOrientationResult(
            transformation,
            transformedPoints,
            residuals = mk.d1array(pointCount) { data.groundPoints[it] distanceTo transformedPoints[it] },
        )
    }

    private fun approximateTransformation(): HelmertTransformation {
        val roughTranslation = data.groundPoints.centroid() - data.arbitraryPoints.centroid()
        val roughScaleFactor =
            data.groundPoints.let { it[0] distanceTo it[1] } / data.arbitraryPoints.let { it[0] distanceTo it[1] }
        val beta1 = 0.0
        val beta2 = 0.0
        val beta3 = 0.0
        return HelmertTransformation(
            translation = mk.ndarray(
                mk[
                    mk[roughTranslation[0, 0]],
                    mk[roughTranslation[1, 0]],
                    mk[roughTranslation[2, 0]],
                ],
            ),
            rotation = mk.ndarray(mk[beta1, beta2, beta3]),
            scaleFactor = roughScaleFactor,
        )
    }

    private fun createTransformation(parameters: D2Array<Double>): HelmertTransformation = HelmertTransformation(
        translation = mk.ndarray(
            mk[mk[parameters[TRANSLATION_X, 0]], mk[parameters[TRANSLATION_Y, 0]], mk[parameters[TRANSLATION_Z, 0]]],
        ),
        rotation = mk.ndarray(mk[parameters[BETA_1, 0], parameters[BETA_2, 0], parameters[BETA_3, 0]]),
        scaleFactor = parameters[SCALE_FACTOR, 0],
    )

    private fun aMatrix(
        transformation: HelmertTransformation,
    ): D2Array<Double> {
        val (_, r, s) = transformation

        // Jacobian of constraint equation F with respect to the observations x, y, z
        return mk.ndarray(
            mk[
                mk[
                    -s * cos(r[1]) * cos(r[2]),
                    -s * (cos(r[0]) * sin(r[2]) + cos(r[2]) * sin(r[0]) * sin(r[1])),
                    -s * (sin(r[0]) * sin(r[2]) - cos(r[0]) * cos(r[2]) * sin(r[1])),
                ],
                mk[
                    s * cos(r[1]) * sin(r[2]),
                    -s * (cos(r[0]) * cos(r[2]) - sin(r[0]) * sin(r[1]) * sin(r[2])),
                    -s * (cos(r[2]) * sin(r[0]) + cos(r[0]) * sin(r[1]) * sin(r[2])),
                ],
                mk[
                    -s * sin(r[1]),
                    s * cos(r[1]) * sin(r[0]),
                    -s * cos(r[0]) * cos(r[1]),
                ],
            ],
        )
    }

    @Suppress("LongMethod", "MaxLineLength")
    private fun bMatrix(
        transformation: HelmertTransformation,
        arbitraryPoint: Cartesian3D,
    ): D2Array<Double> {
        val (_, r, s) = transformation
        val b1 = r[0]
        val b2 = r[1]
        val b3 = r[2]
        val x = arbitraryPoint.x
        val y = arbitraryPoint.y
        val z = arbitraryPoint.z

        // Jacobian of constraint equation F with respect to the parameters b1, b2, b3, s, tx, ty, tz
        // Yes, it's a mess, but it's a necessary mess.
        return mk.ndarray(
            mk[
                mk[
                    s * y * (sin(b1) * sin(b3) - cos(b1) * cos(b3) * sin(b2)) - s * z * (
                        cos(b1) * sin(b3) + cos(b3) * sin(
                            b1,
                        ) * sin(b2)
                        ),
                    s * x * cos(b3) * sin(b2) + s * z * cos(b1) * cos(b2) * cos(b3) - s * y * cos(b2) * cos(b3) * sin(
                        b1,
                    ),
                    s * x * cos(b2) * sin(b3) - s * z * (cos(b3) * sin(b1) + cos(b1) * sin(b2) * sin(b3)) - s * y * (
                        cos(
                            b1,
                        ) * cos(b3) - sin(b1) * sin(b2) * sin(b3)
                        ),
                    -y * (cos(b1) * sin(b3) + cos(b3) * sin(b1) * sin(b2)) - z * (
                        sin(
                            b1,
                        ) * sin(b3) - cos(b1) * cos(b3) * sin(b2)
                        ) - x * cos(b2) * cos(b3),
                    -1.0, 0.0, 0.0,
                ],
                mk[
                    s * y * (cos(b3) * sin(b1) + cos(b1) * sin(b2) * sin(b3)) - s * z * (
                        cos(b1) * cos(b3) - sin(b1) * sin(
                            b2,
                        ) * sin(b3)
                        ),
                    s * y * cos(b2) * sin(b1) * sin(b3) - s * z * cos(b1) * cos(b2) * sin(b3) - s * x * sin(b2) * sin(
                        b3,
                    ),
                    s * y * (cos(b1) * sin(b3) + cos(b3) * sin(b1) * sin(b2)) + s * z * (
                        sin(b1) * sin(b3) - cos(b1) * cos(
                            b3,
                        ) * sin(b2)
                        ) + s * x * cos(b2) * cos(b3),
                    x * cos(b2) * sin(b3) - z * (
                        cos(b3) * sin(b1) + cos(b1) * sin(
                            b2,
                        ) * sin(b3)
                        ) - y * (cos(b1) * cos(b3) - sin(b1) * sin(b2) * sin(b3)),
                    0.0, -1.0, 0.0,
                ],
                mk[
                    s * y * cos(b1) * cos(b2) + s * z * cos(b2) * sin(b1),
                    s * z * cos(b1) * sin(b2) - s * x * cos(b2) - s * y * sin(
                        b1,
                    ) * sin(b2),
                    0.0, y * cos(b2) * sin(b1) - z * cos(b1) * cos(b2) - x * sin(b2), 0.0, 0.0, -1.0,
                ],
            ],
        )
    }

    private fun fMatrix(
        groundCoordinate: Cartesian3D,
        arbitraryCoordinate: Cartesian3D,
        helmertTransformation: HelmertTransformation,
    ): D2Array<Double> {
        val ground = groundCoordinate.matrix
        val arbitrary = arbitraryCoordinate.matrix

        val (t, _, s) = helmertTransformation
        val m = helmertTransformation.rotationMatrix

        return ground - s * m * arbitrary - t
    }
}

/**
 * The two lists of points are parallel arrays, with the first point in the first list corresponding to the first point
 * in the second list, and so on.
 *
 * @property groundPoints The ground-space coordinates of the control points.
 * @property arbitraryPoints The arbitrary-space coordinates of the control points.
 */
data class AbsoluteOrientationData(
    val groundPoints: List<Cartesian3D>,
    val arbitraryPoints: List<Cartesian3D>,
) : OperationData() {
    override val propertyOrder: List<KProperty<*>> = listOf(
        ::groundPoints,
        ::arbitraryPoints,
    )
}

/**
 * @property transformation The transformation from arbitrary space to ground space.
 * @property transformedPoints The transformed arbitrary-space points.
 * @property residuals The residuals of the transformation.
 */
data class AbsoluteOrientationResult(
    val transformation: HelmertTransformation,
    val transformedPoints: List<Cartesian3D>,
    val residuals: NDArray<Double, D1>,
) : OperationResult() {
    override val propertyOrder: List<KProperty<*>> = listOf(
        ::transformation,
        ::transformedPoints,
        ::residuals,
    )
}
