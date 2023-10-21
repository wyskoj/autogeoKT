package org.wysko.autogeokt

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.wysko.autogeokt.geospatial.Cartesian2D
import org.wysko.autogeokt.geospatial.Circle
import org.wysko.autogeokt.geospatial.DegreesMinutesSeconds
import org.wysko.autogeokt.geospatial.Ray
import org.wysko.autogeokt.operation.cogo.DirectionDirectionIntersection
import org.wysko.autogeokt.operation.cogo.DistanceDistanceIntersection
import kotlin.test.assertEquals

class TestCogo {
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

    @Test
    fun `DirectionDirectionIntersection should work with a Ghilani example`() {
        // Oracle: Elementary Surveying, Ghilani 11.2
        val ray1 = Ray(
            Cartesian2D(1425.07, 1971.28),
            DegreesMinutesSeconds(76, 4, 24.0)
        )

        val ray2 = Ray(
            Cartesian2D(7484.8, 5209.64),
            DegreesMinutesSeconds(141, 30, 16.0)
        )

        val intersection = DirectionDirectionIntersection(Pair(ray1, ray2))

        assertEquals(8637.85, intersection.result.x, 0.01)
        assertEquals(3759.83, intersection.result.y, 0.01)
    }
}