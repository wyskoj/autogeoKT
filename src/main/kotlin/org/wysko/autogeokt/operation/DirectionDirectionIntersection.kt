package org.wysko.autogeokt.operation

import org.wysko.autogeokt.geospatial.Cartesian2D
import org.wysko.autogeokt.geospatial.Ray
import org.wysko.autogeokt.gui.form.FormDetails
import org.wysko.autogeokt.gui.form.Formable
import org.wysko.autogeokt.gui.form.components.FormField
import org.wysko.autogeokt.gui.form.components.InputData
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.reflect.KProperty

private const val DECI_ARC_SECOND = 0.000000484

/**
 * Computes the intersection of two rays.
 */
data class DirectionDirectionIntersection(
    override val data: DirectionDirectionIntersectionData,
) : Operation<DirectionDirectionIntersectionData, DirectionDirectionIntersectionResult>() {
    override val result by lazy {
        require(
            abs(data.ray1.direction.toRadians() % Math.PI - data.ray2.direction.toRadians() % Math.PI) >=
                DECI_ARC_SECOND,
        ) {
            // If tighter than 0.1" (0.000000484 radians), let's call it parallel.
            "The directions must not be parallel."
        }

        require(data.ray1.point != data.ray2.point) {
            "Points must not be coincident."
        }

        val pointsDistance = data.ray1.point distanceTo data.ray2.point
        val pointsAzimuth = data.ray1.point azimuthTo data.ray2.point

        val angleA = data.ray1.direction.toRadians() - pointsAzimuth
        val angleB = Math.PI + pointsAzimuth - data.ray2.direction.toRadians()
        val angleP = Math.PI - angleA - angleB

        val distanceAP = pointsDistance * (sin(angleB) / sin(angleP))

        val x = data.ray1.point.x + distanceAP * sin(data.ray1.direction.toRadians())
        val y = data.ray1.point.y + distanceAP * cos(data.ray1.direction.toRadians())

        DirectionDirectionIntersectionResult(Cartesian2D(x, y))
    }

    companion object : Formable {
        override val form = FormDetails(
            formContents = listOf(
                FormField.RayInput(
                    InputData(
                        name = "ray1",
                        title = "Station 1",
                        description = "Enter the first station.",
                    ),
                ),
                FormField.RayInput(
                    InputData(
                        name = "ray2",
                        title = "Station 2",
                        description = "Enter the second station.",
                    ),
                ),
            ),
            toData = { DirectionDirectionIntersectionData(it["ray1"] as Ray, it["ray2"] as Ray) },
            canBeTemporary = true,
            operationDetails = OPERATION_DETAILS.getValue(DirectionDirectionIntersection::class),
        )
    }
}

/**
 * @property ray1 The first ray.
 * @property ray2 The second ray.

 */
data class DirectionDirectionIntersectionData(
    @PropertyTitle("Station 1")
    val ray1: Ray,
    @PropertyTitle("Station 2")
    val ray2: Ray,
) : OperationData() {
    override val propertyOrder: List<KProperty<*>> = listOf(
        DirectionDirectionIntersectionData::ray1,
        DirectionDirectionIntersectionData::ray2,
    )
}

/**
 * @property intersectionPoint The intersection point.
 */
data class DirectionDirectionIntersectionResult(
    @PropertyTitle("Intersection")
    val intersectionPoint: Cartesian2D,
) : OperationResult() {
    override val propertyOrder: List<KProperty<*>> = listOf(
        DirectionDirectionIntersectionResult::intersectionPoint,
    )
}
