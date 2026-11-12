package com.wesleyhome.test.jupiter.annotations.processor

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.wesleyhome.test.jupiter.annotations.WithNull
import com.wesleyhome.test.jupiter.annotations.validation.ValuesFromReference

/**
 * Applies the checks the extension makes when the test runs, so a misnamed or mistyped function
 * fails the build instead of the test run.
 */
internal class ValuesFromValidator(private val resolver: Resolver) {

    fun validate(parameter: KSValueParameter, raw: String): List<String> {
        val parameterName = parameter.name?.asString() ?: return emptyList()
        val reference = ValuesFromReference.parse(raw, parameterName)
            ?: return listOf(ValuesFromReference.malformed(raw, parameterName))
        val function = when (val lookup = lookUp(parameter, reference)) {
            is Lookup.Found -> lookup.function
            is Lookup.Problem -> return listOf(lookup.message)
        }
        val arguments = function.parameters.size + if (function.extensionReceiver != null) 1 else 0
        if (arguments != 0) {
            return listOf(reference.takesArguments(arguments))
        }
        val returned = function.returnType?.resolve() ?: return emptyList()
        return checkReturnType(returned, parameter, reference)
    }

    private sealed interface Lookup {
        class Found(val function: KSFunctionDeclaration) : Lookup
        class Problem(val message: String) : Lookup
    }

    private fun lookUp(parameter: KSValueParameter, reference: ValuesFromReference): Lookup {
        val className = reference.className
        val owner = if (className == null) {
            (parameter.parent as? KSFunctionDeclaration)?.parentDeclaration as? KSClassDeclaration
                ?: return Lookup.Problem(reference.missingFunction("the test class"))
        } else {
            resolver.getClassDeclarationByName(resolver.getKSNameFromString(className))
                ?: return topLevel(className, reference)
        }
        hierarchyOf(owner).forEach { type ->
            callableFunctions(type).firstOrNull { it.simpleName.asString() == reference.functionName }
                ?.let { return Lookup.Found(it) }
        }
        val instanceFunction = hierarchyOf(owner)
            .flatMap { it.getDeclaredFunctions() }
            .any { it.simpleName.asString() == reference.functionName }
        return Lookup.Problem(
            if (instanceFunction) reference.instanceFunction() else reference.missingFunction(owner.simpleName.asString())
        )
    }

    /**
     * `com.example.FixturesKt#values` names a top-level function by the class it compiles into, which
     * KSP does not model; a top-level function of that name in the same package is taken as it.
     */
    private fun topLevel(className: String, reference: ValuesFromReference): Lookup {
        val packageName = className.substringBeforeLast('.', "")
        val qualified = if (packageName.isEmpty()) reference.functionName else "$packageName.${reference.functionName}"
        return resolver.getFunctionDeclarationsByName(resolver.getKSNameFromString(qualified), includeTopLevel = true)
            .firstOrNull { it.parentDeclaration == null }
            ?.let { Lookup.Found(it) }
            ?: Lookup.Problem(reference.missingClass())
    }

    private fun hierarchyOf(type: KSClassDeclaration): Sequence<KSClassDeclaration> =
        generateSequence(type) { current ->
            current.superTypes
                .map { it.resolve().declaration }
                .filterIsInstance<KSClassDeclaration>()
                .firstOrNull { it.classKind == ClassKind.CLASS }
        }.takeWhile { it.qualifiedName?.asString() != "kotlin.Any" }

    /** What can be called before the test instance exists: Java statics, object members, companion members. */
    private fun callableFunctions(type: KSClassDeclaration): Sequence<KSFunctionDeclaration> {
        val companion = type.declarations
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.isCompanionObject }
            .flatMap { it.getDeclaredFunctions() }
        val own = type.getDeclaredFunctions()
            .filter { type.classKind == ClassKind.OBJECT || Modifier.JAVA_STATIC in it.modifiers }
        return own + companion
    }

    private fun checkReturnType(
        returned: KSType,
        parameter: KSValueParameter,
        reference: ValuesFromReference
    ): List<String> {
        val declared = returned.declaration.qualifiedName?.asString()
        val element = when {
            declared in PRIMITIVE_ARRAYS -> PRIMITIVE_ARRAYS.getValue(declared!!)(resolver)
            isOneOf(returned, ITERABLE, SEQUENCE, ARRAY) -> returned.arguments.singleOrNull()?.type?.resolve()
            else -> return listOf(reference.wrongReturnType(returned.declaration.simpleName.asString()))
        }
        if (element == null || element.declaration is KSTypeParameter) {
            return emptyList()
        }
        val expected = parameter.type.resolve()
        if (!expected.makeNotNullable().isAssignableFrom(element.makeNotNullable())) {
            return listOf(
                reference.wrongElementType(element.declaration.simpleName.asString(), expected.declaration.simpleName.asString())
            )
        }
        val nullable = expected.isMarkedNullable ||
            parameter.annotations.any { it.shortName.asString() == WithNull::class.simpleName }
        if (element.isMarkedNullable && !nullable) {
            return listOf(reference.nullableElements())
        }
        return emptyList()
    }

    private fun isOneOf(type: KSType, vararg names: String): Boolean = names.any { name ->
        resolver.getClassDeclarationByName(resolver.getKSNameFromString(name))
            ?.asStarProjectedType()
            ?.isAssignableFrom(type) == true
    }

    private companion object {
        const val ITERABLE = "kotlin.collections.Iterable"
        const val SEQUENCE = "kotlin.sequences.Sequence"
        const val ARRAY = "kotlin.Array"

        val PRIMITIVE_ARRAYS: Map<String, (Resolver) -> KSType> = mapOf(
            "kotlin.BooleanArray" to { it.builtIns.booleanType },
            "kotlin.ByteArray" to { it.builtIns.byteType },
            "kotlin.CharArray" to { it.builtIns.charType },
            "kotlin.ShortArray" to { it.builtIns.shortType },
            "kotlin.IntArray" to { it.builtIns.intType },
            "kotlin.LongArray" to { it.builtIns.longType },
            "kotlin.FloatArray" to { it.builtIns.floatType },
            "kotlin.DoubleArray" to { it.builtIns.doubleType }
        )
    }
}
