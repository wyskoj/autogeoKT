package org.wysko.autogeokt.geospatial

import org.wysko.autogeokt.operation.Radii
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

private const val MAX_LATITUDE_APPROXIMATION_ITERATIONS = 10
private const val APPROXIMATION_THRESHOLD = 1E-5

/**
 * Represents a point in geographic coordinates.
 *
 * @param latitude Latitude of the point
 * @param longitude Longitude of the point
 * @param height Height of the point
 */
data class GeographicCoordinates(
    val latitude: DMS,
    val longitude: DMS,
    val height: Double,
)

fun Cartesian3D.toGeographic(ellipsoid: Ellipsoid): GeographicCoordinates {
    val dp = hypot(x, y) // Distance in the xy-plane
    val longitude = atan2(y, x)

    // Iterative method to compute latitude
    var latitude = atan(z / (dp * (1 - ellipsoid.eccentricitySquared)))

    // Prevent possible deadlock by setting a maximum iteration count
    var iterationCount = 0

    while (iterationCount++ < MAX_LATITUDE_APPROXIMATION_ITERATIONS) {
        val primeVerticalRadius = Radii.radiusPrimeVertical(ellipsoid, DMS(latitude))
        val updatedLatitude = computeLatitude(z, ellipsoid.eccentricitySquared, primeVerticalRadius, latitude, dp)
        if (abs(updatedLatitude - latitude) * 206264.8062 < APPROXIMATION_THRESHOLD) {
            latitude = updatedLatitude
            break
        }
        latitude = updatedLatitude // Update iteration
    }

    // Compute height
    val height = computeHeight(DMS(latitude), dp, z, ellipsoid)

    return GeographicCoordinates(
        latitude = DMS(latitude),
        longitude = DMS(longitude),
        height = height,
    )
}

private fun computeHeight(latitude: DMS, dp: Double, z: Double, ellipsoid: Ellipsoid): Double =
    if (abs(latitude.toRadians()) < Math.PI / 4) {
        dp / cos(latitude.toRadians()) - Radii.radiusPrimeVertical(ellipsoid, latitude)
    } else {
        z / sin(latitude.toRadians()) - (1 - ellipsoid.eccentricitySquared) * Radii.radiusPrimeVertical(
            ellipsoid,
            latitude,
        )
    }

private fun computeLatitude(
    z: Double,
    eSquare: Double,
    primeVerticalRadius: Double,
    latitude: Double,
    dp: Double,
): Double =
    atan((z + eSquare * primeVerticalRadius * sin(latitude)) / dp)
