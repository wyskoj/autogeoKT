package org.wysko.autogeokt.rinex

/**
 * A satellite. This class is used to represent a satellite in a RINEX file.
 *
 * @property satelliteSystem The satellite system, i.e., constellation.
 * @property satelliteNumber The satellite number.
 */
data class Satellite(
    val satelliteSystem: SatelliteSystem,
    val satelliteNumber: Int
)