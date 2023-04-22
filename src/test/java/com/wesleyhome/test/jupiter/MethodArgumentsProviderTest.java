package com.wesleyhome.test.jupiter;

import org.junit.jupiter.params.ParameterizedTest;

class MethodArgumentsProviderTest {

  @ParameterizedTest
  @ParametersSource
  void testParameters(Boolean bool) {
    System.out.println(bool);
  }

  @ParameterizedTest
  @ParametersSource
  void testParameters_primitive(boolean bool) {
    System.out.println(bool);
  }

  @ParameterizedTest
  @ParametersSource
  void testParameters_enum(TestEnum testEnum) {
    System.out.println(testEnum);
  }

  @ParameterizedTest
  @ParametersSource
  void testParameters_lots(TestEnum testEnum, Boolean wrap, boolean bool) {
    System.out.printf("%s - %s - %s%n", testEnum, wrap, bool);
  }

  @ParameterizedTest
  @ParametersSource({IntegerProvider.class})
  void testParameters_provider(int val) {
    System.out.printf("%d%n", val);
  }

  @ParameterizedTest
  @ParametersSource({IntegerProvider.class})
  void testParameters_providers(int val, double val2) {
    System.out.printf("%d - %f%n", val, val2);
  }

  @SuppressWarnings("WeakerAccess")
  public double[] doubles() {
    return new double[] {2.0, 3.2, 5.6};
  }
}
