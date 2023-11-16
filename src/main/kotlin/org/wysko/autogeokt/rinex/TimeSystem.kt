package org.wysko.autogeokt.rinex

/**
 * A time system.
 *
 * @property code The code.
 */
@Suppress("unused")
sealed class TimeSystem(val code: String) {
    /**
     * The GPS time system.
     */
    data object Gps : TimeSystem("GPS")

    /**
     * The GLONASS time system (UTC).
     */
    data object Glonass : TimeSystem("GLO")

    /**
     * The Galileo time system.
     */
    data object Galileo : TimeSystem("GAL")

    companion object {
        /**
         * Converts a RINEX time system code to a [TimeSystem].
         *
         * @param code The RINEX time system code.
         * @return The corresponding [TimeSystem].
         */
        fun fromString(code: String): TimeSystem {
            return TimeSystem::class.sealedSubclasses
                .map { it.objectInstance!! }
                .first { it.code == code }
        }
    }
}