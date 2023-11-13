package org.wysko.autogeokt.operation.leastsquares

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.wysko.autogeokt.operation.IndirectObservationLeastSquares
import org.wysko.autogeokt.operation.IndirectObservationLeastSquaresData
import kotlin.test.Test
import kotlin.test.assertEquals

class TestIndirectObservationLeastSquares {
    @Test
    fun `Should work with a simple example`() {
        val a = mk.ndarray(
            mk[
                mk[1.0, 1.0],
                mk[2.0, -1.0],
                mk[1.0, -1.0],
            ],
        )
        val l = mk.ndarray(mk[3.0, 1.5, 0.2])

        val w = mk.ndarray(mk[1.0, 1.0, 1.0])

        val data = IndirectObservationLeastSquaresData(2, 3, a, l, w)
        val result = IndirectObservationLeastSquares(data).result

        assertEquals(1.514_285, result.x[0], 0.000_001)
        assertEquals(1.442_857, result.x[1], 0.000_001)

        assertEquals(-0.042857, result.v[0], 0.000_001)
        assertEquals(0.085714, result.v[1], 0.000_001)
        assertEquals(-0.128571, result.v[2], 0.000_001)

        assertEquals(0.160_356, result.so, 0.000_001)
    }
}
