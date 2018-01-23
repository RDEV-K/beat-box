package org.ice1000.julia.lang

import com.intellij.CommonBundle
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.*
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.*

object JuliaLanguage : Language(JuliaBundle.message("julia.name"), "text/")

object JuliaFileType : LanguageFileType(JuliaLanguage) {
	override fun getDefaultExtension() = JULIA_EXTENSION
	override fun getName() = JuliaBundle.message("julia.name")
	override fun getIcon() = null
	override fun getDescription() = JuliaBundle.message("julia.name.description")
}

class JuliaFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, JuliaLanguage) {
	override fun getFileType() = JuliaFileType
	override fun processDeclarations(
			processor: PsiScopeProcessor,
			state: ResolveState,
			lastParent: PsiElement?,
			place: PsiElement) = processDeclTrivial(processor, state, lastParent, place)
}

class JuliaFileTypeFactory : FileTypeFactory() {
	override fun createFileTypes(consumer: FileTypeConsumer) = consumer.consume(JuliaFileType, JULIA_EXTENSION)
}

object JuliaBundle {
	@NonNls private const val BUNDLE = "org.ice1000.julia.lang.julia-bundle"
	private val bundle: ResourceBundle by lazy { ResourceBundle.getBundle(BUNDLE) }

	@JvmStatic
	fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
			CommonBundle.message(bundle, key, *params)
}
