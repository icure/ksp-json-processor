# ksp-json-processor

A KSP processor that writes each declaration of the visited packages as json: the IR that sdk-codegen reads. charix builds the IR, so it holds declarations and annotations, not expressions.

It visits Kraken's REST controllers and DTOs and Cardinal's SDK packages. Other packages are added through the `ksp-json.packages` option, a comma-separated list of packages, subpackages included:

```kotlin
dependencies {
    add("kspJvm", "com.icure:ksp-json-processor")
}

ksp {
    arg("ksp-json.packages", "com.icure.khermes.dto")
}
```

Consumers build it from source with an included build:

```kotlin
includeBuild("ksp-json-processor") {
    dependencySubstitution {
        substitute(module("com.icure:ksp-json-processor")).using(project(":library"))
    }
}
```

The json lands in `build/generated/ksp/<target>/<sourceSet>/resources`, one file per top-level declaration.
