package org.wysko.autogeokt.linalg

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestMKExtensions {
    @Test
    fun `Diagonal stacking`() {
        val a = mk.ndarray(
            mk[
                mk[1.0, 2.0, 3.0],
                mk[4.0, 5.0, 6.0],
                mk[7.0, 8.0, 9.0],
            ],
        )
        val b = mk.ndarray(
            mk[
                mk[10.0, 11.0, 12.0],
                mk[13.0, 14.0, 15.0],
                mk[16.0, 17.0, 18.0],
            ],
        )
        val c = mk.ndarray(
            mk[
                mk[1.0, 2.0, 3.0, 0.0, 0.0, 0.0],
                mk[4.0, 5.0, 6.0, 0.0, 0.0, 0.0],
                mk[7.0, 8.0, 9.0, 0.0, 0.0, 0.0],
                mk[0.0, 0.0, 0.0, 10.0, 11.0, 12.0],
                mk[0.0, 0.0, 0.0, 13.0, 14.0, 15.0],
                mk[0.0, 0.0, 0.0, 16.0, 17.0, 18.0],
            ],
        )
        assertEquals(c, mk.dStack(a, b))
    }

    @Test
    fun `Vertical stacking`() {
        val a = mk.ndarray(
            mk[
                mk[1.0, 2.0, 3.0],
                mk[4.0, 5.0, 6.0],
                mk[7.0, 8.0, 9.0],
            ],
        )
        val b = mk.ndarray(
            mk[
                mk[10.0, 11.0, 12.0],
                mk[13.0, 14.0, 15.0],
                mk[16.0, 17.0, 18.0],
            ],
        )
        val c = mk.ndarray(
            mk[
                mk[1.0, 2.0, 3.0],
                mk[4.0, 5.0, 6.0],
                mk[7.0, 8.0, 9.0],
                mk[10.0, 11.0, 12.0],
                mk[13.0, 14.0, 15.0],
                mk[16.0, 17.0, 18.0],
            ],
        )
        assertEquals(c, mk.vStack(a, b))
    }

    @Test
    fun `Horizontal stacking`() {
        val a = mk.ndarray(
            mk[
                mk[1.0, 2.0, 3.0],
                mk[4.0, 5.0, 6.0],
                mk[7.0, 8.0, 9.0],
            ],
        )
        val b = mk.ndarray(
            mk[
                mk[10.0, 11.0, 12.0],
                mk[13.0, 14.0, 15.0],
                mk[16.0, 17.0, 18.0],
            ],
        )
        val c = mk.ndarray(
            mk[
                mk[1.0, 2.0, 3.0, 10.0, 11.0, 12.0],
                mk[4.0, 5.0, 6.0, 13.0, 14.0, 15.0],
                mk[7.0, 8.0, 9.0, 16.0, 17.0, 18.0],
            ],
        )
        assertEquals(c, mk.hStack(a, b))
    }
}
