package com.icure.codegen.generator

object DtoGeneratorsOptions {
	/**
	 * To prevent a method from the controller from being converted to an SDK method, you can add it to the .dtoignore file.
	 * The file must be in the resources/com/icure/codegen folder of the project.
	 * Each line of the file specifies a qualified dto name
	 */
	val dtoIgnore = this::class.java.getResource(".dtoignore")?.readText()?.split("\n").orEmpty().toSet()
	val tsDtoIgnore = this::class.java.getResource(".tsdtoignore")?.readText()?.split("\n").orEmpty().toSet()
	val pyDtoIgnore = this::class.java.getResource(".pydtoignore")?.readText()?.split("\n").orEmpty().toSet()
	val dartDtoIgnore = this::class.java.getResource(".dartdtoignore")?.readText()?.split("\n").orEmpty().toSet()
	val pyApiIgnore = parseApiIgnoreConfig(".pyapiignore")

	private fun parseApiIgnoreConfig(resourceName: String): Map<String, ApiIgnoreConfiguration> =
		this::class.java.getResource(resourceName)?.readText()?.split("\n").orEmpty().filter {
			it.isNotBlank()
		}.map { entry ->
			val split = entry.split("::")
			when {
				split.size == 1 -> Pair(entry, null)
				split.size == 2 && split.all { it.isNotBlank() } -> Pair(split[0], split[1])
				else -> throw IllegalStateException("Invalid api ignore config $resourceName for entry $entry")
			}
		}.groupBy { it.first }.mapValues { (key, values) ->
			val methodNames = values.map { it.second }
			if (methodNames.size == 1) {
				methodNames.first()?.let {
					ApiIgnoreConfiguration.Methods(setOf(it))
				} ?: ApiIgnoreConfiguration.All
			} else {
				ApiIgnoreConfiguration.Methods(
					methodNames.mapNotNull { it }.toSet().also {
						check(it.size == methodNames.size) {
							// Duplicate entries or All+Method-specific entries
							"Invalid api ignore config $resourceName for key $key"
						}
					}
				)
			}
		}
}