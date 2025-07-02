import info.solidsoft.gradle.pitest.PitestPluginExtension

plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("jacoco")
    id("java")
    id("info.solidsoft.pitest") version "1.19.0-rc.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.2"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

testing {
    suites {
        val testIntegration by registering(JvmTestSuite::class) {
            sources {
                kotlin {
                    setSrcDirs(listOf("src/testIntegration/kotlin"))
                }
                compileClasspath += sourceSets.main.get().output
                runtimeClasspath += sourceSets.main.get().output
            }
        }
    }
}

testing {
    suites {
        val testComponent by registering(JvmTestSuite::class) {
            sources {
                kotlin {
                    setSrcDirs(listOf("src/testComponent/kotlin"))
                }
                compileClasspath += sourceSets.main.get().output
                runtimeClasspath += sourceSets.main.get().output
            }
        }
    }
}

testing {
    suites {
        val testArchitecture by registering(JvmTestSuite::class) {
            sources {
                kotlin {
                    setSrcDirs(listOf("src/testArchitecture/kotlin"))
                }
                compileClasspath += sourceSets.main.get().output
                runtimeClasspath += sourceSets.main.get().output
            }
        }
    }
}

val testIntegrationImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val testComponentImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val testArchitectureImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.liquibase:liquibase-core:4.23.2")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    testImplementation("org.testcontainers:postgresql:1.19.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest:kotest-property:5.9.1")
    testImplementation("io.mockk:mockk:1.14.4")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.2")
    testIntegrationImplementation("io.mockk:mockk:1.13.8")
    testIntegrationImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testIntegrationImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testIntegrationImplementation("com.ninja-squad:springmockk:4.0.2")
    testIntegrationImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
    testIntegrationImplementation("org.testcontainers:postgresql:1.19.0")
    testIntegrationImplementation("org.testcontainers:junit-jupiter:1.19.0")
    testIntegrationImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    testComponentImplementation("io.cucumber:cucumber-java:7.14.0")
    testComponentImplementation("io.cucumber:cucumber-spring:7.14.0")
    testComponentImplementation("io.cucumber:cucumber-junit:7.14.0")
    testComponentImplementation("io.cucumber:cucumber-junit-platform-engine:7.14.0")
    testComponentImplementation("io.rest-assured:rest-assured:5.3.2")
    testComponentImplementation("org.junit.platform:junit-platform-suite:1.10.0")
    testComponentImplementation("org.testcontainers:postgresql:1.19.1")
    testComponentImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testComponentImplementation("org.springframework.boot:spring-boot-starter-test")
    testArchitectureImplementation("com.tngtech.archunit:archunit-junit5:1.0.1")
    testArchitectureImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testArchitectureImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}



kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.13"
}

tasks.register<JacocoReport>("jacocoFullReport") {
    dependsOn(tasks.named("test"), tasks.named("testIntegration"), tasks.named("testComponent"), tasks.named("testArchitecture"))
    executionData(tasks.named("test").get(), tasks.named("testIntegration").get(), tasks.named("testComponent").get(), tasks.named("testArchitecture").get())
    sourceSets(sourceSets["main"])

    reports {
        xml.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("custom-reports/jacocoFullReport.xml"))
        html.required.set(true)
    }
}

configure<PitestPluginExtension> {
    targetClasses.set(listOf("com.example.demo.*"))
}

pitest {
    targetClasses.add("com.example.demo.*")
    junit5PluginVersion.set("1.2.0")
    avoidCallsTo.set(setOf("kotlin.jvm.internal"))
    mutators.set(setOf("STRONGER"))
    threads.set(4)
    jvmArgs.add("-Xmx1024m")
    testSourceSets.addAll(sourceSets["test"])
    mainSourceSets.addAll(sourceSets["main"])
    outputFormats.addAll("XML", "HTML")
    excludedClasses.add("**LibraryApplication")
}

detekt {
    config = files("config/detekt.yml") // Path to the Detekt configuration file
    buildUponDefaultConfig = true // Use default Detekt rules as a base
    allRules = false // Disable all rules not explicitly configured
    parallel = true // Enable parallel analysis
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        xml.required.set(true) // Enable XML report
        html.required.set(true) // Enable HTML report
        txt.required.set(false) // Disable TXT report
    }
}