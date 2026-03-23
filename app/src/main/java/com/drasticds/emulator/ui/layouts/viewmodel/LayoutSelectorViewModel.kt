package com.drasticds.emulator.ui.layouts.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import com.drasticds.emulator.domain.repositories.LayoutsRepository
import com.drasticds.emulator.ui.layouts.LayoutSelectorActivity
import com.drasticds.emulator.ui.layouts.model.SelectedLayout
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LayoutSelectorViewModel @Inject constructor(
    layoutsRepository: LayoutsRepository,
    savedStateHandle: SavedStateHandle,
) : BaseLayoutsViewModel(layoutsRepository) {

    private val initiallySelectedLayout = savedStateHandle.get<String?>(LayoutSelectorActivity.KEY_SELECTED_LAYOUT_ID)?.let {
        UUID.fromString(it)
    }

    private val _currentSelectedLayoutId = MutableStateFlow(SelectedLayout(initiallySelectedLayout, SelectedLayout.SelectionReason.INITIAL_SELECTION))
    override val selectedLayoutId = _currentSelectedLayoutId.asStateFlow()

    init {
        viewModelScope.launch {
            layoutsRepository.getLayouts()
                .onEach { layouts ->
                    val currentSelectedLayoutId = _currentSelectedLayoutId.value.layoutId
                    // If the currently selected layout cannot be found in the layouts list, it was deleted. Fallback to the global layout (null ID)
                    if (!layouts.any { it.id == currentSelectedLayoutId }) {
                        applyFallbackLayout()
                    }
                }
                .collect { layouts ->
                    val layoutList = layouts.toMutableList()
                    layoutList.add(0, layoutsRepository.getGlobalLayoutPlaceholder())
                    _layouts.value = layoutList
                }
        }
    }

    override fun setSelectedLayoutId(id: UUID?) {
        _currentSelectedLayoutId.value = SelectedLayout(id, SelectedLayout.SelectionReason.SELECTED_BY_USER)
    }

    override fun applyFallbackLayout() {
        _currentSelectedLayoutId.value = SelectedLayout(null, SelectedLayout.SelectionReason.SELECTED_BY_FALLBACK)
    }
}