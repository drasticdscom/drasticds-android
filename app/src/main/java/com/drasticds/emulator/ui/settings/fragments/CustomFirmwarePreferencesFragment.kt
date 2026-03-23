package com.drasticds.emulator.ui.settings.fragments

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import com.drasticds.emulator.R
import com.drasticds.emulator.common.DirectoryAccessValidator
import com.drasticds.emulator.common.UriPermissionManager
import com.drasticds.emulator.domain.model.ConfigurationDirResult
import com.drasticds.emulator.domain.model.ConsoleType
import com.drasticds.emulator.ui.settings.PreferenceFragmentHelper
import com.drasticds.emulator.ui.settings.PreferenceFragmentTitleProvider
import com.drasticds.emulator.ui.settings.SettingsViewModel
import com.drasticds.emulator.ui.settings.preferences.BiosDirectoryPickerPreference
import com.drasticds.emulator.utils.enumValueOfIgnoreCase
import javax.inject.Inject

@AndroidEntryPoint
class CustomFirmwarePreferencesFragment : BasePreferenceFragment(), PreferenceFragmentTitleProvider {

    private val viewModel: SettingsViewModel by activityViewModels()
    private val helper by lazy { PreferenceFragmentHelper(this, uriPermissionManager, directoryAccessValidator) }
    @Inject lateinit var uriPermissionManager: UriPermissionManager
    @Inject lateinit var directoryAccessValidator: DirectoryAccessValidator

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_custom_firmware, rootKey)
        val consoleTypePreference = findPreference<ListPreference>("console_type")!!
        val dsBiosDirPreference = findPreference<BiosDirectoryPickerPreference>("bios_dir")!!
        val dsiBiosDirPreference = findPreference<BiosDirectoryPickerPreference>("dsi_bios_dir")!!

        helper.setupStoragePickerPreference(dsBiosDirPreference)
        helper.setupStoragePickerPreference(dsiBiosDirPreference)

        val biosValidator = object : BiosDirectoryPickerPreference.BiosDirectoryValidator {
            override fun getBiosDirectoryValidationResult(consoleType: ConsoleType, directory: Uri?): ConfigurationDirResult {
                return viewModel.getConsoleConfigurationDirectoryStatus(consoleType, directory)
            }
        }

        dsBiosDirPreference.setBiosDirectoryValidator(biosValidator)
        dsiBiosDirPreference.setBiosDirectoryValidator(biosValidator)

        consoleTypePreference.setOnPreferenceChangeListener { _, newValue ->
            val consoleTypePreferenceValue = newValue as String
            val newConsoleType = enumValueOfIgnoreCase<ConsoleType>(consoleTypePreferenceValue)

            if (viewModel.getConsoleConfigurationDirectoryStatus(newConsoleType).status != ConfigurationDirResult.Status.VALID) {
                val textRes = when (newConsoleType) {
                    ConsoleType.DS -> R.string.ds_incorrect_bios_dir_info
                    ConsoleType.DSi -> R.string.dsi_incorrect_bios_dir_info
                }

                AlertDialog.Builder(requireContext())
                        .setMessage(textRes)
                        .setPositiveButton(R.string.ok, null)
                        .show()
            }

            true
        }
    }

    override fun getTitle() = getString(R.string.custom_bios_firmware)
}