package org.wysko.autogeokt.operation.leastsquares

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import kotlin.test.Test

class TestGeneralLeastSquares {
    @Test
    fun `Should work with a simple example`() {
        val a = mk.ndarray(mk[
            mk[1.0, 1.0],
            mk[2.0, -1.0],
            mk[1.0, -1.0]
        ])
        val l = mk.ndarray(mk[3.0, 1.5, 0.2])

        val w = mk.ndarray(mk[1.0, 1.0, 1.0])

        val data = GeneralLeastSquaresData(a, l, w)
        GeneralLeastSquares(data).result
    }
}