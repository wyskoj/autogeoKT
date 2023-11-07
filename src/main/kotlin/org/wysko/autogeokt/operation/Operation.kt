package org.wysko.autogeokt.operation

import kotlinx.serialization.Serializable
import org.wysko.autogeokt.operation.details.OperationCategory
import kotlin.reflect.KProperty

/**
 * Abstract class for operations. It is used to store data and result of the operation.
 */
@Serializable
sealed class Operation<T : OperationData, R : OperationResult> {
    /**
     * Data used in the operation.
     */
    abstract val data: T

    /**
     * Result of the operation.
     */
    abstract val result: R
}

/**
 * The data used in an operation.
 *
 * @property propertyOrder The order in which the properties should be displayed in the GUI.
 */
@Serializable
abstract class OperationData {
    abstract val propertyOrder: List<KProperty<*>>
}

/**
 * The result of an operation.
 *
 * @property propertyOrder The order in which the properties should be displayed in the GUI.
 */
@Serializable
abstract class OperationResult {
    abstract val propertyOrder: List<KProperty<*>>
}

/**
 * The comprehensive list of operations.
 */
val OPERATION_DETAILS = mapOf(
    Radii::class to OperationDetails(
        name = "radii",
        title = "Radii of Curvature",
        description = "Computes the radii of curvature of the ellipsoid at a given latitude.",
        category = OperationCategory.GEODETIC_COMPUTATIONS,
        icon = "/icons/public.svg",
    ),
    DirectionDirectionIntersection::class to OperationDetails(
        name = "directionDirectionIntersection",
        title = "Direction-Direction Intersection",
        description = "Computes the intersection of two directions.",
        category = OperationCategory.COORDINATE_GEOMETRY,
        icon = "/icons/direction-direction.svg",
    ),
    DistanceDistanceIntersection::class to OperationDetails(
        name = "distanceDistanceIntersection",
        title = "Distance-Distance Intersection",
        description = "Computes the intersection of two circles.",
        category = OperationCategory.COORDINATE_GEOMETRY,
        icon = "/icons/distance-distance.svg",
    ),
)

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class PropertyTitle(val value: String)

/**
 * Data used to display the operation in the GUI.
 * @property name Unique, internal name of the operation.
 * @property title Display name of the operation.
 * @property description Description of the operation.
 * @property category Category of the operation.
 * @property icon Path to the icon of the operation.
 */
data class OperationDetails(
    val name: String,
    val title: String,
    val description: String,
    val category: OperationCategory,
    val icon: String,
)
