package org.wysko.autogeokt.operation.leastsquares

import org.jetbrains.kotlinx.multik.api.identity
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.wysko.autogeokt.operation.Operation

data class GeneralLeastSquares(
    override val data: GeneralLeastSquaresData
) : Operation<GeneralLeastSquaresData, GeneralLeastSquaresResult> {
    val wMatrix = mk.identity<Double>(data.w.size)
    override val result: GeneralLeastSquaresResult by lazy {
        TODO()
    }
}


data class GeneralLeastSquaresData(
    val a: D2Array<Double>,
    val l: D1Array<Double>,
    val w: D1Array<Double>
)

data class GeneralLeastSquaresResult(
    val x: D1Array<Double>,
    val v: D1Array<Double>,
    val so: Double
)