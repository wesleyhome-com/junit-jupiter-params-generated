package com.wesleyhome.test.jupiter.annotations.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSValueParameter
import com.wesleyhome.test.jupiter.annotations.datetime.InstantRangeSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateRangeSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateTimeRangeSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalTimeSource
import com.wesleyhome.test.jupiter.annotations.number.DoubleRangeSource
import com.wesleyhome.test.jupiter.annotations.number.FloatRangeSource
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.annotations.number.LongRangeSource
import com.wesleyhome.test.jupiter.annotations.validation.number.NumberRangeValidator
import java.time.LocalDate
import java.time.LocalDateTime

class AnnotationProcessor(private val environment: SymbolProcessorEnvironment) :
    SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val logger = environment.logger
        val numberRangeAnnotations = listOf(
            IntRangeSource::class,
            LongRangeSource::class,
            FloatRangeSource::class,
            DoubleRangeSource::class
        )
        val dateTimeRangeAnnotations = listOf(
            LocalDateRangeSource::class,
            LocalDateTimeRangeSource::class,
            LocalTimeSource::class,
            InstantRangeSource::class
        )
        numberRangeAnnotations.map { annClass -> annClass to annClass.qualifiedName!! }
            .map { (annClass, clsName) -> annClass to resolver.getSymbolsWithAnnotation(clsName) }
            .forEach { (annClass, sequence) ->
                sequence.forEach { p ->
                    val parameter = p as KSValueParameter
                    val annotations = parameter.annotations
                    val annotation = annotations.first { ann ->
                        ann.shortName.asString() == annClass.simpleName
                    }
                    val arguments = annotation.arguments
                    val map: Map<String, Number> =
                        arguments.filterNot { it.name!!.asString() == "ascending"}.associate { it.name!!.asString() to it.value!! as Number }
                    val errors =
                        NumberRangeValidator.validate(map.getValue("min"), map.getValue("max"), map["increment"])
                    if (errors.isNotEmpty()) {
                        errors.forEach { err -> logger.error(err, p) }
                    }
                }
            }

        return emptyList()
    }

}
