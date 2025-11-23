package util

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

abstract class MinifyJsonTask : DefaultTask() {
    @get:InputDirectory
    abstract val dir: DirectoryProperty

    @TaskAction
    fun minify() {
        val jsonSlurper = JsonSlurper()
        var jsonMinified = 0
        var jsonBytesBefore = 0L
        var jsonBytesAfter = 0L
        val start = System.currentTimeMillis()

        dir.get().asFileTree.matching {
            include("**/*.json")
        }.forEach { file ->
            jsonMinified++
            jsonBytesBefore += file.length()
            try {
                val parsed = jsonSlurper.parse(file)
                val minified = JsonOutput.toJson(parsed)
                file.writeText(minified)
            } catch (e: Exception) {
                logger.error("JSON Error in ${file.path}", e)
                throw e
            }
            jsonBytesAfter += file.length()
        }

        logger.lifecycle("Minified $jsonMinified JSON files. Reduced ${jsonBytesBefore / 1024} kB → ${(jsonBytesAfter / 1024)} kB. Took ${System.currentTimeMillis() - start} ms")
    }
}