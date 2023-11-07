package org.wysko.autogeokt.gui.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import org.wysko.autogeokt.geospatial.DegreesMinutesSeconds
import org.wysko.autogeokt.geospatial.Ellipsoid
import org.wysko.autogeokt.gui.form.components.FormField
import org.wysko.autogeokt.gui.form.components.input.*
import org.wysko.autogeokt.operation.OperationData

@Composable
fun <D : OperationData> GenerateForm(formDetails: FormDetails<D>, onSubmit: (title: String?, data: D) -> Unit) {
    val data = remember { mutableStateOf(formDetails.formContents.makeData()) }
    val errors = remember { mutableStateOf(formDetails.formContents.makeErrors()) }
    val isMutated = remember { mutableStateOf(formDetails.formContents.makeIsMutated()) }
    val showErrors = remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            Text(formDetails.operationDetails.title, style = MaterialTheme.typography.headlineMedium)
            Text(formDetails.operationDetails.description, style = MaterialTheme.typography.bodyLarge)
        }
        TitleInput(
            value = data.value["title"] as String?,
            onValueChange = { data.value += ("title" to it) },
            errors = errors.value["title"],
            setErrors = { errors.value += ("title" to it) },
            isShowError = showErrors.value,
            canBeTemporary = formDetails.canBeTemporary,
        )
        // Instance form fields
        formDetails.formContents.forEach { field ->
            when (field) {
                is FormField.EllipsoidInput -> EllipsoidInput(
                    value = data.value[field.data.name] as Ellipsoid?,
                    onValueChange = { data.value += (field.data.name to it) },
                    label = field.data.title,
                    descriptionText = field.data.description,
                    isShowError = showErrors.value,
                    errors = errors.value[field.data.name],
                    setErrors = { errors.value += (field.data.name to it) },
                    setIsMutated = { isMutated.value += (field.data.name to it) },
                )

                is FormField.DegreesMinutesSecondsInput -> DegreesMinutesSecondsInput(
                    value = data.value[field.data.name] as DegreesMinutesSeconds?,
                    onValueChange = { data.value += (field.data.name to it) },
                    label = field.data.title,
                    descriptionText = field.data.description,
                    isShowError = showErrors.value && errors.value[field.data.name]?.any() == true && (!field.data.isOptional || isMutated.value[field.data.name] == true),
                    isOptional = field.data.isOptional,
                    errors = errors.value[field.data.name],
                    setErrors = { errors.value += (field.data.name to it) },
                    setIsMutated = { isMutated.value += (field.data.name to it) },
                )

                is FormField.RayInput -> RayInput(
                    value = data.value[field.data.name] as org.wysko.autogeokt.geospatial.Ray?,
                    onValueChange = { data.value += (field.data.name to it) },
                    label = field.data.title,
                    descriptionText = field.data.description,
                    isShowError = showErrors.value && errors.value[field.data.name]?.any() == true && (!field.data.isOptional || isMutated.value[field.data.name] == true),
                    isOptional = field.data.isOptional,
                    errors = errors.value[field.data.name],
                    setErrors = { errors.value += (field.data.name to it) },
                    setIsMutated = { isMutated.value += (field.data.name to it) },
                )

                is FormField.NumberInput -> RealNumberInput(
                    value = data.value[field.data.name] as Double?,
                    onValueChange = { data.value += (field.data.name to it) },
                    label = field.data.title,
                    descriptionText = field.data.description,
                    isShowError = showErrors.value && errors.value[field.data.name]?.any() == true && (!field.data.isOptional || isMutated.value[field.data.name] == true),
                    isOptional = field.data.isOptional,
                    errors = errors.value[field.data.name],
                    setErrors = { errors.value += (field.data.name to it) },
                    setIsMutated = { isMutated.value += (field.data.name to it) },
                )

                is FormField.CircleInput -> CircleInput(
                    value = data.value[field.data.name] as org.wysko.autogeokt.geospatial.Circle?,
                    onValueChange = { data.value += (field.data.name to it) },
                    label = field.data.title,
                    descriptionText = field.data.description,
                    isShowError = showErrors.value && errors.value[field.data.name]?.any() == true && (!field.data.isOptional || isMutated.value[field.data.name] == true),
                    isOptional = field.data.isOptional,
                    errors = errors.value[field.data.name],
                    setErrors = { errors.value += (field.data.name to it) },
                    setIsMutated = { isMutated.value += (field.data.name to it) },
                )
            }
        }

        Button(
            onClick = {
                showErrors.value = true
                // Remove errors for fields that are optional and not mutated
                val relevantErrors = errors.value.mapValues { (name, errors) ->
                    if (formDetails.formContents.find { it.data.name == name }?.data?.isOptional == true && isMutated.value[name] == false) {
                        emptySet()
                    } else {
                        errors
                    }
                }
                if (relevantErrors.values.flatten().isEmpty()) {
                    onSubmit(data.value["title"]?.toString(), formDetails.toData(data.value))
                }
            },
        ) {
            Text("Submit")
        }
    }
}

fun List<FormField<out Any>>.makeData(addTitle: Boolean = true): Map<String, Any?> =
    associate { it.data.name to null }.run {
        toMutableMap().also {
            if (addTitle) {
                it["title"] = null
            }
        }
    }

fun List<FormField<out Any>>.makeErrors(): Map<String, Set<InputError>> = associate { it.data.name to emptySet() }
fun List<FormField<out Any>>.makeIsMutated(): Map<String, Boolean> = associate { it.data.name to false }
