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
}