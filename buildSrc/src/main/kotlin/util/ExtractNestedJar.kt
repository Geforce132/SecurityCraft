package util;

import org.gradle.api.GradleException
import org.gradle.api.artifacts.transform.InputArtifact
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformOutputs
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import java.io.File
import java.util.jar.JarInputStream

interface NestedJarParameters : TransformParameters {
	@get:Input
	val nestedJarPath: Property<String>
}

abstract class ExtractNestedJar : TransformAction<NestedJarParameters> {
	@get:InputArtifact
	abstract val inputArtifact: Provider<FileSystemLocation>

	override fun transform(outputs: TransformOutputs) {
		val inputJar = inputArtifact.get().asFile
		val nestedPath = parameters.nestedJarPath.get()

		val outputJar = outputs.file(File(nestedPath).name)

		try {
			JarInputStream(inputJar.inputStream().buffered()).use { jarStream ->
				var entry = jarStream.nextJarEntry
				while (entry != null) {
					if (entry.name == nestedPath) {
						outputJar.outputStream().buffered().use { outputStream ->
							jarStream.copyTo(outputStream)
						}
						return
					}
					entry = jarStream.nextJarEntry
				}
			}
		} catch (e: Exception) {
			throw GradleException("Failed to transform $inputJar: ${e.message}", e)
		}

		throw GradleException("Could not find nested jar '$nestedPath' in '$inputJar'.")
	}
}