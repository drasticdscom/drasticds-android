package com.drasticds.emulator.ui.dsiwaremanager.ui

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.drasticds.emulator.common.Permission
import com.drasticds.emulator.common.contracts.CreateFileContract
import com.drasticds.emulator.common.contracts.FilePickerContract
import com.drasticds.emulator.domain.model.DSiWareTitle
import com.drasticds.emulator.domain.model.dsinand.DSiWareTitleFileType

@Composable
fun rememberDSiWareTitleImportFilePicker(
    onFilePicked: (title: DSiWareTitle, fileType: DSiWareTitleFileType, fileUri: Uri) -> Unit,
): DSiWareTitleFilePickerLauncher {
    return rememberDSiWareTitleFilePicker(onFilePicked = onFilePicked, permission = Permission.READ)
}

@Composable
fun rememberDSiWareTitleExportFilePicker(
    onFilePicked: (title: DSiWareTitle, fileType: DSiWareTitleFileType, fileUri: Uri) -> Unit,
): DSiWareTitleNewFilePickerLauncher {
    return rememberDSiWareTitleNewFilePicker(onFilePicked)
}

@Composable
private fun rememberDSiWareTitleFilePicker(
    onFilePicked: (title: DSiWareTitle, fileType: DSiWareTitleFileType, fileUri: Uri) -> Unit,
    permission: Permission,
): DSiWareTitleFilePickerLauncher {
    val onFilePickedCallback = rememberUpdatedState(onFilePicked)
    val requestData = remember {
        DSiWareTitleFilePickerRequestData()
    }
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = FilePickerContract(permission),
        onResult = {
            if (it != null) {
                val title = requestData.currentTitle ?: return@rememberLauncherForActivityResult
                val fileType = requestData.currentFileType ?: return@rememberLauncherForActivityResult

                onFilePickedCallback.value(title, fileType, it)
            }
            requestData.currentTitle = null
            requestData.currentFileType = null
        },
    )

    return remember {
        DSiWareTitleFilePickerLauncher(
            requestData,
            filePickerLauncher,
        )
    }
}

@Composable
private fun rememberDSiWareTitleNewFilePicker(
    onFilePicked: (title: DSiWareTitle, fileType: DSiWareTitleFileType, fileUri: Uri) -> Unit,
): DSiWareTitleNewFilePickerLauncher {
    val onFilePickedCallback = rememberUpdatedState(onFilePicked)
    val requestData = remember {
        DSiWareTitleFilePickerRequestData()
    }
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = CreateFileContract(),
        onResult = {
            if (it != null) {
                val title = requestData.currentTitle ?: return@rememberLauncherForActivityResult
                val fileType = requestData.currentFileType ?: return@rememberLauncherForActivityResult

                onFilePickedCallback.value(title, fileType, it)
            }
            requestData.currentTitle = null
            requestData.currentFileType = null
        },
    )

    return remember {
        DSiWareTitleNewFilePickerLauncher(
            requestData = requestData,
            filePickerLauncher = filePickerLauncher,
        )
    }
}

internal class DSiWareTitleFilePickerRequestData {
    var currentTitle: DSiWareTitle? = null
    var currentFileType: DSiWareTitleFileType? = null
}

class DSiWareTitleFilePickerLauncher internal constructor(
    private val requestData: DSiWareTitleFilePickerRequestData,
    private val filePickerLauncher: ManagedActivityResultLauncher<Pair<Uri?, Array<String>?>, Uri?>,
) {

    fun launch(title: DSiWareTitle, fileType: DSiWareTitleFileType) {
        requestData.currentTitle = title
        requestData.currentFileType = fileType
        filePickerLauncher.launch(null to null)
    }
}

class DSiWareTitleNewFilePickerLauncher internal constructor(
    private val requestData: DSiWareTitleFilePickerRequestData,
    private val filePickerLauncher: ManagedActivityResultLauncher<String, Uri?>,
) {

    fun launch(title: DSiWareTitle, fileType: DSiWareTitleFileType) {
        requestData.currentTitle = title
        requestData.currentFileType = fileType
        filePickerLauncher.launch(fileType.fileName)
    }
}