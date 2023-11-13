package org.wysko.autogeokt.geospatial

import kotlinx.serialization.Serializable
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A point in 2D space.
 *
 * @property x The x (easting) coordinate.
 * @property y The y (northing) coordinate.
 */
@Serializable
class Cartesian2D(
    val x: Double,
    val y: Double,
) {
    /**
     * Calculates the Euclidean distance between two [Cartesian2D] points.
     *
     * @param other The Cartesian2D point to calculate the distance to.
     * @return The distance between this point and the other point.
     */
    infix fun distanceTo(other: Cartesian2D) = sqrt((x - other.x).pow(2) + (y - other.y).pow(2))

    /**
     * Calculates the azimuth from this point to another point.
     *
     * @param other The Cartesian2D point to calculate the azimuth to.
     * @throws IllegalArgumentException if the two points are coincident.
     * @return The azimuth from this point to the other point, in radians.
     */
    infix fun azimuthTo(other: Cartesian2D): Double {
        val dX = other.x - this.x
        val dY = other.y - this.y

        require(!(dX == 0.0 && dY == 0.0)) { "Points must not be coincident." }

        return (PI / 2 - atan2(dY, dX)).let { azimuth ->
            if (azimuth < 0.0) {
                azimuth + 2 * PI
            } else {
                azimuth
            }
        }
    }

    override fun toString(): String = "(x=$x, y=$y)"
}
