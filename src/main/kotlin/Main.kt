package org.example

import java.io.File
import java.nio.file.Paths
import org.jetbrains.kotlin.buildtools.api.CompilationService
import org.jetbrains.kotlin.buildtools.api.ProjectId
import org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi
import org.jetbrains.kotlin.buildtools.api.KotlinToolchain
import org.jetbrains.kotlin.buildtools.api.arguments.JvmCompilerArguments

enum class OutputFormat {
    JAR,
    DIRECTORY
}

fun main() {
    val outputFormat = OutputFormat.JAR

    println("Starting Kotlin compiler client...")

    // Path to the test project
    val testProjectPath = Paths.get("simple-test").toAbsolutePath().toString()
    val sourceFile = File("$testProjectPath/src/Main.kt")
    require(sourceFile.exists()) { "No source file: ${sourceFile.absolutePath}" }
    val sources = listOf(sourceFile)

    val outputDir = File("$testProjectPath/result")
    println("Test project path: $testProjectPath")
    println("Source file exists: ${sourceFile.exists()}")

    outputDir.mkdirs()

    try {
        compileUsingToolchain(sources, outputDir, outputFormat)
    } catch (e: Exception) {
        println("Failed to compile using kotlin-build-tools: ${e.message}")
    }
}

@OptIn(ExperimentalBuildToolsApi::class)
fun compileUsingToolchain(sources: List<File>, outputDir: File, outputFormat: OutputFormat = OutputFormat.JAR) {
    println("Compiling Kotlin project using toolchain")
    println("Source files: ${sources.joinToString("\n") { it.absolutePath }}")
    println("Output directory: ${outputDir.absolutePath}")
    println("Output format: $outputFormat")

    val sourcesPath = sources.map { it.toPath() }

    val destination = when (outputFormat) {
        OutputFormat.JAR -> File(outputDir, outputDir.name + ".jar").toPath()
        OutputFormat.DIRECTORY -> outputDir.toPath()
    }

    val runtimeClasspath = System.getProperty("java.class.path")
        ?: error("java.class.path is not set")
    println("Using runtime classpath with ${runtimeClasspath.split(File.pathSeparator).size} entries")


    val toolchain = KotlinToolchain.loadImplementation(ClassLoader.getSystemClassLoader())
    val operation = toolchain.jvm.createJvmCompilationOperation(sourcesPath, destination)
    operation.compilerArguments[JvmCompilerArguments.JvmCompilerArgument<String?>("CLASSPATH")] = runtimeClasspath
    operation.compilerArguments[JvmCompilerArguments.JvmCompilerArgument<String?>("MODULE_NAME")] = "web-module"
    operation.compilerArguments[JvmCompilerArguments.JvmCompilerArgument<Boolean>("NO_STDLIB")] = true
//    println("Arguments: ${operation.compilerArguments.}")
    toolchain.createBuildSession().use { it.executeOperation(operation, toolchain.createInProcessExecutionPolicy())}

//    val arguments = listOf(
//        "-cp", runtimeClasspath,
//        "-module-name", "web-module",
//        "-no-stdlib", "-no-reflect",
//        "-progressive",
////        "-kotlin-home", kotlinHome.absolutePath,
//        "-d", destination.absolutePath,
//    )

    // Load the CompilationService implementation
//    val service = CompilationService.loadImplementation(ClassLoader.getSystemClassLoader())
//    println("Loaded CompilationService implementation: ${service.javaClass.name}")
//
//    // Create a random project ID
//    val projectId = ProjectId.RandomProjectUUID()
//    println("Created project ID: $projectId")
//
//    // Create compiler execution strategy configuration
//    val strategyConfig = service.makeCompilerExecutionStrategyConfiguration().useInProcessStrategy()
//    println("Created compiler execution strategy configuration")
//
//    // Create JVM compilation configuration
//    val compilationConfig = service.makeJvmCompilationConfiguration()
//    println("Created JVM compilation configuration")
//
//    // Prepare the list of source files
//
//    val runtimeClasspath = System.getProperty("java.class.path")
//        ?: error("java.class.path is not set")
//    println("Using runtime classpath with ${runtimeClasspath.split(File.pathSeparator).size} entries")
//
//    // Prepare compiler arguments
//    val destination = when (outputFormat) {
//        OutputFormat.JAR -> File(outputDir, outputDir.name + ".jar")
//        OutputFormat.DIRECTORY -> outputDir
//    }

//    val kotlinHome = System.getenv("KOTLIN_HOME")?.let(::File)
//        ?: System.getProperty("kotlin.home.dir")?.let(::File)
//        ?: error("Set KOTLIN_HOME env or -Dkotlin.home.dir to the Kotlin distribution (kotlinc) directory")
//    val libDir = File(kotlinHome, "lib")
//    require(File(libDir, "kotlin-stdlib.jar").exists()) {
//        "Invalid kotlin-home: missing ${File(libDir, "kotlin-stdlib.jar").absolutePath}"
//    }

//    val arguments = listOf(
//        "-cp", runtimeClasspath,
//        "-module-name", "web-module",
//        "-no-stdlib", "-no-reflect",
//        "-progressive",
////        "-kotlin-home", kotlinHome.absolutePath,
//        "-d", destination.absolutePath,
//    )
//    println("Compiler arguments: $arguments")
//
//    // Compile the Kotlin code
//    println("Executing compilation using CompilationService...")
//    val result = service.compileJvm(projectId, strategyConfig, compilationConfig, sources, arguments)
//
//    // Check the result
//    when (result) {
//        org.jetbrains.kotlin.buildtools.api.CompilationResult.COMPILATION_SUCCESS -> {
//            println("Compilation successful!")
//
//            // List the generated class files
//            if (outputFormat == OutputFormat.JAR) {
//                java.util.jar.JarFile(destination).use { jar ->
//                    jar.entries().asSequence()
//                        .filter { !it.isDirectory && it.name.endsWith(".class") }
//                        .forEach { println(" - ${it.name}") }
//                }
//            } else {
//                // For directory output, list class files in the directory
//                destination.walk()
//                    .filter { it.isFile && it.name.endsWith(".class") }
//                    .forEach { println(" - ${it.relativeTo(destination).path}") }
//            }
//        }
//        org.jetbrains.kotlin.buildtools.api.CompilationResult.COMPILATION_ERROR -> {
//            println("Compilation failed with errors")
//        }
//        org.jetbrains.kotlin.buildtools.api.CompilationResult.COMPILATION_OOM_ERROR -> {
//            println("Compilation failed due to out of memory error")
//        }
//        org.jetbrains.kotlin.buildtools.api.CompilationResult.COMPILER_INTERNAL_ERROR -> {
//            println("Compilation failed due to internal compiler error")
//        }
//    }

    // Finalize the compilation
//    service.finishProjectCompilation(projectId)
    println("Finalized project compilation")
}

@OptIn(ExperimentalBuildToolsApi::class)
fun compileUsingKotlinBuildTools(sources: List<File>, outputDir: File, outputFormat: OutputFormat = OutputFormat.JAR) {
    println("Compiling Kotlin project using kotlin-build-tools...")
    println("Source files: ${sources.joinToString("\n") { it.absolutePath }}")
    println("Output directory: ${outputDir.absolutePath}")
    println("Output format: $outputFormat")

    // Load the CompilationService implementation
    val service = CompilationService.loadImplementation(ClassLoader.getSystemClassLoader())
    println("Loaded CompilationService implementation: ${service.javaClass.name}")

    // Create a random project ID
    val projectId = ProjectId.RandomProjectUUID()
    println("Created project ID: $projectId")

    // Create compiler execution strategy configuration
    val strategyConfig = service.makeCompilerExecutionStrategyConfiguration().useInProcessStrategy()
    println("Created compiler execution strategy configuration")

    // Create JVM compilation configuration
    val compilationConfig = service.makeJvmCompilationConfiguration()
    println("Created JVM compilation configuration")

    // Prepare the list of source files

    val runtimeClasspath = System.getProperty("java.class.path")
        ?: error("java.class.path is not set")
    println("Using runtime classpath with ${runtimeClasspath.split(File.pathSeparator).size} entries")

    // Prepare compiler arguments
    val destination = when (outputFormat) {
        OutputFormat.JAR -> File(outputDir, outputDir.name + ".jar")
        OutputFormat.DIRECTORY -> outputDir
    }

//    val kotlinHome = System.getenv("KOTLIN_HOME")?.let(::File)
//        ?: System.getProperty("kotlin.home.dir")?.let(::File)
//        ?: error("Set KOTLIN_HOME env or -Dkotlin.home.dir to the Kotlin distribution (kotlinc) directory")
//    val libDir = File(kotlinHome, "lib")
//    require(File(libDir, "kotlin-stdlib.jar").exists()) {
//        "Invalid kotlin-home: missing ${File(libDir, "kotlin-stdlib.jar").absolutePath}"
//    }

    val arguments = listOf(
        "-cp", runtimeClasspath,
        "-module-name", "web-module",
        "-no-stdlib", "-no-reflect",
        "-progressive",
//        "-kotlin-home", kotlinHome.absolutePath,
        "-d", destination.absolutePath,
    )
    println("Compiler arguments: $arguments")

    // Compile the Kotlin code
    println("Executing compilation using CompilationService...")
    val result = service.compileJvm(projectId, strategyConfig, compilationConfig, sources, arguments)

    // Check the result
    when (result) {
        org.jetbrains.kotlin.buildtools.api.CompilationResult.COMPILATION_SUCCESS -> {
            println("Compilation successful!")

            // List the generated class files
            if (outputFormat == OutputFormat.JAR) {
                java.util.jar.JarFile(destination).use { jar ->
                    jar.entries().asSequence()
                        .filter { !it.isDirectory && it.name.endsWith(".class") }
                        .forEach { println(" - ${it.name}") }
                }
            } else {
                // For directory output, list class files in the directory
                destination.walk()
                    .filter { it.isFile && it.name.endsWith(".class") }
                    .forEach { println(" - ${it.relativeTo(destination).path}") }
            }
        }
        org.jetbrains.kotlin.buildtools.api.CompilationResult.COMPILATION_ERROR -> {
            println("Compilation failed with errors")
        }
        org.jetbrains.kotlin.buildtools.api.CompilationResult.COMPILATION_OOM_ERROR -> {
            println("Compilation failed due to out of memory error")
        }
        org.jetbrains.kotlin.buildtools.api.CompilationResult.COMPILER_INTERNAL_ERROR -> {
            println("Compilation failed due to internal compiler error")
        }
    }

    // Finalize the compilation
    service.finishProjectCompilation(projectId)
    println("Finalized project compilation")
}