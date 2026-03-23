package com.drasticds.emulator.domain.model

data class FirmwareConfiguration(
        val nickname: String,
        val message: String,
        val language: Int,
        val favouriteColour: Int,
        val birthdayMonth: Int,
        val birthdayDay: Int,
        val randomizeMacAddress: Boolean,
        val internalMacAddress: String?
)