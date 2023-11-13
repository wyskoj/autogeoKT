package org.wysko.autogeokt.geospatial

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestHelmertTransformation {

    private fun pointsEqual(p1: Cartesian3D, p2: Cartesian3D) {
        assertEquals(p1.x, p2.x, 0.00001, "X coordinates are not equal")
        assertEquals(p1.y, p2.y, 0.00001, "Y coordinates are not equal")
        assertEquals(p1.z, p2.z, 0.00001, "Z coordinates are not equal")
    }

    @Test
    fun `Test transform point`() {
        val point = Cartesian3D(721.07, 305.13, 567.12)
        val transformation = HelmertTransformation(
            translation = mk.ndarray(
                mk[
                    mk[585.65],
                    mk[112.50],
                    mk[288.07],
                ],
            ),
            rotation = mk.ndarray(mk[56.72, 331.24, 575.36]),
            scaleFactor = 254.28,
        )
        val expected = Cartesian3D(-124_577.296731132, -51_782.6421476419, -204_834.842263831)
        val actual = transformation.transformPoint(point)

        pointsEqual(expected, actual)
    }
}
