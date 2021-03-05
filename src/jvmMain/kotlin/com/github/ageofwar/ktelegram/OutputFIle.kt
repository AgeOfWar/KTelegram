package com.github.ageofwar.ktelegram

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.name
import kotlin.io.path.readBytes

fun OutputFile.Companion.fromFile(file: File) = fromContent(file.name, file.readBytes())

@OptIn(ExperimentalPathApi::class)
fun OutputFile.Companion.fromPath(path: Path) = fromContent(path.name, path.readBytes())

fun OutputFile.Companion.fromPath(path: String) = fromPath(Paths.get(path))
