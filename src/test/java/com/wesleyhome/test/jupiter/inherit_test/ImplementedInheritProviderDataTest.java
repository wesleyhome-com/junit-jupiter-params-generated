package com.wesleyhome.test.jupiter.inherit_test;

public class ImplementedInheritProviderDataTest extends InheritProviderDataTest {

  @Override
  double[] data() {
    return new double[] {2.3, 1.2, 5, 6.7};
  }
}
