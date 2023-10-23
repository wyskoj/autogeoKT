package org.wysko.autogeokt.geospatial

/**
 * A point and a direction.
 *
 * @property point The point.
 * @property direction The direction, in [DegreesMinutesSeconds].
 */
data class Ray(
    val point: Cartesian2D,
    val direction: DegreesMinutesSeconds
)