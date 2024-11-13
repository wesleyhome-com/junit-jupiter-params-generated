# junit-jupiter-params-generated
This library is an extension to JUnit Jupiter's parameterized test functionality. 
This library will generate all parameter permutations for a parameterized test by inspecting the type, 
and including annotations for certain types to tell the extension the acceptable values. 

Below is a basic usage of this library. For more annotations and detailed usage of the annotations,
you can go [here](annotations/index.html)

## Examples
```java
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest;
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource;
import com.wesleyhome.test.jupiter.annotations.number.IntSource;

public class TestClass { 
    @GeneratedParametersTest
    void testMethod(
        @IntRangeSource(min=10, max=20) 
        int firstParam, 
        @IntSource(values = [10,15,25,1000]) 
        int secondParam 
    ) {
        
    }
}
```

```kotlin

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest

class TestClass {
    @GeneratedParametersTest
    fun testMethod(scenario: Scenario, enabled: Boolean) {
        
    }
}

enum Scenario {
  ONE, TWO, THREE
}

```
