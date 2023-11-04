package org.wysko.autogeokt.operation.cogo

import org.wysko.autogeokt.geospatial.Cartesian2D
import org.wysko.autogeokt.geospatial.Circle
import org.wysko.autogeokt.operation.Operation
import org.wysko.autogeokt.operation.OperationData
import org.wysko.autogeokt.operation.OperationResult
import org.wysko.autogeokt.operation.PropertyTitle
import kotlin.math.*
import kotlin.reflect.KProperty

/**
 * Computes the intersection of two circles. The result is a pair of two points, which are the intersection points.
 *
 * If the circles don't intersect, an [IllegalArgumentException] is thrown.
 * If the circles are identical in both center and radius, an [IllegalArgumentException] is thrown.
 */
data class DistanceDistanceIntersection(
    override val data: DistanceDistanceIntersectionData
) : Operation<DistanceDistanceIntersectionData, DistanceDistanceIntersectionResult> {
    override val result by lazy {
        // Circles mustn't be identical.
        require(data.circle1 != data.circle2) {
            "Circles must not be identical"
        }

        // Distance between stations.
        val distanceBetweenCenters = data.circle1.center distanceTo data.circle2.center

        // Azimuth from station 1 to station 2.
        val centerToCenterAzimuth = data.circle1.center azimuthTo data.circle2.center

        // Angle P2_A_P1
        val angle =
            acos((distanceBetweenCenters.pow(2) + data.circle1.radius.pow(2) - data.circle2.radius.pow(2)) / (2 * distanceBetweenCenters * data.circle1.radius))

        // Two solutions for the azimuth to intersection point.
        val solution1Azimuth = centerToCenterAzimuth + angle
        val solution2Azimuth = centerToCenterAzimuth - angle

        // Compute the first solution.
        val solution1 = Cartesian2D(
            data.circle1.center.x + data.circle1.radius * sin(solution1Azimuth),
            data.circle1.center.y + data.circle1.radius * cos(solution1Azimuth),
        )

        // Compute the second solution.
        val solution2 = Cartesian2D(
            data.circle1.center.x + data.circle1.radius * sin(solution2Azimuth),
            data.circle1.center.y + data.circle1.radius * cos(solution2Azimuth),
        )

        require(!solution1.x.isNaN()) {
            "No solution found for intersection of circles ${data.circle1} and ${data.circle2}"
        }

        DistanceDistanceIntersectionResult(solution1, solution2)
    }
}

data class DistanceDistanceIntersectionData(
    @PropertyTitle("Circle 1")
    val circle1: Circle,
    @PropertyTitle("Circle 2")
    val circle2: Circle
) : OperationData() {
    override val propertyOrder: List<KProperty<*>> = listOf(
        DistanceDistanceIntersectionData::circle1,
        DistanceDistanceIntersectionData::circle2
    )
}

data class DistanceDistanceIntersectionResult(
    @PropertyTitle("Solution 1")
    val solution1: Cartesian2D,
    @PropertyTitle("Solution 2")
    val solution2: Cartesian2D
) : OperationResult() {
    override val propertyOrder: List<KProperty<*>> = listOf(
        DistanceDistanceIntersectionResult::solution1,
        DistanceDistanceIntersectionResult::solution2
    )
}