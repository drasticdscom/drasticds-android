package com.drasticds.emulator.extensions

import androidx.documentfile.provider.DocumentFile

val DocumentFile.nameWithoutExtension get() = name?.substringBeforeLast('.')

val DocumentFile.extension get() = name?.substringAfterLast('.')