package com.github.ageofwar.ktelegram

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.writeBytes

suspend fun TelegramApi.downloadFile(fileId: String, file: java.io.File) {
    file.writeBytes(downloadFile(fileId))
}

@OptIn(ExperimentalPathApi::class)
suspend fun TelegramApi.downloadFile(fileId: String, path: Path) {
    path.writeBytes(downloadFile(fileId))
}

suspend fun TelegramApi.downloadFile(fileId: String, path: String) {
    downloadFile(fileId, Paths.get(path))
}
