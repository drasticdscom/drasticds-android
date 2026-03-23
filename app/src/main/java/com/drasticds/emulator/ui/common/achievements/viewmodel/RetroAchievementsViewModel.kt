package com.drasticds.emulator.ui.common.achievements.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.drasticds.emulator.domain.model.rom.Rom
import com.drasticds.emulator.domain.model.retroachievements.RAUserAchievement
import com.drasticds.emulator.domain.repositories.RetroAchievementsRepository
import com.drasticds.emulator.domain.repositories.SettingsRepository
import com.drasticds.emulator.ui.romdetails.model.AchievementBucketUiModel
import com.drasticds.emulator.ui.romdetails.model.AchievementSetUiModel
import com.drasticds.emulator.ui.romdetails.model.RomAchievementsSummary
import com.drasticds.emulator.ui.romdetails.model.RomRetroAchievementsUiState
import com.drasticds.emulator.rcheevosapi.model.RAAchievement

abstract class RetroAchievementsViewModel (
    private val retroAchievementsRepository: RetroAchievementsRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<RomRetroAchievementsUiState>(RomRetroAchievementsUiState.Loading)
    val uiState by lazy {
        loadAchievements()
        _uiState.asStateFlow()
    }

    private val _viewAchievementEvent = MutableSharedFlow<String>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val viewAchievementEvent = _viewAchievementEvent.asSharedFlow()

    private var achievementLoadJob: Job? = null

    protected abstract fun getRom(): Rom

    protected abstract suspend fun buildAchievementBuckets(achievements: List<RAUserAchievement>): List<AchievementBucketUiModel>

    private fun loadAchievements() {
        achievementLoadJob?.cancel()
        achievementLoadJob = viewModelScope.launch {
            if (retroAchievementsRepository.isUserAuthenticated()) {
                val forHardcoreMode = settingsRepository.isRetroAchievementsHardcoreEnabled()
                retroAchievementsRepository.getUserGameData(getRom().retroAchievementsHash, forHardcoreMode).fold(
                    onSuccess = { userGameData ->
                        val sets = userGameData?.sets?.map { set ->
                            AchievementSetUiModel(
                                setId = set.id.id,
                                setTitle = set.title,
                                setType = set.type,
                                setIcon = set.iconUrl,
                                setSummary = buildAchievementsSummary(forHardcoreMode, set.achievements),
                                buckets = buildAchievementBuckets(set.achievements),
                            )
                        }
                        _uiState.value = RomRetroAchievementsUiState.Ready(sets.orEmpty())
                    },
                    onFailure = {
                        ensureActive()
                        _uiState.value = RomRetroAchievementsUiState.AchievementLoadError
                    },
                )
            } else {
                _uiState.value = RomRetroAchievementsUiState.LoggedOut
            }
        }
    }

    fun login(username: String, password: String) {
        _uiState.value = RomRetroAchievementsUiState.Loading
        viewModelScope.launch {
            val result = retroAchievementsRepository.login(username, password)
            if (result.isSuccess) {
                loadAchievements()
            } else {
                _uiState.value = RomRetroAchievementsUiState.LoginError
            }
        }
    }

    fun retryLoadAchievements() {
        _uiState.value = RomRetroAchievementsUiState.Loading
        loadAchievements()
    }

    fun viewAchievement(achievement: RAAchievement) {
        val achievementUrl = "https://retroachievements.org/achievement/${achievement.id}"
        _viewAchievementEvent.tryEmit(achievementUrl)
    }

    private fun buildAchievementsSummary(forHardcoreMode: Boolean, userAchievements: List<RAUserAchievement>): RomAchievementsSummary {
        return RomAchievementsSummary(
            forHardcoreMode = forHardcoreMode,
            totalAchievements = userAchievements.size,
            completedAchievements = userAchievements.count { it.isUnlocked },
            totalPoints = userAchievements.sumOf { it.userPointsWorth() },
        )
    }
}