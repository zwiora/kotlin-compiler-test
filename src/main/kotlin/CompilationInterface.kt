package org.example

import org.jetbrains.kotlin.buildtools.api.CompilationResult
import java.io.File

interface CompilationInterface {
    val name: String

    fun compile(sources: List<File>, output: File, classpath: String, outputFormat: OutputFormat = OutputFormat.JAR): CompilationResult
}