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
			environment.options[PACKAGES_OPTION]
				?.split(",")
				?.map { it.trim() }
				?.filter { it.isNotEmpty() }
				.orEmpty()
		)

	companion object {
		/**
		 * KSP option: comma-separated packages whose declarations are written as json, subpackages included, on top of the
		 * built-in Kraken and Cardinal packages. For example `ksp { arg("ksp-json.packages", "com.example.dto") }`.
		 */
		const val PACKAGES_OPTION = "ksp-json.packages"
	}
}
