@file:UseSerializers(D1Serializer::class, D2Serializer::class)

package org.wysko.autogeokt.serialization

import kotlinx.serialization.UseSerializers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.wysko.autogeokt.geospatial.Cartesian2D
import org.wysko.autogeokt.geospatial.Cartesian3D
import org.wysko.autogeokt.geospatial.Circle
import org.wysko.autogeokt.geospatial.DegreesMinutesSeconds
import org.wysko.autogeokt.geospatial.Ellipsoid
import org.wysko.autogeokt.geospatial.HelmertTransformation
import kotlin.test.assertEquals

class TestSerializeSpecialTypes {
    private lateinit var json: Json

    @BeforeEach
    fun setUp() {
        json = Json { encodeDefaults = true }
    }

    @Test
    fun `Test serialize Cartesian2D`() {
        val cartesian2D = Cartesian2D(602.11, 853.45)
        val serialized = json.encodeToString(cartesian2D)
        val deserialized = json.decodeFromString<Cartesian2D>(serialized)
        assertEquals(cartesian2D, deserialized, "Cartesian2D was not serialized and deserialized correctly.")
    }

    @Test
    fun `Test serialize Cartesian3D`() {
        val cartesian3D = Cartesian3D(697.57, 101.08, 870.02)
        val serialized = json.encodeToString(cartesian3D)
        val deserialized = json.decodeFromString<Cartesian3D>(serialized)
        assertEquals(cartesian3D, deserialized, "Cartesian3D was not serialized and deserialized correctly.")
    }

    @Test
    fun `Test serialize Circle`() {
        val circle = Circle(Cartesian2D(971.06, 429.19), 474.58)
        val serialized = json.encodeToString(circle)
        val deserialized = json.decodeFromString<Circle>(serialized)
        assertEquals(circle, deserialized, "Circle was not serialized and deserialized correctly.")
    }

    @Test
    fun `Test serialize DegreesMinutesSeconds`() {
        val degreesMinutesSeconds = DegreesMinutesSeconds(45, 30, 1.64)
        val serialized = json.encodeToString(degreesMinutesSeconds)
        val deserialized = json.decodeFromString<DegreesMinutesSeconds>(serialized)
        assertEquals(
            degreesMinutesSeconds,
            deserialized,
            "DegreesMinutesSeconds was not serialized and deserialized correctly.",
        )
    }

    @Test
    fun `Test serialize Ellipsoid`() {
        val serialized = json.encodeToString(Ellipsoid.WGS84)
        val deserialized = json.decodeFromString<Ellipsoid>(serialized)
        assertEquals(Ellipsoid.WGS84, deserialized, "Ellipsoid was not serialized and deserialized correctly.")
    }

    @Test
    fun `Test serialize HelmertTransformation`() {
        val transformation = HelmertTransformation(
            translation = mk.ndarray(
                mk[
                    mk[929.89],
                    mk[471.18],
                    mk[895.81],
                ],
            ),
            rotation = mk.ndarray(mk[883.42, 433.16, 9.95]),
            scaleFactor = 364.65,
        )
        val serialized = json.encodeToString(transformation).also {
            println(it)
        }
        val deserialized = json.decodeFromString<HelmertTransformation>(serialized)
        assertEquals(
            transformation,
            deserialized,
            "HelmertTransformation was not serialized and deserialized correctly.",
        )
    }

    @Test
    fun `Test serialize D2Array`() {
        val array = mk.ndarray(
            mk[
                mk[1.0, 2.0, 3.0],
                mk[4.0, 5.0, 6.0],
                mk[7.0, 8.0, 9.0],

            ],
        )
        val string = json.encodeToString(D2ArraySerializer, array)
        val decoded = json.decodeFromString(D2ArraySerializer, string)
        assertEquals(array, decoded)
    }

    @Test
    fun `Test serialize D1Array`() {
        val array = mk.ndarray(mk[1.0, 2.0, 3.0])
        val string = json.encodeToString(D1ArraySerializer, array)
        val decoded = json.decodeFromString(D1ArraySerializer, string)
        assertEquals(array, decoded)
    }
}
