package com.wesleyhome.test.jupiter.annotations.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSValueParameter
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
