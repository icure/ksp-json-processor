package com.icure.codegen.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSVisitorVoid

abstract class Generator (
    protected val codeGenerator: CodeGenerator,
    protected val logger: KSPLogger
) : KSVisitorVoid()
