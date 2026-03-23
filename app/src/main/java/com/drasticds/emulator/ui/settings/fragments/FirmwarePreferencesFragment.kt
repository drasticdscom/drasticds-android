package com.drasticds.emulator.ui.settings.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import com.drasticds.emulator.R
import com.drasticds.emulator.common.DirectoryAccessValidator
import com.drasticds.emulator.common.UriPermissionManager
import com.drasticds.emulator.ui.settings.PreferenceFragmentHelper
import com.drasticds.emulator.ui.settings.PreferenceFragmentTitleProvider
import javax.inject.Inject

@AndroidEntryPoint
class FirmwarePreferencesFragment : BasePreferenceFragment(), PreferenceFragmentTitleProvider {
    private val helper by lazy { PreferenceFragmentHelper(this, uriPermissionManager, directoryAccessValidator) }
    @Inject lateinit var uriPermissionManager: UriPermissionManager
    @Inject lateinit var directoryAccessValidator: DirectoryAccessValidator

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_internal_firmware_settings, rootKey)
        helper.bindPreferenceSummaryToValue(findPreference("firmware_settings_birthday"))
        helper.bindPreferenceSummaryToValue(findPreference("internal_mac_address"))
    }

    override fun getTitle() = getString(R.string.internal_firmware_settings)
}