package org.wysko.autogeokt.linalg

import org.jetbrains.kotlinx.multik.api.linalg.inv
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.unaryMinus
import org.wysko.autogeokt.linalg.GeneralLeastSquares.Initialization
import kotlin.math.abs

/**
 * A class that provides the functionality to solve a system of equations using the general least-squares method.
 *
 * @property initialization The initial values for least-squares iteration.
 * @property a The function that calculates the matrix A for the system of equations.
 * @property b The function that calculates the matrix B for the system of equations.
 * @property f The function that calculates the matrix F for the system of equations.
 * @param weightMatrix The weight matrix for the system of equations.
 * @property options The options for the least-squares iteration.
 *
 * @see LeastSquaresIterationOptions
 * @see Initialization
 */
@Suppress("OutdatedDocumentation") // Can't figure out how to document this properly
class GeneralLeastSquares(
    private val initialization: Initialization,
    val a: (observations: D2Array<Double>, parameters: D2Array<Double>) -> D2Array<Double>,
    val b: (observations: D2Array<Double>, parameters: D2Array<Double>) -> D2Array<Double>,
    val f: (observations: D2Array<Double>, parameters: D2Array<Double>) -> D2Array<Double>,
    weightMatrix: D2Array<Double>,
    private val options: LeastSquaresIterationOptions = LeastSquaresIterationOptions(),
) {
    private val q = mk.linalg.inv(weightMatrix)

    /**
     * Solves a system of equations using the least-squares method.
     *
     * @return The calculated parameters that minimize the error.
     * @throws LeastSquaresDivergenceException if the iteration threshold is exceeded.
     */
    fun solve(): D2Array<Double> {
        var isIterating = true
        var errorPrevious = Double.MAX_VALUE
        var error: Double
        var iterations = 0

        var observations = initialization.observations
        var parameters = initialization.parameters

        while (isIterating) {
            val a: D2Array<Double> = a(observations, parameters)
            val b: D2Array<Double> = b(observations, parameters)
            val f: D2Array<Double> = f(observations, parameters)

            // General least-squares matrices
            val ff = -f - a * (initialization.observations - observations)
            val qE = a * q * a.transpose()
            val wE = mk.linalg.inv(qE)
            val normal = b.transpose() * wE * b
            val t = b.transpose() * wE * ff
            val delta = mk.linalg.inv(normal) * t
            val v = q * a.transpose() * wE * (ff - b * delta)
            error = (v.transpose() * q * v)[0, 0]

            val objective = abs((errorPrevious - error) / errorPrevious)

            if (objective < options.convergenceThreshold) {
                isIterating = false
            }

            if (iterations > options.iterationThreshold) {
                throw LeastSquaresDivergenceException()
            }

            observations = initialization.observations + v
            parameters += delta
            errorPrevious = error
            iterations++
        }

        return parameters
    }

    /**
     * Wraps initial values for least-squares iteration.
     *
     * @property observations The initial observations matrix (L).
     * @property parameters The initial parameters matrix (par or x).
     */
    data class Initialization(
        val observations: D2Array<Double>,
        val parameters: D2Array<Double>,
    )
}
