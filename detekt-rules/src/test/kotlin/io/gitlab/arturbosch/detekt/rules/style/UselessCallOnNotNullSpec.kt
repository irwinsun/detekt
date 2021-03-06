package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object UselessCallOnNotNullSpec : Spek({

    val subject by memoized { UselessCallOnNotNull() }

    lateinit var environment: KotlinCoreEnvironment

    beforeEachTest {
        environment = KtTestCompiler.createEnvironment()
    }

    describe("UselessCallOnNotNull rule") {
        it("reports when calling orEmpty on a list") {
            val code = """val testList = listOf("string").orEmpty()"""
            assertThat(subject.lintWithContext(environment, code)).hasSize(1)
        }

        it("reports when calling orEmpty on a nullable list") {
            val code = """val testList = listOf("string")?.orEmpty()"""
            assertThat(subject.lintWithContext(environment, code)).hasSize(1)
        }

        it("reports when calling orEmpty in a chain") {
            val code = """val testList = listOf("string").orEmpty().map { _ }"""
            assertThat(subject.lintWithContext(environment, code)).hasSize(1)
        }

        it("reports when calling isNullOrBlank on a nullable type") {
            val code = """val testString = ""?.isNullOrBlank()"""
            assertThat(subject.lintWithContext(environment, code)).hasSize(1)
        }

        it("reports when calling isNullOrEmpty on a nullable type") {
            val code = """val testString = ""?.isNullOrEmpty()"""
            assertThat(subject.lintWithContext(environment, code)).hasSize(1)
        }

        it("reports when calling isNullOrEmpty on a string") {
            val code = """val testString = "".isNullOrEmpty()"""
            assertThat(subject.lintWithContext(environment, code)).hasSize(1)
        }

        it("reports when calling orEmpty on a string") {
            val code = """val testString = "".orEmpty()"""
            assertThat(subject.lintWithContext(environment, code)).hasSize(1)
        }

        it("reports when calling orEmpty on a sequence") {
            val code = """val testSequence = listOf(1).asSequence().orEmpty()"""
            assertThat(subject.lintWithContext(environment, code)).hasSize(1)
        }

        it("only reports on a Kotlin list") {
            val code = """
                |fun main() {
                |    val javaList = Java().list.orEmpty()
                |    val list = listOf(1, 2, 3).orEmpty()
                |}
            """.trimMargin()
            assertThat(subject.lintWithContext(environment, code)).hasSize(1)
        }
    }
})
