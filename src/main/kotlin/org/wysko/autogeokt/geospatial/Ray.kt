package org.wysko.autogeokt.geospatial

import kotlinx.serialization.Serializable

/**
 * A point and a direction.
 *
 * @property point The point.
 * @property direction The direction, in [DegreesMinutesSeconds].
 */
@Serializable
data class Ray(
    val point: Cartesian2D,
    val direction: DegreesMinutesSeconds,
)
