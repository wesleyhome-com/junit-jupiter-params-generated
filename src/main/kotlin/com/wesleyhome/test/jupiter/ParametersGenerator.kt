package com.wesleyhome.test.jupiter

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import java.util.concurrent.atomic.AtomicLong
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction

class ParametersGenerator(
	private val parameterClassList: List<KParameter>,
	private val testObject: Any,
	private val dataProviderClasses: List<KClass<*>>,
	private val testMethodName: String
) : Iterable<Arguments> {

	private val testClass = testObject.javaClass.kotlin
	private val filterMethod: KFunction<Boolean>?
	private val options: List<List<Any?>>
	private val totalPermutations: Long
	private val pointers: Array<Int>
	private val current = AtomicLong(0)

	init {
		filterMethod = (testClass.allFunctions()
			.firstOrNull { isFilterMethod(it) })
			?.let { it as KFunction<Boolean> }
		if (filterMethod != null) {
			filterMethod.javaMethod!!.trySetAccessible()
		}
		options = parameterClassList.map(::deriveOption)
		totalPermutations = options
			.asSequence()
			.map { it.size }
			.map { it.toLong() }
			.reduce { acc, i -> acc * i }
		pointers = Array(options.size) { 0 }
	}

	private fun deriveOption(parameter: KParameter): List<Any?> {
		val javaType = (parameter.type.classifier as KClass<*>).java
		val booleanClass = java.lang.Boolean::class.java
		val booleanType = java.lang.Boolean.TYPE
		val kotlinBoolean = Boolean::class
		val values = when {
			javaType.isEnum -> {
				javaType.enumConstants.toList().let { list ->
					if (parameter.type.isMarkedNullable) list + null else list
				}
			}

			javaType in listOf(booleanType, booleanClass, kotlinBoolean)
			-> listOf(true, false)

			else -> createOptionsFromProviders(parameter)
		}
		return values
	}

	private fun createOptionsFromProviders(parameter: KParameter): List<Any?> {
		return dataProviderClasses.firstNotNullOfOrNull {
			getOptionsFromProvider(it, parameter)
		} ?: emptyList()
	}

	private fun getOptionsFromProvider(providerClass: KClass<*>, parameter: KParameter): List<Any?>? =
		providerClass.allFunctions()
			.asSequence()
			.filter {
				val staticNonTestInstance = it.isStaticNonTestInstance(providerClass)
				val instanceMethodOfTestClass = it.isInstanceMethodOfTestClass(testClass)
				staticNonTestInstance || instanceMethodOfTestClass
			}
			.filter {
				val noArg = it.isNoArg()
				noArg
			}
			.filter {
				val returnsIterable = it.returnsIterable(parameter)
				returnsIterable
			}
			.flatMap {
				val invoke = invoke(it as KFunction<Iterable<*>>)
				invoke
			}
			.toList()
			.ifEmpty { null }

	private fun invoke(function: KFunction<Iterable<*>>): Iterable<Any?> {
		function.javaMethod?.trySetAccessible()
		return if (function.isStatic()) {
			function.call()
		} else {
			function.call(testObject)
		}
	}

	private fun isFilterMethod(function: KFunction<*>): Boolean {
		val methodName: String = function.name
		val returnType: KType = function.returnType
		val functionParameters = function.parameters.drop(1).map {
			it.type
		}
		val expectedParameters = parameterClassList.map { it.type }
		val parametersEqual = functionParameters == expectedParameters
		val returnBoolean = returnType.isSubtypeOf(Boolean::class.starProjectedType)
		val containsFilterMethodName = methodName == testMethodName + "_filter"
		return parametersEqual && returnBoolean && containsFilterMethodName
	}

	override fun iterator(): Iterator<Arguments> {
		return object : Iterator<Arguments> {
			override fun hasNext(): Boolean {
				if (filterMethod == null) {
					return current.get() < totalPermutations
				}
				while (current.get() < totalPermutations) {
					val argument = createArgument()
					if (shouldFilter(argument)) {
						return true
					}
					increment()
				}
				return false
			}

			override fun next(): Arguments {
				return createArgument().also { increment() }
			}
		}
	}

	private fun shouldFilter(argument: Arguments): Boolean {
		return filterMethod!!.call(testObject, *argument.get())
	}

	private fun increment() {
		current.incrementAndGet()
		incrementIndex(pointers.size - 1)
	}

	private fun incrementIndex(index: Int) {
		if (index < 0) {
			return
		}
		val parameterIndex = pointers[index] + 1
		val length = options[index].size
		if (parameterIndex < length) {
			pointers[index] = parameterIndex
		} else {
			pointers[index] = 0
			incrementIndex(index - 1)
		}
	}

	fun createArgument(): Arguments {
		val indexed = options
			.mapIndexed { index, list ->
				val paramIndex = pointers[index]
				list[paramIndex]
			}
		return Arguments.of(*indexed.toTypedArray())
	}

	companion object {
		fun create(context: ExtensionContext): ParametersGenerator {
			val requiredTestMethod = context.requiredTestMethod.kotlinFunction!!
			val requiredTestClass = context.requiredTestClass.kotlin
			val requiredTestInstance = ReflectionHelper.invokeConstructor(requiredTestClass.java)
			val parametersSource = requiredTestMethod.findAnnotation<ParametersSource>()!!
			val methodName = requiredTestMethod.name
			val parameterClassList = requiredTestMethod.parameters.drop(1)
			val dataProviderClasses = parametersSource.value.toList() + requiredTestClass
			return ParametersGenerator(parameterClassList, requiredTestInstance, dataProviderClasses, methodName)
		}
	}
}
