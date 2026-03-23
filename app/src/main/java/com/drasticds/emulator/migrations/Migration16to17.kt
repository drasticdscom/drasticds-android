package com.drasticds.emulator.migrations

import com.drasticds.emulator.domain.repositories.RomsRepository

class Migration16to17(private val romsRepository: RomsRepository) : Migration {
    override val from = 16
    override val to = 17

    override fun migrate() {
        romsRepository.invalidateRoms()
    }
}