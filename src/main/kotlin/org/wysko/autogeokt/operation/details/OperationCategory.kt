package org.wysko.autogeokt.operation.details

/**
 * Represents the category of an operation.
 *
 * @property title The title of the category.
 * @property icon The URL of the category's icon.
 */
enum class OperationCategory(
    val title: String,
    val icon: String,
) {
    GEODETIC_COMPUTATIONS("Geodetic Computations", "/icons/public.svg"),
    COORDINATE_GEOMETRY("Coordinate Geometry", "/icons/cogo.svg"),
}
