# JUnit Jupiter Generate Parameters

Welcome to the junit-jupiter-params-generated wiki!

## Examples
```java
import com.wesleyhome.test.jupiter.annotations.ParametersSource;
import com.wesleyhome.test.jupiter.annotations.IntRangeSource;
import com.wesleyhome.test.jupiter.annotations.IntSource;

public class TestClass {

  @ParameterizedTest
  @ParametersSource
  void testMethod(@IntRangeSource(min=10, max=20) int firstParam, @IntSource(values = [10,15,25,1000]) int secondParam ) {

  }

}
```

```kotlin

import com.wesleyhome.test.jupiter.annotations.ParametersSource

class TestClass {

  @ParamterizedTest
  @ParameterSource
  fun testMethod(scenario: Scenario, enabled: Boolean) {

  }
}

enum Scenario {
  ONE, TWO, THREE
}

```
You can also look at some [test cases](https://github.com/wesleyhome-com/junit-jupiter-params-generated/blob/master/src/test/kotlin/com/wesleyhome/test/jupiter/kotlin/AnnotationsTest.kt) for more examples.
