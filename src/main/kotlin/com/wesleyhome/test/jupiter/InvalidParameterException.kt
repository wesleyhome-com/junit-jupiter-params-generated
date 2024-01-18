package com.wesleyhome.test.jupiter

import kotlin.reflect.KClass

class InvalidParameterException(parameterClass: KClass<*>) :
    RuntimeException("Unable to find a suitable data provider for '${parameterClass.qualifiedName}' type")
