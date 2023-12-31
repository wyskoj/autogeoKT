package org.wysko.autogeokt.geospatial

import kotlinx.serialization.Serializable
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A mathematical model of the Earth. It is the revolution of an ellipse with major axis [a] and minor axis [b] around
 * the polar axis.
 *
 * @property a The semi-major axis of the ellipsoid.
 * @property b The semi-minor axis of the ellipsoid.
 * @property name The name of the ellipsoid.
 */
@Serializable
data class Ellipsoid(
    val a: Double,
    val b: Double,
    val name: String = "",
) {
    /**
     * The flattening of the ellipsoid.
     */
    val flattening: Double by lazy { (a - b) / a }

    /**
     * The inverse flattening of the ellipsoid.
     */
    val inverseFlattening: Double by lazy { 1 / flattening }

    /**
     * The eccentricity, or `e`, of the ellipsoid.
     */
    val eccentricity: Double by lazy { sqrt(1 - (b / a).pow(2)) }

    /**
     * The eccentricity squared, or `e^2`, of the ellipsoid.
     */
    val eccentricitySquared: Double by lazy { eccentricity.pow(2) }

    /**
     * The eccentricity prime squared, or `e'^2`, of the ellipsoid.
     */
    val secondEccentricity: Double by lazy { sqrt(a.pow(2) / b.pow(2) - 1) }

    override fun toString(): String = name

    companion object {

        /**
         * The "World Geodetic System of 1984" ellipsoid.
         */
        val WGS84 = Ellipsoid(6_378_137.0, 6_356_752.314245179, "WGS84")

        /**
         * The GRS80 ellipsoid.
         */
        val GRS80 = Ellipsoid(6_378_137.0, 6_356_752.314140348, "GRS80")

        /**
         * The Clarke 1866 ellipsoid.
         */
        val CLARKE_1866 = Ellipsoid(6_378_206.4, 6_356_583.8, "Clarke 1866")
    }
}
