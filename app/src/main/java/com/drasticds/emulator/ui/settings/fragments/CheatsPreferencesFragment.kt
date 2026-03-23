package com.drasticds.emulator.ui.settings.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.drasticds.emulator.R
import com.drasticds.emulator.common.Permission
import com.drasticds.emulator.common.contracts.FilePickerContract
import com.drasticds.emulator.extensions.isNotificationPermissionGranted
import com.drasticds.emulator.ui.settings.CheatsImportProgressDialog
import com.drasticds.emulator.ui.settings.PreferenceFragmentTitleProvider
import com.drasticds.emulator.ui.settings.SettingsViewModel

class CheatsPreferencesFragment : BasePreferenceFragment(), PreferenceFragmentTitleProvider {

    private val viewModel: SettingsViewModel by activityViewModels()

    private val cheatFilePickerLauncher = registerForActivityResult(FilePickerContract(Permission.READ)) {
        if (it != null) {
            viewModel.importCheatsDatabase(it)
            CheatsImportProgressDialog().show(childFragmentManager, null)
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        handleCheatsImport()
    }

    override fun getTitle() = getString(R.string.cheats)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_cheats, rootKey)
        val importCheatsPreference = findPreference<Preference>("cheats_import")!!

        importCheatsPreference.setOnPreferenceClickListener {
            if (!requestNotificationPermission()) {
                handleCheatsImport()
            }
            true
        }
    }

    private fun handleCheatsImport() {
        if (viewModel.areCheatsBeingImported()) {
            CheatsImportProgressDialog().show(childFragmentManager, null)
        } else {
            cheatFilePickerLauncher.launch(Pair(null, arrayOf("text/xml")))
        }
    }

    /**
     * Requests the notification permission if required.
     *
     * @return Whether the permission is being requested or not
     */
    private fun requestNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return false
        }

        return if (requireContext().isNotificationPermissionGranted()) {
            false
        } else {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            true
        }
    }
}