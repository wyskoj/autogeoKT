package org.wysko.autogeokt.operation.geodeticcomputations

import org.wysko.autogeokt.geospatial.DegreesMinutesSeconds
import org.wysko.autogeokt.geospatial.Ellipsoid
import org.wysko.autogeokt.operation.Operation
import java.util.Optional
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Computes the radii of curvature of the ellipsoid at a given latitude and azimuth.
 */
data class Radii(
    override val data: RadiiData
) : Operation<RadiiData, RadiiResult> {
    override val result: RadiiResult by lazy {
        val radiusMeridian = calculateRadiusMeridian()
        val radiusPrimeVertical = calculateRadiusPrimeVertical()
        val radiusAzimuth = calculateRadiusAzimuth(radiusMeridian, radiusPrimeVertical)
        RadiiResult(radiusPrimeVertical, radiusMeridian, radiusAzimuth)
    }

    private fun calculateRadiusMeridian(): Double =
        (data.ellipsoid.a * (1 - data.ellipsoid.eccentricitySquared)) / (1 - data.ellipsoid.eccentricitySquared * sin(
            data.latitude.toRadians()
        ).pow(2)).pow(1.5)

    private fun calculateRadiusPrimeVertical(): Double =
        data.ellipsoid.a / sqrt(1 - data.ellipsoid.eccentricitySquared * sin(data.latitude.toRadians()).pow(2))

    private fun calculateRadiusAzimuth(radiusMeridian: Double, radiusPrimeVertical: Double): Optional<Double> =
        data.azimuth.map { azimuth ->
            1 / (cos(azimuth.toRadians()).pow(2) / radiusMeridian + sin(azimuth.toRadians()).pow(2) / radiusPrimeVertical)
        }
}

data class RadiiData(
    val ellipsoid: Ellipsoid,
    val latitude: DegreesMinutesSeconds,
    val azimuth: Optional<DegreesMinutesSeconds>
)

data class RadiiResult(
    val radiusPrimeVertical: Double,
    val radiusMeridian: Double,
    val radiusAzimuth: Optional<Double>
)