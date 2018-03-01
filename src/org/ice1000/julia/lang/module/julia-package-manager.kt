package org.ice1000.julia.lang.module

import org.ice1000.julia.lang.executeCommand
import org.intellij.lang.annotations.Language
import java.io.File

data class InfoData(val name: String, val version: String, val latestVersion: String = "")

val packageInfos = emptyList<InfoData>().toMutableList()
var packageNameFinished = false
var packageVersionFinished = false
/**
 * It's needed for UE(User Experience)
 */
fun packageNamesList(): List<String> {
	fun String.toFile() = File(this)
	@Language("Julia")
	val code = "Pkg.dir()"
	val (stdout) = executeCommand(juliaPath, code, 5000L)
	return stdout
		.firstOrNull()
		?.trim('"')
		?.toFile()
		?.listFiles()
		?.filter { it.isDirectory && !it.name.startsWith(".") && it.name != "METADATA" }
		?.map { it.name }
		?: emptyList()
}

/**
 * very slow.
 */
fun versionsList(settings: JuliaSettings) =
	executeCommand(settings.exePath, "Pkg.installed()", 20_000L)
		.first
		.filter { "=>" in it }
		.map {
			//language=RegExp
			val (name, version) = it.split("=>")
			name.trim(' ', '"') to version.trim(' ', '"').removePrefix("v\"")
		}
