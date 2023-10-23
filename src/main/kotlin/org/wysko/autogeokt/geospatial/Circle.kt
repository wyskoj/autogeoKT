package org.wysko.autogeokt.geospatial

/**
 * A circle is defined by its [center] and [radius]. It is the set of all points in a plane that are at a given
 * distance from a given point.
 *
 * @property center The center point of the circle.
 * @property radius The radius of the circle.
 */
data class Circle(
    val center: Cartesian2D,
    val radius: Double
)