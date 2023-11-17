package org.wysko.autogeokt.geospatial

import kotlinx.serialization.Serializable

typealias DMS = DegreesMinutesSeconds

private const val MINUTES_IN_DEGREE = 60.0

private const val SECONDS_IN_DEGREE = 3600.0

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

    constructor(radians: Double) : this(
        degrees = Math.toDegrees(radians).toInt(),
        minutes = (Math.toDegrees(radians) % 1 * 60).toInt(),
        seconds = Math.toDegrees(radians) % 1 * 60 % 1 * 60,
    )

    /**
     * Converts the angle to radians.
     *
     * @return The angle in radians.
     */
    fun toRadians(): Double = toRadians(degrees, minutes, seconds)

    /**
     * Converts the angle to decimal degrees.
     */
    fun toDegrees(): Double = degrees + minutes / MINUTES_IN_DEGREE + seconds / SECONDS_IN_DEGREE

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
            Math.toRadians(degrees + minutes / MINUTES_IN_DEGREE + seconds / SECONDS_IN_DEGREE)
    }
}
