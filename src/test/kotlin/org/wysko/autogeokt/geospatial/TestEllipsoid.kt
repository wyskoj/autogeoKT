package org.wysko.autogeokt.geospatial

import kotlin.test.Test
import kotlin.test.assertEquals

class TestEllipsoid {

    @Test
    fun `Should return correct eccentricity`() {
        assertEquals(0.081_819_190_84, Ellipsoid.WGS84.eccentricity, 0.000_000_000_01)
        assertEquals(0.081_819_191_04, Ellipsoid.GRS80.eccentricity, 0.000_000_000_01)
        assertEquals(0.082_271_854_22, Ellipsoid.CLARKE_1866.eccentricity, 0.000_000_000_01)
    }

    @Test
    fun `Should return correct flattening`() {
        assertEquals(0.003_352_810_665, Ellipsoid.WGS84.flattening, 0.000_000_000_001)
        assertEquals(0.003_352_810_681, Ellipsoid.GRS80.flattening, 0.000_000_000_001)
        assertEquals(0.003_390_075_304, Ellipsoid.CLARKE_1866.flattening, 0.000_000_000_001)
    }

    @Test
    fun `Should return correct inverse flattening`() {
        assertEquals(298.257_223_5, Ellipsoid.WGS84.inverseFlattening, 0.000_000_1)
        assertEquals(298.257_222_1, Ellipsoid.GRS80.inverseFlattening, 0.000_000_1)
        assertEquals(294.978_698_2, Ellipsoid.CLARKE_1866.inverseFlattening, 0.000_000_1)
    }

    @Test
    fun `Should return correct eccentricity squared`() {
        assertEquals(0.006_694_379_991, Ellipsoid.WGS84.eccentricitySquared, 0.000_000_000_01)
        assertEquals(0.006_694_380_023, Ellipsoid.GRS80.eccentricitySquared, 0.000_000_000_01)
        assertEquals(0.006_768_657_998, Ellipsoid.CLARKE_1866.eccentricitySquared, 0.000_000_000_01)
    }

    @Test
    fun `Should return correct second eccentricity`() {
        assertEquals(0.082_094_437_95, Ellipsoid.WGS84.secondEccentricity, 0.000_000_000_01)
        assertEquals(0.082_094_438_15, Ellipsoid.GRS80.secondEccentricity, 0.000_000_000_01)
        assertEquals(0.082_551_710_74, Ellipsoid.CLARKE_1866.secondEccentricity, 0.000_000_000_01)
    }
}