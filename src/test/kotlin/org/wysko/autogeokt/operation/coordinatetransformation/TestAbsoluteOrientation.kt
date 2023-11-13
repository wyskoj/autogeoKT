package org.wysko.autogeokt.operation.coordinatetransformation

import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.wysko.autogeokt.geospatial.Cartesian3D
import org.wysko.autogeokt.operation.AbsoluteOrientation
import org.wysko.autogeokt.operation.AbsoluteOrientationData
import kotlin.test.assertEquals

private const val ONE_ARC_SECOND = 1 / 206_204.8062
private const val ONE_MILLIMETER = 0.001

class TestAbsoluteOrientation {
    private lateinit var groundPoints: List<Cartesian3D>
    private lateinit var arbPoints: List<Cartesian3D>

    @BeforeEach
    fun setUp() {
        groundPoints = """
            333180.322  3102960.870 2.405
            333208.52   3102906.916 2.194
            333212.708  3102954.894 2.791
            333158.723  3102891.875 2.050
        """.trimIndent()
            .split("\n")
            .filter { it.isNotBlank() }
            .map { line -> line.split(Regex("""\s+""")).filter { it.isNotBlank() }.map { it.toDouble() } }
            .map {
                Cartesian3D(it[0], it[1], it[2])
            }

        arbPoints = """
            180.943162787145          100.381734653823         -340.246470472667
            284.467017313431         -142.681399965004         -368.303790156785
            312.889064601158             64.1666629089         -377.138983879985
            73.4469339269127         -199.362115123118         -306.056136003108
        """.trimIndent()
            .split("\n")
            .filter { it.isNotBlank() }
            .map { line -> line.split(Regex("""\s+""")).filter { it.isNotBlank() }.map { it.toDouble() } }
            .map {
                Cartesian3D(it[0], it[1], it[2])
            }
    }

    @Test
    fun `Test absolute orientation`() {
        val data = AbsoluteOrientationData(
            groundPoints,
            arbPoints,
        )

        val result = AbsoluteOrientation(data).result
        assertEquals(-0.014229567594644, result.transformation.rotation[0], ONE_ARC_SECOND)
        assertEquals(0.28686584680227, result.transformation.rotation[1], ONE_ARC_SECOND)
        assertEquals(-0.051142030821033, result.transformation.rotation[2], ONE_ARC_SECOND)

        assertEquals(3.331205205141926E5, result.transformation.translation[0, 0], ONE_MILLIMETER)
        assertEquals(3.102934094664197E6, result.transformation.translation[1, 0], ONE_MILLIMETER)
        assertEquals(64.95674411219184, result.transformation.translation[2, 0], ONE_MILLIMETER)

        assertEquals(0.228421926223749, result.transformation.scaleFactor, 0.00001)
    }
}
