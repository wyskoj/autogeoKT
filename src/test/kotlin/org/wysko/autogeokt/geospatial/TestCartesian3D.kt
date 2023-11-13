package org.wysko.autogeokt.geospatial

import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestCartesian3D {

    private lateinit var examplePoints: List<Cartesian3D>

    @BeforeEach
    fun setUp() {
        // XYZ in rows
        examplePoints = """
            840.93  280.68  802.52
            73.23   334.80  11.38
            367.96  213.43  812.62
        """.trimIndent().split("\n").map { line ->
            line.split(Regex("""\s+""")).map { it.toDouble() }.let {
                Cartesian3D(x = it[0], y = it[1], z = it[2])
            }
        }
    }

    @Test
    fun `Centroid should work`() {
        examplePoints.centroid().let {
            assertEquals(427.373, it[0, 0], 0.001, "Centroid x should be correct")
            assertEquals(276.303, it[1, 0], 0.001, "Centroid y should be correct")
            assertEquals(542.173, it[2, 0], 0.001, "Centroid z should be correct")
        }
    }
}
