package org.wysko.autogeokt.geospatial

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A mathematical model of the Earth. It is the revolution of an ellipse with major axis [a] and minor axis [b] around
 * the polar axis.
 *
 * @property a The semi-major axis of the ellipsoid.
 * @property b The semi-minor axis of the ellipsoid.
 */
data class Ellipsoid(
    val a: Double,
    val b: Double,
    val name: String = ""
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


    companion object {
        /**
         * The WGS84 ellipsoid.
         */
        val WGS84 = Ellipsoid(6378137.0, 6356752.314245179, "WGS84")

        /**
         * The GRS80 ellipsoid.
         */
        val GRS80 = Ellipsoid(6378137.0, 6356752.314140348, "GRS80")

        /**
         * The Clarke 1866 ellipsoid.
         */
        val CLARKE_1866 = Ellipsoid(6378206.4, 6356583.8, "Clarke 1866")
    }
}