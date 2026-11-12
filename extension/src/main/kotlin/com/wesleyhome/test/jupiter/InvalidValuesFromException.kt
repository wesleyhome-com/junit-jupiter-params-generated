package com.wesleyhome.test.jupiter

class InvalidValuesFromException internal constructor(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)
