package org.wysko.autogeokt.operation.cogo

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.wysko.autogeokt.geospatial.Cartesian2D
import org.wysko.autogeokt.geospatial.Circle
import kotlin.test.assertEquals

class TestDistanceDistanceIntersection {
    @Test
    fun `DistanceDistanceIntersection should work with a Ghilani example`() {
        val circle1 = Circle(Cartesian2D(2851.28, 299.4), 2000.0)
        val circle2 = Circle(Cartesian2D(3898.72, 2870.15), 1500.0)

        val intersection = DistanceDistanceIntersection(Pair(circle1, circle2))

        assertEquals(4464.85, intersection.result.first.x, 0.01)
        assertEquals(1481.09, intersection.result.first.y, 0.01)

        assertEquals(2523.02, intersection.result.second.x, 0.01)
        assertEquals(2272.28, intersection.result.second.y, 0.01)
    }

    @Test
    fun `DistanceDistanceIntersection should fail when circles are identical`() {
        val circle1 = Circle(Cartesian2D(2851.28, 299.4), 2000.0)
        val circle2 = Circle(Cartesian2D(2851.28, 299.4), 2000.0)

        assertThrows<IllegalArgumentException> { DistanceDistanceIntersection(Pair(circle1, circle2)).result }
    }

    @Test
    fun `DistanceDistanceIntersection should fail when there is no solution`() {
        val circle1 = Circle(Cartesian2D(4192.56, 6847.32), 500.0)
        val circle2 = Circle(Cartesian2D(2398.13, 2251.76), 500.0)

        assertThrows<IllegalArgumentException> { DistanceDistanceIntersection(Pair(circle1, circle2)).result }
    }
}