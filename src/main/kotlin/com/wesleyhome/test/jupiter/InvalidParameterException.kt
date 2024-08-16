package com.wesleyhome.test.jupiter

import com.wesleyhome.test.jupiter.provider.TestParameter

class InvalidParameterException(testParameter: TestParameter) :
    RuntimeException("Unable to find a suitable data provider for parameter [${testParameter.name}] with type '${testParameter.type.qualifiedName}'")
