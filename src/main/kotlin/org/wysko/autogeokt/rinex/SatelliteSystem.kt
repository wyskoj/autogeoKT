package org.wysko.autogeokt.rinex

/**
 * A satellite system, i.e., constellation. This class is used to represent a satellite system in a RINEX file.
 *
 * @property system The system code.
 */
@Suppress("unused")
sealed class SatelliteSystem(val system: Char) {

    /**
     * The GPS satellite system. Run by the United States.
     */
    data object Gps : SatelliteSystem('G')

    /**
     * The GLONASS satellite system. Run by Russia.
     */
    data object Glonass : SatelliteSystem('R')

    /**
     * The Geostationary Signal Payload satellite system.
     */
    data object GeostationarySignalPayload : SatelliteSystem('S')

    /**
     * The Galileo satellite system. Run by the European Union.
     */
    data object Galileo : SatelliteSystem('E')

    /**
     * Mixed satellite system.
     */
    data object Mixed : SatelliteSystem('M')

    companion object {
        /**
         * Converts a RINEX satellite system code to a [SatelliteSystem].
         *
         * @param char The RINEX satellite system code.
         * @return The corresponding [SatelliteSystem].
         */
        fun fromChar(char: Char): SatelliteSystem {
            if (char.isWhitespace()) return Gps
            return SatelliteSystem::class.sealedSubclasses
                .map { it.objectInstance!! }
                .first {
                    it.system == char
                }
        }
    }
}