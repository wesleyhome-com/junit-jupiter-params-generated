package com.wesleyhome.test.jupiter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParametersGeneratorTest {

  @Mock
  private ExtensionContext context;

  @Test
  void provideArguments_classFiltered() throws Exception {
    assertTestParameterSourceMethod((TestEnum.values().length - 1) * 4,
      "testMethodFiltered",
      TestEnum.class,
      boolean.class,
      Boolean.class
    );
  }

  @Test
  void provideArguments_enum_class() throws Exception {
    assertTestParameterSourceMethod(TestEnum.values().length, "testEnumMethod", TestEnum.class);
  }

  @Test
  void provideArguments_BigBoolean() throws Exception {
    assertTestParameterSourceMethod(2, "testBigBooleanMethod", Boolean.class);
  }

  @Test
  void provideArguments_LittleBoolean() throws Exception {
    assertTestParameterSourceMethod(2, "testLittleBooleanMethod", boolean.class);
  }

  @Test
  void provideArguments_int() throws Exception {
    assertTestParameterSourceMethod(5, "testIntMethod", int.class);
  }

  @Test
  void provideArguments_integer() throws Exception {
    assertTestParameterSourceMethod(5, "testIntegerMethod", Integer.class);
  }

  @Test
  void provideArguments_little_double() throws Exception {
    assertTestParameterSourceMethod(7, "testLittleDoubleMethod", double.class);
  }

  @Test
  void provideArguments_big_double() throws Exception {
    assertTestParameterSourceMethod(7, "testBigDoubleMethod", Double.class);
  }

  @Test
  void provideArguments_string() throws Exception {
    assertTestParameterSourceMethod(10, "testStringMethod", String.class);
  }

  @Test
  void provideArguments_testObject() throws Exception {
    assertTestParameterSourceMethod(4, "testTestObjectMethod", TestObject.class);
  }

  private void assertTestParameterSourceMethod(int expectedSize, String methodName, Class<?>... parameterClass)
    throws NoSuchMethodException {
    final var testClass = ParametersGeneratorTest.class;
    when(context.getRequiredTestMethod()).thenReturn(testClass.getDeclaredMethod(methodName, parameterClass));
    when(context.getRequiredTestClass()).thenAnswer(invocation -> testClass);
    final var arguments = ParametersGenerator.Companion.create(context);
    assertThat(arguments).hasSize(expectedSize);
  }

  private boolean testMethodFiltered_filter(TestEnum testEnum, boolean one, Boolean two) {
    return TestEnum.BAD != testEnum;
  }

  @ParametersSource
  private void testMethodFiltered(TestEnum testEnum, boolean one, Boolean two) {
  }

  @ParametersSource
  private void testEnumMethod(TestEnum testEnum) {
  }

  @ParametersSource
  private void testBigBooleanMethod(Boolean testBool) {
  }

  @ParametersSource
  private void testLittleBooleanMethod(boolean testBool) {
  }

  @ParametersSource
  private void testIntMethod(int testValue) {
  }

  @ParametersSource
  private void testIntegerMethod(Integer testValue) {
  }

  @ParametersSource
  private void testLittleDoubleMethod(double testValue) {
  }

  @ParametersSource
  private void testBigDoubleMethod(Double testValue) {
  }

  @ParametersSource
  private void testStringMethod(String testValue) {
  }

  @ParametersSource
  private void testTestObjectMethod(TestObject testValue) {
  }

  private List<Integer> intSource() {
    return List.of(1, 2, 3, 4, 5);
  }

  private List<Double> doubleSource() {
    return List.of(1d, 2d, 3d, 4d, 5d, 6d, 7d);
  }

  private List<String> stringSource() {
    return List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0).stream().map(Objects::toString).collect(Collectors.toList());
  }

  private List<TestObject> testObjectSource() {
    return List.of("one", "two", "three", "four")
      .stream()
      .map(TestObject::new)
      .collect(Collectors.toList());
  }

}

