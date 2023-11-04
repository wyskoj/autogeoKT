package org.wysko.autogeokt.gui.form

import org.wysko.autogeokt.gui.form.components.FormField
import org.wysko.autogeokt.operation.OperationDetails

interface Formable {
    val form: FormDetails<out Any>
}

data class FormDetails<D>(
    val formContents: List<FormField<out Any>>,
    val toData: (Map<String, Any?>) -> D,
    val canBeTemporary: Boolean = false,
    val operationDetails: OperationDetails
)