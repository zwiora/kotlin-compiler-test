package org.example

import org.jetbrains.kotlin.buildtools.api.CompilationResult
import org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi
import org.jetbrains.kotlin.buildtools.api.KotlinToolchain
import org.jetbrains.kotlin.buildtools.api.arguments.JvmCompilerArguments
import java.io.File

class ToolchainCompilation : CompilationInterface{
    override val name: String = "ToolchainCompilation"

    @OptIn(ExperimentalBuildToolsApi::class)
    override fun compile(
        sources: List<File>,
        output: File,
        classpath: String,
        outputFormat: OutputFormat
    ): CompilationResult {

        val sourcesPath = sources.map { it.toPath() }
        val outputPath = output.toPath()

        val toolchain = KotlinToolchain.loadImplementation(ClassLoader.getSystemClassLoader())
        val operation = toolchain.jvm.createJvmCompilationOperation(sourcesPath, outputPath)
        operation.compilerArguments[JvmCompilerArguments.JvmCompilerArgument<String?>("CLASSPATH")] = classpath
        operation.compilerArguments[JvmCompilerArguments.JvmCompilerArgument<String?>("MODULE_NAME")] = "web-module"
        operation.compilerArguments[JvmCompilerArguments.JvmCompilerArgument<Boolean>("NO_STDLIB")] = true
        val session = toolchain.createBuildSession()
        try {
            val result = session.executeOperation(operation, toolchain.createInProcessExecutionPolicy())
            return result
        } finally {
            session.close()
        }
    }
}