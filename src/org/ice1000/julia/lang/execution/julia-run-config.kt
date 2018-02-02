package org.ice1000.julia.lang.execution

import com.intellij.execution.Executor
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.PathMacroManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.JDOMExternalizer
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.editing.JULIA_BIG_ICON
import org.ice1000.julia.lang.module.*
import org.jdom.Element
import com.google.common.io.Files as GoogleFiles

class JuliaRunConfiguration(project: Project, factory: ConfigurationFactory) :
	ModuleBasedConfiguration<RunConfigurationModule>(
		JuliaBundle.message("julia.name"),
		RunConfigurationModule(project),
		factory) {
	var workingDir = ""
	var targetFile = ""
	var jitCompiler = "yes"
	var additionalOptions = ""
	var programArgs = ""
	var juliaExecutable = ""
	var inlineOption = false
	var checkBoundsOption = false
	var colorOption = false
	var unsafeFloatOption = false
	var handleSignalOption = false
	var startupFileOption = false
	var historyOption = false
	var optimizationLevel = 3
		set(value) {
			field = if (value > 3) 3 else if (value < 0) 0 else value
		}

	override fun getConfigurationEditor() = JuliaRunConfigurationEditor(this)
	override fun getState(executor: Executor, env: ExecutionEnvironment) = JuliaCommandLineState(this, env)
	override fun getValidModules() = allModules
	override fun writeExternal(element: Element) {
		PathMacroManager.getInstance(project).expandPaths(element)
		super.writeExternal(element)
		JDOMExternalizer.write(element, "workingDir", workingDir)
		JDOMExternalizer.write(element, "targetFile", targetFile)
		JDOMExternalizer.write(element, "jitCompiler", jitCompiler)
		JDOMExternalizer.write(element, "juliaExecutive", juliaExecutable)
		JDOMExternalizer.write(element, "additionalOptions", additionalOptions)
		JDOMExternalizer.write(element, "programArgs", programArgs)
		JDOMExternalizer.write(element, "inlineOption", inlineOption)
		JDOMExternalizer.write(element, "checkBoundsOption", checkBoundsOption)
		JDOMExternalizer.write(element, "colorOption", colorOption)
		JDOMExternalizer.write(element, "mathModeOption", unsafeFloatOption)
		JDOMExternalizer.write(element, "handleSignalOption", handleSignalOption)
		JDOMExternalizer.write(element, "startupFileOption", startupFileOption)
		JDOMExternalizer.write(element, "historyOption", historyOption)
		JDOMExternalizer.write(element, "optimizationLevel", optimizationLevel)
	}

	override fun readExternal(element: Element) {
		super.readExternal(element)
		JDOMExternalizer.readString(element, "workingDir")?.let { workingDir = it }
		JDOMExternalizer.readString(element, "targetFile")?.let { targetFile = it }
		JDOMExternalizer.readString(element, "jitCompiler")?.let { jitCompiler = it }
		JDOMExternalizer.readString(element, "juliaExecutive")?.let { juliaExecutable = it }
		JDOMExternalizer.readString(element, "additionalOptions").let { additionalOptions = it ?: "" }
		JDOMExternalizer.readString(element, "programArgs").let { programArgs = it ?: "" }
		JDOMExternalizer.readBoolean(element, "inlineOption").let { inlineOption = it }
		JDOMExternalizer.readBoolean(element, "checkBoundsOption").let { checkBoundsOption = it }
		JDOMExternalizer.readBoolean(element, "colorOption").let { colorOption = it }
		JDOMExternalizer.readBoolean(element, "mathModeOption").let { unsafeFloatOption = it }
		JDOMExternalizer.readBoolean(element, "handleSignalOption").let { handleSignalOption = it }
		JDOMExternalizer.readBoolean(element, "startupFileOption").let { startupFileOption = it }
		JDOMExternalizer.readBoolean(element, "historyOption").let { historyOption = it }
		JDOMExternalizer.readInteger(element, "optimizationLevel", 3).let { optimizationLevel = it }
		PathMacroManager.getInstance(project).collapsePathsRecursively(element)
	}
}

class JuliaRunConfigurationFactory(type: JuliaRunConfigurationType) : ConfigurationFactory(type) {
	override fun createTemplateConfiguration(project: Project) = JuliaRunConfiguration(project, this)
}

object JuliaRunConfigurationType : ConfigurationType {
	override fun getIcon() = JULIA_BIG_ICON
	override fun getConfigurationTypeDescription() = JuliaBundle.message("julia.run-config.description")
	override fun getId() = JULIA_RUN_CONFIG_ID
	override fun getDisplayName() = JuliaBundle.message("julia.name")
	override fun getConfigurationFactories() = arrayOf(JuliaRunConfigurationFactory(this))
}

class JuliaRunConfigurationProducer : RunConfigurationProducer<JuliaRunConfiguration>(JuliaRunConfigurationType) {
	override fun isConfigurationFromContext(
		configuration: JuliaRunConfiguration, context: ConfigurationContext) =
		configuration.targetFile == context.dataContext.getData(CommonDataKeys.VIRTUAL_FILE)?.path

	override fun setupConfigurationFromContext(
		configuration: JuliaRunConfiguration, context: ConfigurationContext, ref: Ref<PsiElement>?): Boolean {
		val file = context.dataContext.getData(CommonDataKeys.VIRTUAL_FILE)
		if (file?.fileType != JuliaFileType) return false
		configuration.targetFile = file.path
		configuration.workingDir = context.project.basePath.orEmpty()
		configuration.name = GoogleFiles.getNameWithoutExtension(configuration.targetFile)
		val existPath = context.project
			.juliaSettings
			.settings
			.exePath
		if (validateJuliaExe(existPath)) configuration.juliaExecutable = existPath
		return true
	}
}

