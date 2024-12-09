package com.icure.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.icure.codegen.generator.KspJsonGenerator
import com.icure.codegen.utils.KRAKEN_DTO_BASE_PATH

class KspJsonProcessor(
	private val codeGenerator: CodeGenerator,
	private val logger: KSPLogger
): SymbolProcessor {
	private val successfullyProcessedKSDeclaration: MutableSet<KSDeclaration> = mutableSetOf()

	companion object {
		var commonLogger: KSPLogger? = null
	}

	override fun process(resolver: Resolver): List<KSAnnotated> {
		logger.warn("KspJsonProcessor created")
		commonLogger = logger

		val jsonGenerator = KspJsonGenerator(codeGenerator, logger)
		val failedRawSdkGenerations = resolver
			.getSymbolsWithAnnotation("org.springframework.web.bind.annotation.RestController")
			.filterIsInstance<KSClassDeclaration>()
			.filter { it.packageName.asString().contains(".v2.") }
			.mapNotNull {
				try {
					logger.warn(it.qualifiedName?.asString() ?: it.simpleName.asString())
					it.accept(jsonGenerator, Unit)
					null
				} catch (e: Exception) {
					logger.warn("Error while generating sdk for ${it.qualifiedName?.asString()}:\n$e")
					it
				}
			}.toList()

		val failedDtoGenerations = resolver
			.getAllFiles()
			.filter { it.packageName.asString().contains(KRAKEN_DTO_BASE_PATH) }
			.flatMap { it.declarations }
			.filterNot { it in successfullyProcessedKSDeclaration }
			.toList()
//			.filter { it.simpleName.asString() == "ArticleDto" }
			.also { successfullyProcessedKSDeclaration.addAll(it) }
			.mapNotNull {
				try {
					logger.warn(it.qualifiedName?.asString() ?: it.simpleName.asString())
					it.accept(jsonGenerator, Unit)
					null
				} catch (e: Exception) {
					logger.warn("Error while generating dto for ${it.packageName.asString()}:\n$e")
					successfullyProcessedKSDeclaration.remove(it)
					it
				}
			}

		val failedMpApiGeneration = resolver
			.getAllFiles()
			.filter {
				it.packageName.asString().startsWith("com.icure.cardinal.sdk.model")
					|| it.packageName.asString().startsWith("com.icure.cardinal.sdk.crypto.entities")
					|| it.packageName.asString().startsWith("com.icure.cardinal.sdk.utils")
					|| it.packageName.asString().startsWith("com.icure.cardinal.sdk.subscription")
					|| it.packageName.asString().startsWith("com.icure.cardinal.sdk.filters")
					|| (
						it.packageName.asString().startsWith("com.icure.cardinal.sdk.api")
						&& !it.packageName.asString().contains("raw")
					)

			}
			.flatMap { it.declarations }
			.filterNot { it in successfullyProcessedKSDeclaration }
			.toList()
//			.filter { it.simpleName.asString() == "ReplicateCommandDto" }
			.also { successfullyProcessedKSDeclaration.addAll(it) }
			.mapNotNull {
				try {
					logger.warn(it.qualifiedName?.asString() ?: it.simpleName.asString())
					it.accept(jsonGenerator, Unit)
					null
				} catch (e: Exception) {
					logger.warn("Error while generating api for ${it.packageName.asString()}:\n$e")
					successfullyProcessedKSDeclaration.remove(it)
					it
				}
			}

		return failedRawSdkGenerations + failedDtoGenerations + failedMpApiGeneration
	}
}
