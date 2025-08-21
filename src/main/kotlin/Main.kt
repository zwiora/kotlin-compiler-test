package org.example

import org.example.OutputFormat.*
import org.example.CompilationMethod.*
import java.io.File
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

fun main() {
    val executionTime = measureTimeMillis {
        val outputFormat = JAR
        val compilationMethod = TOOLCHAIN
        val testProject = "simple-test"

        println("Starting Kotlin compiler client...")
        println("Output format: $outputFormat")
        println("Compilation method: $compilationMethod")

        val (sources, output) = prepareDirectories(testProject)

        try {
            val compilation: CompilationInterface
            when (compilationMethod) {
                COMPILATION_SERVICE -> compilation = CompilationServiceCompilation()
                TOOLCHAIN -> compilation = ToolchainCompilation()
            }
            compile(compilation, sources, output, outputFormat)

        } catch (e: Exception) {
            println("Failed to compile using $compilationMethod: ${e.message}")
        }
    }
    println("Total execution time: $executionTime ms")
}

fun prepareDirectories(testProject: String): Pair<List<File>, File> {
    val testProjectPath = Paths.get(testProject).toAbsolutePath().toString()
    val sourceFile = File("$testProjectPath/src/Main.kt")
    require(sourceFile.exists()) { "No source file: ${sourceFile.absolutePath}" }
    val sources = listOf(sourceFile)
    val output = File("$testProjectPath/result")
    output.deleteRecursively()
    output.mkdirs()
    println("Test project path: $testProjectPath")
    println("Source files: ${sources.joinToString("\n") { it.absolutePath }}")
    println("Output directory: ${output.absolutePath}")

    return Pair(sources, output)
}

fun compile(compilation: CompilationInterface, sources: List<File>, outputDir: File, outputFormat: OutputFormat = JAR) {
    println("Compiling Kotlin project using ${compilation.name}")

    val output = when (outputFormat) {
        JAR -> File(outputDir, outputDir.name + ".jar")
        DIRECTORY -> outputDir
    }

    val runtimeClasspath = System.getProperty("java.class.path")
        ?: error("java.class.path is not set")
    println("Using runtime classpath with ${runtimeClasspath.split(File.pathSeparator).size} entries")

    val result = compilation.compile(sources, output, runtimeClasspath, outputFormat)

    when (result) {
        org.jetbrains.kotlin.buildtools.api.CompilationResult.COMPILATION_SUCCESS -> {
            println("Compilation successful!")

            if (outputFormat == JAR) {
                java.util.jar.JarFile(output).use { jar ->
                    jar.entries().asSequence()
                        .filter { !it.isDirectory && it.name.endsWith(".class") }
                        .forEach { println(" - ${it.name}") }
                }
            }else {
                output.walk()
                    .filter { it.isFile && it.name.endsWith(".class") }
                    .forEach { println(" - ${it.relativeTo(output).path}") }
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
    println("Finalized project compilation")
}
