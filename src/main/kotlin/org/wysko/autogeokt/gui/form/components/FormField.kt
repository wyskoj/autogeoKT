package org.wysko.autogeokt.gui.form.components

import org.wysko.autogeokt.geospatial.DegreesMinutesSeconds
import org.wysko.autogeokt.geospatial.Ellipsoid
import org.wysko.autogeokt.geospatial.Ray

/**
 * Data class for input data.
 * @property name An internal, unique name for the input.
 * @property title The title (display name) of the input.
 * @property description A description of the input.
 * @property isOptional Whether the input is optional.
 */
data class InputData(
    val name: String,
    val title: String,
    val description: String,
    val isOptional: Boolean = false
)

/**
 * A type of form input field.
 */
sealed class FormField<D> {
    /** The data for the input field. */
    abstract val data: InputData

    /** Input for selecting an [Ellipsoid]. */
    data class EllipsoidInput(
        override val data: InputData
    ) : FormField<Ellipsoid>()

    /** Input for entering a [DegreesMinutesSeconds]. */
    data class DegreesMinutesSecondsInput(
        override val data: InputData
    ) : FormField<DegreesMinutesSeconds>()

    /** Input for entering a [Number]. */
    data class NumberInput(
        override val data: InputData
    ) : FormField<Number>()

    /** Input for entering a [Ray], which is just a point and a direction. */
    data class RayInput(
        override val data: InputData
    ) : FormField<Ray>()
}