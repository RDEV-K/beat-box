/*
 *     Julia language support plugin for Intellij-based IDEs.
 *     Copyright (C) 2024 julia-intellij contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.ice1000.julia.lang.module

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.roots.AdditionalLibraryRootsProvider
import com.intellij.openapi.roots.SyntheticLibrary
import com.intellij.openapi.roots.libraries.*
import com.intellij.openapi.roots.libraries.ui.LibraryEditorComponent
import com.intellij.openapi.roots.libraries.ui.LibraryPropertiesEditor
import com.intellij.openapi.util.Condition
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.xmlb.XmlSerializerUtil
import icons.JuliaIcons
import org.ice1000.julia.lang.JULIA_WEBSITE
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.printJulia
import org.jdom.Element
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.swing.Icon
import javax.swing.JComponent

///** @deprecated */
//@Deprecated("No longer used")
class JuliaSdkType : SdkType(JuliaBundle.message("julia.name")) {
	override fun getPresentableName() = JuliaBundle.message("julia.modules.sdk.name")
	override fun getIcon() = JuliaIcons.JULIA_BIG_ICON
	override fun getIconForAddAction() = JuliaIcons.ADD_SDK_ICON
	override fun isValidSdkHome(sdkHome: String) = validateJuliaSDK(sdkHome)
	override fun suggestSdkName(currentSdkName: String?, sdkHome: String): String =
		JuliaBundle.message("julia.modules.sdk.name")
	override fun suggestHomePath() = juliaGlobalSettings.knownJuliaExes.firstOrNull()?.let { Paths.get(it) }?.parent?.parent?.toString()
	override fun getDownloadSdkUrl() = JULIA_WEBSITE
	override fun createAdditionalDataConfigurable(md: SdkModel, m: SdkModificator): AdditionalDataConfigurable? = null
	override fun getVersionString(sdkHome: String): String? = versionOf(sdkHome)
	override fun saveAdditionalData(additionalData: SdkAdditionalData, element: Element) = Unit // leave blank
	override fun setupSdkPaths(sdk: Sdk, sdkModel: SdkModel): Boolean {
		val modificator = sdk.sdkModificator
		modificator.versionString = getVersionString(sdk) ?: JuliaBundle.message("julia.modules.sdk.unknown-version")
		modificator.commitChanges()
		return true
	}

	companion object InstanceHolder {
		val instance get() = findInstance(JuliaSdkType::class.java)
	}
}

fun validateJuliaSDK(sdkHome: String) = Files.exists(Paths.get(sdkHome, "bin", "julia")) ||
	Files.exists(Paths.get(sdkHome, "bin", "julia.exe"))

class JuliaLibraryProperties : LibraryProperties<JuliaLibraryProperties>() {
	var map: Map<String, List<String>> = TreeMap()
	override fun getState(): JuliaLibraryProperties = this
	override fun loadState(state: JuliaLibraryProperties) {
		XmlSerializerUtil.copyBean(state, this)
	}

	override fun equals(other: Any?): Boolean = other is JuliaLibraryProperties && map == other.map
	override fun hashCode(): Int = map.hashCode()
}

class JuliaLibraryType : LibraryType<JuliaLibraryProperties>(LIBRARY_KIND) {
	override fun createPropertiesEditor(editorComponent: LibraryEditorComponent<JuliaLibraryProperties>): LibraryPropertiesEditor? = null
	override fun createNewLibrary(parentComponent: JComponent, contextDirectory: VirtualFile?, project: Project): NewLibraryConfiguration? = null
	override fun getCreateActionName(): String? = null

	companion object {
		@JvmField
		val LIBRARY_KIND: PersistentLibraryKind<JuliaLibraryProperties> = object : PersistentLibraryKind<JuliaLibraryProperties>("JuliaLibraryType") {
			override fun createDefaultProperties(): JuliaLibraryProperties {
				return JuliaLibraryProperties()
			}
		}
	}
}

open class JuliaSdkLibraryPresentationProvider protected constructor() : LibraryPresentationProvider<DummyLibraryProperties>(KIND) {
	override fun getIcon(properties: DummyLibraryProperties?): Icon? = JuliaIcons.JULIA_BIG_ICON

	override fun detect(classesRoots: List<VirtualFile>): DummyLibraryProperties? =
		if (findJuliaRoot(classesRoots) == null) null else DummyLibraryProperties.INSTANCE

	companion object {
		private val KIND = LibraryKind.create("Julia")

		fun findJuliaRoot(classesRoots: List<VirtualFile>): VirtualFile? =
			classesRoots.find { root ->
				root.isInLocalFileSystem && root.isDirectory && root.findChild("share") != null
			}
	}
}

class JuliaStdLibraryProvider : AdditionalLibraryRootsProvider() {
	override fun getAdditionalProjectLibraries(project: Project): Collection<StdLibrary> {
		if (!project.withJulia) return emptyList()

		val settings = project.juliaSettings.settings
		val base = settings.basePath.takeIf { it.isNotEmpty() } ?: return emptyList()
		val version = settings.version
		val list = linkedSetOf<StdLibrary>()

		// it'll cause NPE if `base` is empty, so judge it is not empty before
		val sharePath = Paths.get(base).parent.toFile()
		val dir = VfsUtil.findFileByIoFile(sharePath, true)
		if (dir != null) list.add(StdLibrary("Julia $version", dir))

		try {
			val pkgFile = if (compareVersion(settings.version, "0.7.0") < 0) {
				val pkgdir = printJulia(juliaPath, timeLimit = 5000L, expr = "Pkg.dir()")
					.first
					.firstOrNull()?.trim('"')
				Paths.get(pkgdir).toFile()
			} else {
				val userHome = System.getProperty("user.home")
				Paths.get(userHome, ".julia", "packages").toFile()
			}
			val pkgVirtualFile = VfsUtil.findFileByIoFile(pkgFile, true)
			if (pkgVirtualFile != null) {
				list.add(StdLibrary("Julia $version Packages", pkgVirtualFile, JuliaLibraryType.PKG))
			}
		} finally {
			return list
		}
	}

	enum class JuliaLibraryType {
		PKG, SDK
	}


	companion object {
		val EXCLUDE_NAMES = arrayOf("test", "deps", "docs")
	}

	class StdLibrary(private val name: String,
									 private val root: VirtualFile,
									 private val type: JuliaLibraryType = JuliaLibraryType.SDK) : SyntheticLibrary(), ItemPresentation {
		private val roots = root.children.asList()
		override fun hashCode() = root.hashCode()
		override fun equals(other: Any?): Boolean = other is StdLibrary && other.root == root
		override fun getSourceRoots() = roots
		override fun getLocationString() = ""
		override fun getIcon(p0: Boolean): Icon = if (type == JuliaLibraryType.SDK) JuliaIcons.JULIA_BIG_ICON else JuliaIcons.JULIA_ICON
		override fun getPresentableText() = name
		override fun getExcludeFileCondition(): Condition<VirtualFile>? = Condition { file ->
			when {
				file.isDirectory -> file.name in EXCLUDE_NAMES || file.parent.name == "base"
				else -> !file.name.endsWith(".jl")
			}
		}

	}
}