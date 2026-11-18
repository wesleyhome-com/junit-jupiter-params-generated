package com.wesleyhome.test.jupiter.generator

import java.io.DataInputStream
import java.lang.invoke.MethodType
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.kotlinFunction

/**
 * The names [this] method's parameters were declared with, or null when its class file doesn't
 * record them. Kotlin records them in its own metadata. Java records them with `-parameters`, and
 * otherwise in the debug information that `-g` adds, which Gradle and Maven pass by default.
 *
 * A bridge for a generic override declares nothing. Kotlin gives it the overriding method's names
 * in the debug information, but only the overriding method should bind by them.
 */
internal fun Method.declaredParameterNames(): List<String>? =
    if (isBridge) null else kotlinParameterNames() ?: compiledParameterNames() ?: LocalVariableNames.of(this)

/**
 * Kotlin reflection describes a Java method too, with the names Java reflection gives it, so only a
 * Kotlin class's are taken. An extension receiver or a context parameter is a JVM parameter without
 * being a value parameter, so those names don't line up and aren't used.
 */
private fun Method.kotlinParameterNames(): List<String>? {
    if (!declaringClass.isAnnotationPresent(Metadata::class.java)) return null
    val function = kotlinFunction ?: return null
    return function.valueParameters.mapNotNull { it.name }.takeIf { it.size == parameterCount }
}

private fun Method.compiledParameterNames(): List<String>? =
    parameters.takeIf { all -> all.all { it.isNamePresent } }?.map { it.name }

/**
 * Parameter names from a class file's LocalVariableTable (JVMS 4.7.13). A few lines here, rather
 * than a copy of ASM, keep the published POM free of a new dependency.
 */
internal object LocalVariableNames {

    private val byClass = object : ClassValue<Map<String, Map<Int, String>>>() {
        override fun computeValue(type: Class<*>): Map<String, Map<Int, String>> =
            runCatching { read(type) }.getOrDefault(emptyMap())
    }

    /** The receiver takes slot 0 of an instance method, and a long or a double takes two (JVMS 2.6.1). */
    fun of(method: Method): List<String>? {
        val locals = byClass.get(method.declaringClass)[method.name + descriptorOf(method)].orEmpty()
        var slot = if (Modifier.isStatic(method.modifiers)) 0 else 1
        return method.parameterTypes.map { type ->
            val name = locals[slot] ?: return null
            slot += if (type == Long::class.javaPrimitiveType || type == Double::class.javaPrimitiveType) 2 else 1
            name
        }
    }

    private fun descriptorOf(method: Method): String =
        MethodType.methodType(method.returnType, method.parameterTypes).toMethodDescriptorString()

    /** Each method's local variable names by slot, keyed by name and descriptor. The layout is JVMS 4.1's. */
    private fun read(type: Class<*>): Map<String, Map<Int, String>> {
        val bytes = type.getResourceAsStream("/${type.name.replace('.', '/')}.class")!!.use { it.readBytes() }
        val input = DataInputStream(bytes.inputStream())
        input.skipBytes(8)
        val strings = readConstantPool(input)
        input.skipBytes(6)
        input.skipBytes(2 * input.readUnsignedShort())
        repeat(input.readUnsignedShort()) {
            input.skipBytes(6)
            skipAttributes(input)
        }
        return buildMap {
            repeat(input.readUnsignedShort()) {
                input.skipBytes(2)
                val key = strings[input.readUnsignedShort()] + strings[input.readUnsignedShort()]
                put(key, readLocals(input, strings))
            }
        }
    }

    private fun readConstantPool(input: DataInputStream): Array<String> {
        val strings = Array(input.readUnsignedShort()) { "" }
        var index = 1
        while (index < strings.size) {
            val tag = input.readUnsignedByte()
            if (tag == UTF8) {
                strings[index++] = input.readUTF()
            } else {
                val size = CONSTANT_SIZES.getValue(tag)
                input.skipBytes(size)
                // Long and double constants, the only eight-byte ones, take two entries (JVMS 4.4.5).
                index += if (size == 8) 2 else 1
            }
        }
        return strings
    }

    /** A local variable never takes a parameter's slot, so a parameter's slot holds only its name. */
    private fun readLocals(input: DataInputStream, strings: Array<String>): Map<Int, String> {
        val locals = mutableMapOf<Int, String>()
        forEachAttribute(input, strings, "Code") {
            input.skipBytes(4)
            input.skipBytes(input.readInt())
            input.skipBytes(8 * input.readUnsignedShort())
            forEachAttribute(input, strings, "LocalVariableTable") {
                repeat(input.readUnsignedShort()) {
                    input.skipBytes(4)
                    val name = strings[input.readUnsignedShort()]
                    input.skipBytes(2)
                    locals[input.readUnsignedShort()] = name
                }
            }
        }
        return locals
    }

    private fun forEachAttribute(input: DataInputStream, strings: Array<String>, name: String, body: () -> Unit) {
        repeat(input.readUnsignedShort()) {
            val attribute = strings[input.readUnsignedShort()]
            val length = input.readInt()
            if (attribute == name) body() else input.skipBytes(length)
        }
    }

    private fun skipAttributes(input: DataInputStream) {
        repeat(input.readUnsignedShort()) {
            input.skipBytes(2)
            input.skipBytes(input.readInt())
        }
    }

    private const val UTF8 = 1

    private val CONSTANT_SIZES = mapOf(
        3 to 4, 4 to 4, 5 to 8, 6 to 8, 7 to 2, 8 to 2, 9 to 4, 10 to 4, 11 to 4, 12 to 4,
        15 to 3, 16 to 2, 17 to 4, 18 to 4, 19 to 2, 20 to 2
    )
}
