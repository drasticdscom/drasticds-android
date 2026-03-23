package com.drasticds.emulator.ui.settings.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import com.drasticds.emulator.R
import com.drasticds.emulator.ui.settings.PreferenceFragmentTitleProvider

@AndroidEntryPoint
class MainPreferencesFragment : BasePreferenceFragment(), PreferenceFragmentTitleProvider {

    override fun getTitle() = getString(R.string.settings)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_main, rootKey)
    }
}