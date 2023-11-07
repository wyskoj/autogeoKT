package org.wysko.autogeokt.geospatial

import kotlinx.serialization.Serializable

/**
 * An angle represented in degrees, minutes, and seconds format.
 *
 * A degree is divided into 60 minutes, and a minute is divided into 60 seconds.
 *
 * @property degrees The degrees portion of the angle.
 * @property minutes The minutes portion of the angle.
 * @property seconds The seconds portion of the angle.
 */
@Serializable
data class DegreesMinutesSeconds(
    val degrees: Int,
    val minutes: Int,
    val seconds: Double,
) {
    /**
     * Converts the angle to radians.
     *
     * @return The angle in radians.
     */
    fun toRadians(): Double = toRadians(degrees, minutes, seconds)

    override fun toString(): String = "$degrees° $minutes' $seconds\""

    companion object {
        /**
         * Converts a [DegreesMinutesSeconds] angle to radians.
         *
         * @param degrees The degrees of the angle.
         * @param minutes The minutes of the angle.
         * @param seconds The seconds of the angle.
         * @return The angle in radians.
         */
        @Suppress("MagicNumber")
        fun toRadians(degrees: Int, minutes: Int, seconds: Double): Double =
            Math.toRadians(degrees + minutes / 60.0 + seconds / 3600.0)
    }
}
