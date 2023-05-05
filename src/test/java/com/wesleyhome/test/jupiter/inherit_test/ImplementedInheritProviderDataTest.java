package com.wesleyhome.test.jupiter.inherit_test;

import java.util.List;

public class ImplementedInheritProviderDataTest extends InheritProviderDataTest {

  @Override
  List<Double> data() {
    return List.of(2.3, 1.2, 5.0, 6.7);
  }
}
