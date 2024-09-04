package com.wesleyhome.test.jupiter.java.provider;

import com.wesleyhome.test.jupiter.provider.EnumParameterDataProvider;
import com.wesleyhome.test.jupiter.provider.TestParameter;
import java.util.List;

public class EnumParameterDataProviderTest extends ParameterDataProviderTest<EnumParameterDataProvider, Enum<?>> {

    @Override
    protected Class<Enum<?>> initializeParameterType() {
        return null;
    }

    @Override
    protected TestParameter createTestParameter(boolean isNullable) {
        return new TestParameter("testCreateParameterOptionsData", kotlin(StateValueJava.class), isNullable, List.of());
    }

    @Override
    protected TestParameter createFalseProvidesForTestParameter() {
        return new TestParameter("testCreateParameterOptionsData", kotlin(Integer.class), true, List.of());
    }

}

enum StateValueJava {
    PENDING_JAVA,
    STARTED_JAVA,
    DONE_JAVA
}
