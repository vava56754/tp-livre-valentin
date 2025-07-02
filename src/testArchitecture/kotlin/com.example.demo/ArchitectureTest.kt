package com.example.demo

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.Test

class ArchitectureTest {

    private val basePackage = "com.example.demo"

    @Test
    fun `it should respect the clean architecture concept`() {
        val importedClasses: JavaClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages(basePackage)

        val rule = layeredArchitecture()
            .consideringAllDependencies()
            // Définir les couches
            .layer("Domain").definedBy("$basePackage.domain..")
            .layer("Infrastructure").definedBy("$basePackage.infrastructure..")
            .layer("Driven").definedBy("$basePackage.infrastructure.driven..")
            .layer("Driving").definedBy("$basePackage.infrastructure.driving..")
            .layer("Model").definedBy("$basePackage.domain.model..")
            .layer("Port").definedBy("$basePackage.domain.port..")
            .layer("Usecase").definedBy("$basePackage.domain.usecase..")
            .layer("Application").definedBy("$basePackage.infrastructure.application..")
            .layer("Controller").definedBy("$basePackage.infrastructure.driving.controller..")
            .layer("Standard API").definedBy(
                "java..", "kotlin..", "kotlinx..", "org.jetbrains.annotations.."
            )
            .withOptionalLayers(true)
            // Vérifier les dépendances
            .whereLayer("Domain").mayNotBeAccessedByAnyLayer()
            .whereLayer("Model").mayOnlyAccessLayers("Domain", "Standard API")
            .whereLayer("Port").mayOnlyAccessLayers("Domain", "Standard API")
            .whereLayer("Usecase").mayOnlyAccessLayers("Domain", "Standard API")
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyAccessLayers("Infrastructure", "Standard API")
            .whereLayer("Driven").mayOnlyAccessLayers("Infrastructure", "Standard API")
            .whereLayer("Driving").mayOnlyAccessLayers("Infrastructure", "Standard API")
            .whereLayer("Controller").mayOnlyAccessLayers("Infrastructure", "Driving", "Standard API")

        rule.check(importedClasses)
    }
}