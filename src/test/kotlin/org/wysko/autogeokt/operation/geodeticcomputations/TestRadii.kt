package org.wysko.autogeokt.operation.geodeticcomputations

import org.junit.jupiter.api.Test
import org.wysko.autogeokt.geospatial.DegreesMinutesSeconds
import org.wysko.autogeokt.geospatial.Ellipsoid
import org.wysko.autogeokt.operation.Radii
import org.wysko.autogeokt.operation.RadiiData
import java.util.Optional
import kotlin.jvm.optionals.getOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TestRadii {
    @Test
    fun `Should work with an example using GRS80`() {
        val latitude = DegreesMinutesSeconds(41,0,0.0)
        val result = Radii(RadiiData(Ellipsoid.GRS80, latitude, Optional.empty())).result

        assertEquals(6387345.731, result.radiusPrimeVertical, 0.001)
        assertEquals(6362920.219, result.radiusMeridian, 0.001)
        assertNull(result.radiusAzimuth.getOrNull())
    }
    @Test
    fun `Should work with an example using GRS80 and an azimuth`() {
        val latitude = DegreesMinutesSeconds(41,0,0.0)
        val azimuth = Optional.of(DegreesMinutesSeconds(10,0,0.0))
        val result = Radii(RadiiData(Ellipsoid.GRS80, latitude, azimuth)).result

        assertEquals(6387345.731, result.radiusPrimeVertical, 0.001)
        assertEquals(6362920.219, result.radiusMeridian, 0.001)
        assertEquals(6363654.007, result.radiusAzimuth.get(), 0.001)
    }
    @Test
    fun `Should work with WGS84`() {
        val latitude = DegreesMinutesSeconds(80,0,0.0)
        val result = Radii(RadiiData(Ellipsoid.WGS84, latitude, Optional.empty())).result

        assertEquals(6398943.46, result.radiusPrimeVertical, 0.001)
        assertEquals(6397643.326, result.radiusMeridian, 0.001)
        assertNull(result.radiusAzimuth.getOrNull())
    }
    @Test
    fun `Should work with WGS84 and an azimuth`() {
        val latitude = DegreesMinutesSeconds(80,0,0.0)
        val azimuth = Optional.of(DegreesMinutesSeconds(30,0,0.0))
        val result = Radii(RadiiData(Ellipsoid.WGS84, latitude, azimuth)).result

        assertEquals(6398943.46, result.radiusPrimeVertical, 0.001)
        assertEquals(6397643.326, result.radiusMeridian, 0.001)
        assertEquals(6397968.31, result.radiusAzimuth.get(), 0.001)
    }
}