package org.wysko.autogeokt.geospatial

data class DegreesMinutesSeconds(
    val degrees: Int,
    val minutes: Int,
    val seconds: Double
) {
    /**
     * Converts the angle to radians.
     *
     * @return The angle in radians.
     */
    fun toRadians(): Double = toRadians(degrees, minutes, seconds)

    companion object {
        /**
         * Converts a DMS angle to radians.
         *
         * @param degrees The degrees of the angle.
         * @param minutes The minutes of the angle.
         * @param seconds The seconds of the angle.
         * @return The angle in radians.
         */
        fun toRadians(degrees: Int, minutes: Int, seconds: Double): Double =
            (degrees + (minutes / 60.0) + (seconds / 3600.0)) * (Math.PI / 180.0)
    }
}

