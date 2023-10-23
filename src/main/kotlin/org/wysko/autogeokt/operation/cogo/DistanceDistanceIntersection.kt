package org.wysko.autogeokt.operation.cogo

import org.wysko.autogeokt.geospatial.Cartesian2D
import org.wysko.autogeokt.geospatial.Circle
import org.wysko.autogeokt.operation.Operation
import kotlin.math.*

/**
 * Computes the intersection of two circles. The result is a pair of two points, which are the intersection points.
 *
 * If the circles don't intersect, an [IllegalArgumentException] is thrown.
 * If the circles are identical in both center and radius, an [IllegalArgumentException] is thrown.
 */
data class DistanceDistanceIntersection(
    override val data: Pair<Circle, Circle>
) : Operation<Pair<Circle, Circle>, Pair<Cartesian2D, Cartesian2D>> {
    override val result by lazy {
        // Circles mustn't be identical.
        require(data.first != data.second) {
            "Circles must not be identical"
        }

        // Distance between stations.
        val distanceBetweenCenters = data.first.center distanceTo data.second.center

        // Azimuth from station 1 to station 2.
        val centerToCenterAzimuth = data.first.center azimuthTo data.second.center

        // Angle P2_A_P1
        val angle =
            acos((distanceBetweenCenters.pow(2) + data.first.radius.pow(2) - data.second.radius.pow(2)) / (2 * distanceBetweenCenters * data.first.radius))

        // Two solutions for the azimuth to intersection point.
        val solution1Azimuth = centerToCenterAzimuth + angle
        val solution2Azimuth = centerToCenterAzimuth - angle

        // Compute the first solution.
        val solution1 = Cartesian2D(
            data.first.center.x + data.first.radius * sin(solution1Azimuth),
            data.first.center.y + data.first.radius * cos(solution1Azimuth),
        )

        // Compute the second solution.
        val solution2 = Cartesian2D(
            data.first.center.x + data.first.radius * sin(solution2Azimuth),
            data.first.center.y + data.first.radius * cos(solution2Azimuth),
        )

        require(!solution1.x.isNaN()) {
            "No solution found for intersection of circles ${data.first} and ${data.second}"
        }

        solution1 to solution2
    }
}

