package com.icure.codegen.generator

sealed interface ApiIgnoreConfiguration {
	data object All : ApiIgnoreConfiguration
	data class Methods(val methodNames : Set<String>) : ApiIgnoreConfiguration
}