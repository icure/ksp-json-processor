package com.icure.codegen

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * Entry point class for KSP and KotlinPoet
 */
class KspJsonProcessorProvider : SymbolProcessorProvider {
	override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
		KspJsonProcessor(
			environment.codeGenerator,
			environment.logger,
			environment.packagesOption(PACKAGES_OPTION),
			environment.packagesOption(CONTROLLER_PACKAGES_OPTION),
		)

	companion object {
		/**
		 * KSP option: comma-separated packages whose declarations are written as json, subpackages included, on top of the
		 * built-in Kraken and Cardinal packages. For example `ksp { arg("ksp-json.packages", "com.example.dto") }`.
		 */
		const val PACKAGES_OPTION = "ksp-json.packages"

		/**
		 * KSP option: comma-separated packages whose `@RestController`s are written as json, subpackages included, on top
		 * of the Kraken ones (`.v2.` packages). For example `ksp { arg("ksp-json.controllerPackages", "com.example.controllers") }`.
		 */
		const val CONTROLLER_PACKAGES_OPTION = "ksp-json.controllerPackages"

		private fun SymbolProcessorEnvironment.packagesOption(name: String): List<String> =
			options[name]?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
	}
}
