package com.drasticds.emulator.ui.layouts.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import com.drasticds.emulator.domain.model.layout.LayoutConfiguration
import com.drasticds.emulator.domain.repositories.LayoutsRepository
import com.drasticds.emulator.domain.repositories.SettingsRepository
import com.drasticds.emulator.ui.layouts.model.SelectedLayout
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LayoutsViewModel @Inject constructor(
    layoutsRepository: LayoutsRepository,
    private val settingsRepository: SettingsRepository,
) : BaseLayoutsViewModel(layoutsRepository) {

    private val _selectedLayoutId = MutableStateFlow(SelectedLayout(settingsRepository.getSelectedLayoutId(), SelectedLayout.SelectionReason.INITIAL_SELECTION))
    override val selectedLayoutId = _selectedLayoutId.asStateFlow()

    init {
        viewModelScope.launch {
            layoutsRepository.getLayouts()
                .onEach { layouts ->
                    val currentSelectedLayoutId = selectedLayoutId.value.layoutId
                    // If the currently selected layout cannot be found in the layouts list, it was deleted. Fallback to the default layout
                    if (!layouts.any { it.id == currentSelectedLayoutId }) {
                        applyFallbackLayout()
                    }
                }
                .collect(_layouts)
        }
    }

    override fun setSelectedLayoutId(id: UUID?) {
        if (id != null) {
            setSelectedLayout(id, SelectedLayout.SelectionReason.SELECTED_BY_USER)
        }
    }

    override fun applyFallbackLayout() {
        setSelectedLayout(LayoutConfiguration.DEFAULT_ID, SelectedLayout.SelectionReason.SELECTED_BY_FALLBACK)
    }

    private fun setSelectedLayout(id: UUID, reason: SelectedLayout.SelectionReason) {
        settingsRepository.setSelectedLayoutId(id)
        _selectedLayoutId.value = SelectedLayout(id, reason)
    }
}