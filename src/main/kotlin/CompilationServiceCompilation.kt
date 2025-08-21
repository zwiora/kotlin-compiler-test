package org.example

import org.jetbrains.kotlin.buildtools.api.CompilationResult
import org.jetbrains.kotlin.buildtools.api.CompilationService
import org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi
import org.jetbrains.kotlin.buildtools.api.ProjectId
import java.io.File

class CompilationServiceCompilation : CompilationInterface {
    override val name: String = "CompilationService"

    @OptIn(ExperimentalBuildToolsApi::class)
    override fun compile(
        sources: List<File>,
        output: File,
        classpath: String,
        outputFormat: OutputFormat
    ): CompilationResult{
        val service = CompilationService.loadImplementation(ClassLoader.getSystemClassLoader())
        val projectId = ProjectId.RandomProjectUUID()
        val strategyConfig = service.makeCompilerExecutionStrategyConfiguration().useInProcessStrategy()
        val compilationConfig = service.makeJvmCompilationConfiguration()

    //    val kotlinHome = System.getenv("KOTLIN_HOME")?.let(::File)
    //        ?: System.getProperty("kotlin.home.dir")?.let(::File)
    //        ?: error("Set KOTLIN_HOME env or -Dkotlin.home.dir to the Kotlin distribution (kotlinc) directory")
    //    val libDir = File(kotlinHome, "lib")
    //    require(File(libDir, "kotlin-stdlib.jar").exists()) {
    //        "Invalid kotlin-home: missing ${File(libDir, "kotlin-stdlib.jar").absolutePath}"
    //    }

        val arguments = listOf(
            "-cp", classpath,
            "-module-name", "web-module",
            "-no-stdlib", "-no-reflect",
            "-progressive",
//        "-kotlin-home", kotlinHome.absolutePath,
            "-d", output.absolutePath,
        )

        val result = service.compileJvm(projectId, strategyConfig, compilationConfig, sources, arguments)
        service.finishProjectCompilation(projectId)

        return result
    }
}