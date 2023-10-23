package org.wysko.autogeokt.operation.cogo

import org.wysko.autogeokt.geospatial.Cartesian2D
import org.wysko.autogeokt.geospatial.Ray
import org.wysko.autogeokt.operation.Operation
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * One tenth of an arc-second, in radians.
 */
private const val DECI_ARC_SECOND = 0.000000484

data class DirectionDirectionIntersection(
    override val data: Pair<Ray, Ray>
) : Operation<Pair<Ray, Ray>, Cartesian2D> {
    override val result by lazy {
        require (
            abs((data.first.direction.toRadians() % Math.PI) - (data.second.direction.toRadians() % Math.PI)) >=
                    DECI_ARC_SECOND
        ) {
            // If tighter than 0.1" (0.000000484 radians), let's call it parallel.
            "The directions must not be parallel."
        }

        require(data.first.point != data.second.point) {
            "Points must not be coincident."
        }

        val pointsDistance = data.first.point distanceTo data.second.point
        val pointsAzimuth = data.first.point azimuthTo data.second.point

        val A = data.first.direction.toRadians() - pointsAzimuth
        val B = Math.PI + pointsAzimuth - data.second.direction.toRadians()
        val P = Math.PI - A - B

        val AP = pointsDistance * (sin(B) / sin(P))

        val x = data.first.point.x + AP * sin(data.first.direction.toRadians())
        val y = data.first.point.y + AP * cos(data.first.direction.toRadians())

        Cartesian2D(x, y)
    }
}