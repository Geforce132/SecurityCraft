import groovy.json.JsonOutput
import groovy.json.JsonSlurper

plugins {
	id("net.neoforged.moddev") version "2.0.107"
}

base {
	archivesName.set("securitycraft")
	group = "net.geforcemods.securitycraft"
	version = "1.10.1-beta1"
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

tasks.processResources {
	exclude(".cache")
	duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

sourceSets {
	main {
		resources {
			srcDir("src/generated/resources") //include generated files
		}
	}
}

val minecraftVersion: String = "1.21.10"
neoForge {
	version = "21.10.28-beta"
	validateAccessTransformers = true

	runs {
		configureEach {
			logLevel = org.slf4j.event.Level.DEBUG
			gameDirectory = file("run/" + name)
			ideName = "SC $minecraftVersion " + ideName.get()
		}

		register("client") {
			client()
		}

		register("client2") {
			client()
			gameDirectory = file("run/client")
			programArguments.addAll("--username", "Dev2")
		}

		register("server") {
			server()
			programArgument("-nogui")
		}

		register("data") {
			clientData()
			programArguments.addAll("--mod", "securitycraft", "--all", "--output", file("src/generated/resources/").absolutePath)
		}
	}

	mods {
		create("securitycraft") {
			sourceSet(sourceSets.main.get())
		}
	}
}

repositories {
	exclusiveContent {
		forRepository { maven("https://www.cursemaven.com") }
		filter { includeGroup("curse.maven") }
	}

	exclusiveContent {
		forRepository { maven("https://api.modrinth.com/maven") }
		filter { includeGroup("maven.modrinth") }
	}
}

dependencies {
	compileOnly("curse.maven:architectury-api-419699:5553800") //ftb teams dependency
	compileOnly("curse.maven:ftb-library-forge-404465:5557408") //ftb teams dependency
	compileOnly("curse.maven:ftb-teams-forge-404468:5448371")
	implementation("curse.maven:jei-238222:7090453")
	compileOnly("curse.maven:the-one-probe-245211:5502323")
	implementation("curse.maven:jade-324717:7056468")
	compileOnly("curse.maven:betterf3-401648:6685252")
	compileOnly("curse.maven:cloth-config-348521:6669837") //betterf3 dependency
	implementation("curse.maven:wthit-forge-455982:7095465")
	implementation("curse.maven:badpackets-615134:7066076") //wthit dependency
	compileOnly("curse.maven:projecte-226410:3955047")
	compileOnly("curse.maven:embeddium-908741:6116910")
	compileOnly("maven.modrinth:sodium:mc1.21.6-0.6.13-neoforge") //incompatible with embeddium
}

tasks.withType<Jar> {
	exclude("net/geforcemods/securitycraft/datagen/**") //exclude files from the built jar that are only used to generate the assets & data
	archiveFileName = "[$minecraftVersion] SecurityCraft v$version.jar"
	manifest {
		attributes(mapOf("Specification-Title" to "SecurityCraft",
				"Specification-Vendor" to "Geforce, bl4ckscor3, Redstone_Dubstep",
				"Specification-Version" to "$version",
				"Implementation-Title" to "SecurityCraft",
				"Implementation-Version" to "$version",
				"Implementation-Vendor" to "Geforce, bl4ckscor3, Redstone_Dubstep"))
	}
}

tasks.withType<JavaCompile> {
	options.compilerArgs.addAll(listOf("-Xmaxerrs", "10000"))
	options.encoding = "UTF-8"
	options.release.set(21)
}

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

tasks.register<MinifyJsonTask>("minifyJson") {
	dir.set(layout.buildDirectory.dir("resources/main"))
}

tasks.named<ProcessResources>("processResources") {
	finalizedBy("minifyJson")
}