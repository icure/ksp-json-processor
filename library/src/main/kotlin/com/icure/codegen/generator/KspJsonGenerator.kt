package com.icure.codegen.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.icure.codegen.generator.DtoGeneratorsOptions.dtoIgnore
import com.icure.codegen.ir.declaration.toIRDeclaration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream

/**
 * This class generates the BaseSDK class for an entity based on the Controller(s) for that entity.
 * Note: this extends the [KSVisitorVoid] because the visitor methods don't need to return anything, as everything
 * will be written to a file.
 * This will generate two different base SDKs (for cloud and common controllers), that will be then joined together
 * by a task.
 * All the Raw SDK classes are annotated as internal by default.
 */
@OptIn(ExperimentalSerializationApi::class)
class KspJsonGenerator(
    codeGenerator: CodeGenerator,
    logger: KSPLogger
) : Generator(codeGenerator, logger) {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        if (!dtoIgnore.contains(classDeclaration.qualifiedName?.asString())) {
            val value = classDeclaration.toIRDeclaration()

            logger.info("Generated JSON for ${classDeclaration.packageName.asString()}/${classDeclaration.simpleName.asString()}")
            logger.info("${value.packageName}/${value.simpleName}")

            codeGenerator.createNewFile(
                dependencies = Dependencies(aggregating = false),
                packageName = classDeclaration.packageName.asString(),
                fileName = classDeclaration.simpleName.asString(),
                extensionName = "json"
            ).use { outputStream ->
                Json.encodeToStream(value, outputStream)
            }
        }
    }
}
