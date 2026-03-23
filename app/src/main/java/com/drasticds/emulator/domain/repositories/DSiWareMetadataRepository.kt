package com.drasticds.emulator.domain.repositories

interface DSiWareMetadataRepository {
    suspend fun getDSiWareTitleMetadata(categoryId: UInt, titleId: UInt): ByteArray
}