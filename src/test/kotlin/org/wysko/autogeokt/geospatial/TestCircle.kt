package org.wysko.autogeokt.geospatial

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestCircle {
    @Test
    fun `Should compute the correct diameter`() {
        val circle = Circle(Cartesian2D(0.0, 0.0), 856.15)
        assertEquals(1712.3, circle.diameter, 0.1)
    }

    @Test
    fun `Should compute the correct area`() {
        val circle = Circle(Cartesian2D(0.0, 0.0), 693.28)
        assertEquals(1_509_966.166, circle.area, 0.001)
    }
}