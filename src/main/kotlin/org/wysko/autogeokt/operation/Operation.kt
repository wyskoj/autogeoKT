package org.wysko.autogeokt.operation

import org.wysko.autogeokt.operation.cogo.DirectionDirectionIntersection
import org.wysko.autogeokt.operation.geodeticcomputations.Radii
import kotlin.reflect.KProperty

/**
 * Abstract class for operations. It is used to store data and result of the operation.
 */
interface Operation<T : OperationData, R : OperationResult> {
    /**
     * Data used in the operation.
     */
    val data: T

    /**
     * Result of the operation.
     */
    val result: R
}

/**
 * The data used in an operation.
 *
 * @property propertyOrder The order in which the properties should be displayed in the GUI.
 */
abstract class OperationData {
    abstract val propertyOrder: List<KProperty<*>>
}

/**
 * The result of an operation.
 *
 * @property propertyOrder The order in which the properties should be displayed in the GUI.
 */
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
        icon = "/icons/public.svg"
    ),
    DirectionDirectionIntersection::class to OperationDetails(
        name = "directionDirectionIntersection",
        title = "Direction-Direction Intersection",
        description = "Computes the intersection of two directions.",
        category = OperationCategory.COORDINATE_GEOMETRY,
        icon = "/icons/direction-direction.svg"
    )
)

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class PropertyTitle(val value: String)

/**
 * Data used to display the operation in the GUI.
 * @param name Unique, internal name of the operation.
 * @param title Display name of the operation.
 * @param description Description of the operation.
 */
data class OperationDetails(
    val name: String,
    val title: String,
    val description: String,
    val category: OperationCategory,
    val icon: String
)