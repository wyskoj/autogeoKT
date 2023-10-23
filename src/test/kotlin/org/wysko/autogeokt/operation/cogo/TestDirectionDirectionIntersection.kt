package org.wysko.autogeokt.operation.cogo

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.wysko.autogeokt.geospatial.Cartesian2D
import org.wysko.autogeokt.geospatial.DegreesMinutesSeconds
import org.wysko.autogeokt.geospatial.Ray
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals

class TestDirectionDirectionIntersection {
    @Test
    fun `It should work with a Ghilani example`() {
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

    @Test
    fun `It should work with random values`() {
        val ray1 = Ray(
            Cartesian2D(3245.87,5682.91),
            DegreesMinutesSeconds(23,10,45.0),
        )

        val ray2 = Ray(
            Cartesian2D(1185.22,2891.50),
            DegreesMinutesSeconds(155,42,36.0),
        )

        val intersection = DirectionDirectionIntersection(Pair(ray1, ray2))

        assertEquals(1629.330, intersection.result.x, 0.001)
        assertEquals(1907.448, intersection.result.y, 0.001)
    }

    @Test
    fun `It should work with perpendicular rays`() {
        val ray1 = Ray(
            Cartesian2D(1000.0, 2000.0),
            DegreesMinutesSeconds(0, 0, 0.0)
        )

        val ray2 = Ray(
            Cartesian2D(2000.0, 3000.0),
            DegreesMinutesSeconds(90, 0, 0.0)
        )

        val intersection = DirectionDirectionIntersection(Pair(ray1, ray2))

        assertEquals(1000.0, intersection.result.x, 0.01)
        assertEquals(3000.0, intersection.result.y, 0.01)
    }

    @Test
    fun `It should error when there is no solution because of equal azimuths`() {
        val ray1 = Ray(
            Cartesian2D(3245.87,5682.91),
            DegreesMinutesSeconds(23,10,45.0),
        )

        val ray2 = Ray(
            Cartesian2D(1185.22,2891.50),
            DegreesMinutesSeconds(23,10,45.0),
        )

        val intersection = DirectionDirectionIntersection(Pair(ray1, ray2))

        val exception = assertThrows<IllegalArgumentException> { intersection.result }
        assertEquals("The directions must not be parallel.", exception.message)
    }

    @Test
    fun `It should error when there is no solution because of reverse azimuths`() {
        val ray1 = Ray(
            Cartesian2D(3245.87,5682.91),
            DegreesMinutesSeconds(23,10,45.0),
        )

        val ray2 = Ray(
            Cartesian2D(1185.22,2891.50),
            DegreesMinutesSeconds(203,10,45.0),
        )

        val intersection = DirectionDirectionIntersection(Pair(ray1, ray2))

        val exception = assertThrows<IllegalArgumentException> { intersection.result }
        assertEquals("The directions must not be parallel.", exception.message)
    }

    @Test
    fun `It should error when both rays start from the same point`() {
        val ray1 = Ray(
            Cartesian2D(1000.0, 2000.0),
            DegreesMinutesSeconds(45, 0, 0.0)
        )

        val ray2 = Ray(
            Cartesian2D(1000.0, 2000.0),
            DegreesMinutesSeconds(135, 0, 0.0)
        )

        val intersection = DirectionDirectionIntersection(Pair(ray1, ray2))

        val exception = assertThrows<IllegalArgumentException> { intersection.result }
        assertEquals("Points must not be coincident.", exception.message)
    }

    @Test
    fun `It should error when both rays are exactly the same`() {
        val ray1 = Ray(
            Cartesian2D(1000.0, 2000.0),
            DegreesMinutesSeconds(45, 0, 0.0)
        )

        val ray2 = Ray(
            Cartesian2D(1000.0, 2000.0),
            DegreesMinutesSeconds(45, 0, 0.0)
        )

        val intersection = DirectionDirectionIntersection(Pair(ray1, ray2))

        assertThrows<IllegalArgumentException> { intersection.result }
    }
}