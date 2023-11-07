package org.wysko.autogeokt.geospatial

import kotlinx.serialization.Serializable
import kotlin.math.PI
import kotlin.math.pow

/**
 * A circle is defined by its [center] and [radius]. It is the set of all points in a plane that are at a given
 * distance from a given point.
 *
 * @property center The center point of the circle.
 * @property radius The radius of the circle.
 */
@Serializable
data class Circle(
    val center: Cartesian2D,
    val radius: Double,
) {
    /** The diameter of the circle, which is twice the radius. */
    val diameter = radius * 2

    /** The area of the circle. */
    val area = PI * radius.pow(2)

    override fun toString(): String = "(center=$center, radius=$radius)"
}
