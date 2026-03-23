package com.drasticds.emulator.ui.settings.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import dagger.hilt.android.AndroidEntryPoint
import com.drasticds.emulator.R
import com.drasticds.emulator.common.DirectoryAccessValidator
import com.drasticds.emulator.common.UriPermissionManager
import com.drasticds.emulator.domain.model.SizeUnit
import com.drasticds.emulator.ui.settings.PreferenceFragmentHelper
import com.drasticds.emulator.ui.settings.PreferenceFragmentTitleProvider
import com.drasticds.emulator.ui.settings.SettingsViewModel
import com.drasticds.emulator.utils.SizeUtils
import javax.inject.Inject
import kotlin.math.pow

@AndroidEntryPoint
class RomsPreferencesFragment : BasePreferenceFragment(), PreferenceFragmentTitleProvider {

    private val viewModel: SettingsViewModel by activityViewModels()
    private val helper by lazy { PreferenceFragmentHelper(this, uriPermissionManager, directoryAccessValidator) }
    @Inject lateinit var uriPermissionManager: UriPermissionManager
    @Inject lateinit var directoryAccessValidator: DirectoryAccessValidator

    private lateinit var clearRomCachePreference: Preference

    override fun getTitle() = getString(R.string.category_roms)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_roms, rootKey)
        val cacheSizePreference = findPreference<SeekBarPreference>("rom_cache_max_size")!!
        clearRomCachePreference = findPreference("rom_cache_clear")!!

        helper.setupStoragePickerPreference(findPreference("rom_search_dirs")!!)

        updateMaxCacheSizePreferenceSummary(cacheSizePreference, cacheSizePreference.value)

        cacheSizePreference.setOnPreferenceChangeListener { preference, newValue ->
            updateMaxCacheSizePreferenceSummary(preference as SeekBarPreference, newValue as Int)
            true
        }
        clearRomCachePreference.setOnPreferenceClickListener {
            if (!viewModel.clearRomCache()) {
                Toast.makeText(requireContext(), R.string.error_clear_rom_cache, Toast.LENGTH_LONG).show()
            }
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getRomCacheSize().observe(viewLifecycleOwner) {
            val cacheSizeRepresentation = SizeUtils.getBestSizeStringRepresentation(requireContext(), it)
            clearRomCachePreference.summary = getString(R.string.cache_size, cacheSizeRepresentation)
        }
    }

    private fun updateMaxCacheSizePreferenceSummary(maxCacheSizePreference: SeekBarPreference, cacheSizeStep: Int) {
        val cacheSize = SizeUnit.MB(128) * 2.toDouble().pow(cacheSizeStep).toLong()
        maxCacheSizePreference.summary = SizeUtils.getBestSizeStringRepresentation(requireContext(), cacheSize)
    }
}