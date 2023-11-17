package org.wysko.autogeokt.rinex

import org.wysko.autogeokt.get

/**
 * A satellite is represented by its space vehicle number and the satellite system it belongs to.
 *
 * @property satelliteSystem The satellite system, i.e., constellation.
 * @property satelliteNumber The satellite number.
 */
data class Satellite(
    val satelliteSystem: SatelliteSystem,
    val satelliteNumber: Int,
) {
    companion object {
        /**
         * Converts a space vehicle ID to a Satellite object.
         *
         * @param id The space vehicle ID to convert.
         * @return The Satellite object representing the space vehicle, or null if the vehicle number is 0.
         */
        fun fromSpaceVehicleId(id: String): Satellite? {
            val vehicleNumber = id[1..2].trim().toInt()
            return if (vehicleNumber == 0) null else Satellite(SatelliteSystem.fromChar(id[0]), vehicleNumber)
        }
    }
}
