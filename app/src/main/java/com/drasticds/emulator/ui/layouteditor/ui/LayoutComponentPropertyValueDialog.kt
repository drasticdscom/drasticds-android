package com.drasticds.emulator.ui.layouteditor.ui

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.drasticds.emulator.R
import com.drasticds.emulator.ui.common.component.dialog.TextInputDialog
import com.drasticds.emulator.ui.common.component.dialog.rememberTextInputDialogState
import com.drasticds.emulator.ui.layouteditor.model.LayoutComponentEditableProperty

@Composable
fun LayoutComponentPropertyValueDialog(
    editableProperty: LayoutComponentEditableProperty?,
    initialValue: Int,
    onValueChanged: (Int) -> Unit,
    onCancel: () -> Unit,
) {
    val dialogState = rememberTextInputDialogState()

    LaunchedEffect(editableProperty) {
        if (editableProperty != null) {
            dialogState.show(
                initialText = initialValue.toString(),
                onConfirm = { onValueChanged(it.toInt()) },
                onCancel = onCancel,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
            )
        }
    }

    TextInputDialog(
        title = when (editableProperty) {
            LayoutComponentEditableProperty.SIZE -> stringResource(R.string.label_size)
            LayoutComponentEditableProperty.WIDTH -> stringResource(R.string.label_width)
            LayoutComponentEditableProperty.HEIGHT -> stringResource(R.string.label_height)
            null -> ""
        },
        dialogState = dialogState,
        textValidator = { it.toIntOrNull() != null },
    )
}