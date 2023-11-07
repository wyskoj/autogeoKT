package org.wysko.autogeokt.gui.form

import org.wysko.autogeokt.gui.form.components.FormField
import org.wysko.autogeokt.operation.OperationDetails

/**
 * Interface for classes that can be represented as a form.
 */
interface Formable {
    val form: FormDetails<out Any>
}

/**
 * Represents the details of a form.
 *
 * @param D The type of data that is derived from the form.
 * @property formContents The list of form fields.
 * @property toData The function that converts the form data into the specified type.
 * @property canBeTemporary Indicates whether the form can be temporary or not. Default is false.
 * @property operationDetails The details of the operation associated with the form.
 */
data class FormDetails<D>(
    val formContents: List<FormField<out Any>>,
    val toData: (Map<String, Any?>) -> D,
    val canBeTemporary: Boolean = false,
    val operationDetails: OperationDetails,
)
