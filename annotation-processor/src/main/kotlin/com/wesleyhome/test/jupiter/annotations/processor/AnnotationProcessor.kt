package com.wesleyhome.test.jupiter.annotations.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.WithNull
import com.wesleyhome.test.jupiter.annotations.datetime.InstantRangeSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateRangeSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateTimeRangeSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalTimeRangeSource
import com.wesleyhome.test.jupiter.annotations.number.DoubleRangeSource
import com.wesleyhome.test.jupiter.annotations.number.FloatRangeSource
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.annotations.number.LongRangeSource
import kotlin.reflect.KClass

class AnnotationProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        RANGE_ANNOTATIONS.forEach { annotationClass ->
            val simpleName = annotationClass.simpleName ?: return@forEach
            resolver.getSymbolsWithAnnotation(annotationClass.qualifiedName ?: return@forEach)
                .filterIsInstance<KSValueParameter>()
                .forEach { parameter -> validate(parameter, simpleName) }
        }
        warnAboutInferredNulls(resolver)
        return emptyList()
    }

    private fun validate(parameter: KSValueParameter, simpleName: String) {
        val annotation = parameter.annotations
            .firstOrNull { it.shortName.asString() == simpleName }
            ?: return
        val arguments = annotation.arguments
            .mapNotNull { argument -> argument.name?.asString()?.let { it to argument.value } }
            .toMap()
        RangeAnnotationValidator.validate(simpleName, arguments)
            .forEach { error -> environment.logger.error(error, parameter) }
    }

    /**
     * A nullable Kotlin parameter gets a null case inferred from its type. That inference cannot be
     * expressed in Java and is invisible at the use site, so it is due to be replaced by [WithNull].
     * Warning now means the change does not silently drop a case later.
     */
    private fun warnAboutInferredNulls(resolver: Resolver) {
        resolver.getSymbolsWithAnnotation(GeneratedParametersTest::class.qualifiedName ?: return)
            .filterIsInstance<KSFunctionDeclaration>()
            .flatMap { it.parameters }
            .filter { it.type.resolve().isMarkedNullable }
            .filterNot { parameter -> parameter.hasAnnotation(WithNull::class.simpleName) }
            .filter { parameter -> parameter.annotations.any { it.isSourceAnnotation() } }
            .forEach { parameter ->
                environment.logger.warn(
                    "[${parameter.name?.asString()}] gets a null case from its nullable type. Annotate it " +
                        "@WithNull; nulls inferred from the type will stop being generated in a future release.",
                    parameter
                )
            }
    }

    private fun KSValueParameter.hasAnnotation(simpleName: String?): Boolean =
        annotations.any { it.shortName.asString() == simpleName }

    private fun KSAnnotation.isSourceAnnotation(): Boolean =
        annotationType.resolve().declaration.annotations.any { it.shortName.asString() == "SourceProvider" }

    private companion object {
        val RANGE_ANNOTATIONS: List<KClass<out Annotation>> = listOf(
            IntRangeSource::class,
            LongRangeSource::class,
            FloatRangeSource::class,
            DoubleRangeSource::class,
            LocalDateRangeSource::class,
            LocalDateTimeRangeSource::class,
            LocalTimeRangeSource::class,
            InstantRangeSource::class
        )
    }
}
