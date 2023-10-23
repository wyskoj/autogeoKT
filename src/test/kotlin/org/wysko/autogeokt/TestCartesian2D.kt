package org.wysko.autogeokt

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.wysko.autogeokt.geospatial.Cartesian2D
import org.wysko.autogeokt.geospatial.DegreesMinutesSeconds.Companion.toRadians
import kotlin.test.Test
import kotlin.test.assertEquals

class TestCartesian2D {
    private lateinit var p1: Cartesian2D

    @BeforeEach
    fun setUp() {
        p1 = Cartesian2D(0.0, 0.0)
    }

    @Test
    fun `Inverse azimuth in various quadrants and directions`() {
        val testData = listOf(
            TestDataPoint(Cartesian2D(100.0, 100.0), toRadians(45, 0, 0.0), "quadrant 1"),
            TestDataPoint(Cartesian2D(100.0, -100.0), toRadians(135, 0, 0.0), "quadrant 2"),
            TestDataPoint(Cartesian2D(-100.0, -100.0), toRadians(225, 0, 0.0), "quadrant 3"),
            TestDataPoint(Cartesian2D(-100.0, 100.0), toRadians(315, 0, 0.0), "quadrant 4"),
            TestDataPoint(Cartesian2D(0.0, 100.0), toRadians(0, 0, 0.0), "due north"),
            TestDataPoint(Cartesian2D(0.0, -100.0), toRadians(180, 0, 0.0), "due south"),
            TestDataPoint(Cartesian2D(100.0, 0.0), toRadians(90, 0, 0.0), "due east"),
            TestDataPoint(Cartesian2D(-100.0, 0.0), toRadians(270, 0, 0.0), "due west")
        )

        testData.forEach { testDataPoint ->
            assertEquals(testDataPoint.expectedAzimuth, p1 azimuthTo testDataPoint.point, 0.0001, "Failed for ${testDataPoint.direction}")
        }
    }

    @Test
    fun `Inverse azimuth fails for coincident points`() {
        val p2 = Cartesian2D(0.0, 0.0)
        assertThrows<IllegalArgumentException> { p1 azimuthTo p2 }
    }

    @Test
    fun `Inverse distance should work`() {
        val p2 = Cartesian2D(100.0, 100.0)
        assertEquals(141.421356, p1 distanceTo p2, 0.000001)
    }
}

/**
 * A test data point for testing the inverse azimuth calculation.
 *
 * @property point The point to calculate the azimuth to.
 * @property expectedAzimuth The expected azimuth to the point.
 * @property direction A description of the direction to the point.
 */
private data class TestDataPoint(val point: Cartesian2D, val expectedAzimuth: Double, val direction: String)