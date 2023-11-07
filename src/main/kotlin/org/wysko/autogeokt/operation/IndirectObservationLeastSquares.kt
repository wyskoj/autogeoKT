package org.wysko.autogeokt.operation

import org.jetbrains.kotlinx.multik.api.d1array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.reflect.KProperty

/**
 * Performs an "indirect observation" linear least-squares adjustment.
 */
data class IndirectObservationLeastSquares(
    override val data: IndirectObservationLeastSquaresData,
) : Operation<IndirectObservationLeastSquaresData, IndirectObservationLeastSquaresResult>() {

    override val result: IndirectObservationLeastSquaresResult by lazy {
        // This implementation is inspired by Adjustment Computations by Charles Ghilani, Chapter 26

        // Step 1: Form normal equations and constants vector directly from observations
        // (Ghilani 26.3)

        // We make the normal matrix flattened since it is symmetric.
        val flattenedSize = data.unknowns * (data.unknowns + 1) / 2
        val normal = mk.d1array<Double>(flattenedSize) { 0.0 }
        val constants = mk.d1array<Double>(flattenedSize) { 0.0 }
        for (h in 0..<data.observations) {
            for (i in 0..<data.unknowns) {
                for (j in i..<data.unknowns) {
                    val vi = flattenIndex(i, j)
                    normal[vi] = normal[vi] + data.a[h, i] * data.a[h, j] * data.w[h]
                }
                constants[i] = constants[i] + data.a[h, i] * data.l[h] * data.w[h]
            }
        }

        // Step 2: Perform Cholesky decomposition.
        // (Ghilani 26.4)

        val cholesky = mk.d1array<Double>(data.unknowns * data.observations) { 0.0 }
        for (i in 0..<data.unknowns) {
            for (j in 0..i) {
                val sum = (0..<j).sumOf { k ->
                    cholesky[flattenIndex(i, k)] * cholesky[flattenIndex(j, k)]
                }
                if (i == j) {
                    cholesky[flattenIndex(i, j)] = sqrt(normal[flattenIndex(i, i)] - sum)
                } else {
                    cholesky[flattenIndex(i, j)] =
                        (1.0 / cholesky[flattenIndex(j, j)]) * (normal[flattenIndex(i, j)] - sum)
                }
            }
        }

        // Step 3: Forward and Backward Substitution
        // (Ghilani 26.5)

        val y = mk.d1array<Double>(data.unknowns) { 0.0 }
        for (i in 0..<data.unknowns) {
            val sum = (0..<i).sumOf { j -> -cholesky[flattenIndex(i, j)] * y[j] } + constants[i]
            y[i] = sum / cholesky[flattenIndex(i, i)]
        }

        val x = mk.d1array<Double>(data.unknowns) { 0.0 }
        for (i in data.unknowns - 1 downTo 0) {
            val sum = (i + 1..<data.unknowns).sumOf { j -> -cholesky[flattenIndex(j, i)] * x[j] } + y[i]
            x[i] = sum / cholesky[flattenIndex(i, i)]
        }

        // / Part B: Residuals ///
        val variances = mk.d1array<Double>(data.observations) { index ->
            (0..<data.unknowns).sumOf { data.a[index, it] * x[it] } - data.l[index]
        }

        // / Part C: Reference standard deviation ///
        val refStdDev = sqrt(
            (0..<data.observations).sumOf { i -> data.w[i] * variances[i].pow(2) } /
                (data.observations - data.unknowns),
        )

        IndirectObservationLeastSquaresResult(x, variances, refStdDev)
    }

    private fun flattenIndex(row: Int, column: Int): Int {
        if (row > column) {
            return flattenIndex(column, row)
        }
        val n = column + 1
        return ((n * (n - 1)) shr 1) + row
    }
}

/**
 * @property unknowns The number of unknowns, or parameters, to solve for in the adjustment.
 * @property observations The number of observations, or condition equations. This must be at least the number of
 * [unknowns], otherwise no solution exists.
 * @property a The "A" matrix.
 * @property l The "L" matrix.
 * @property w The weight matrix.
 */
data class IndirectObservationLeastSquaresData(
    @PropertyTitle("Unknowns")
    val unknowns: Int,
    @PropertyTitle("Observations")
    val observations: Int,
    @PropertyTitle("A matrix")
    val a: D2Array<Double>,
    @PropertyTitle("L matrix")
    val l: D1Array<Double>,
    @PropertyTitle("Weight matrix")
    val w: D1Array<Double>,
) : OperationData() {
    init {
        // Check all matrix dimensions
        require(a.shape.first() == observations) { "The number of rows in the A matrix must equal `observations`." }
        require(a.shape.last() == unknowns) { "The number of columns in the A matrix must equal `unknowns`." }
        require(l.size == observations) { "The number of elements in the L matrix must equal `observations`." }
        require(w.size == observations) { "The number of elements in the W matrix must equal `observations`." }
    }

    override val propertyOrder: List<KProperty<*>> = listOf(
        IndirectObservationLeastSquaresData::unknowns,
        IndirectObservationLeastSquaresData::observations,
        IndirectObservationLeastSquaresData::a,
        IndirectObservationLeastSquaresData::l,
        IndirectObservationLeastSquaresData::w,
    )
}

/**
 * @property x The adjusted values of the parameters.
 * @property v The residuals of each observation.
 * @property so The reference standard deviation.
 */
data class IndirectObservationLeastSquaresResult(
    val x: D1Array<Double>,
    val v: D1Array<Double>,
    val so: Double,
) : OperationResult() {
    override val propertyOrder: List<KProperty<*>> = listOf(
        IndirectObservationLeastSquaresResult::x,
        IndirectObservationLeastSquaresResult::v,
        IndirectObservationLeastSquaresResult::so,
    )
}
