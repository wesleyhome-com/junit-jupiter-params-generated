//[junit-jupiter-params-generated](../../index.md)/[com.wesleyhome.test.jupiter.annotations](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [DataProviderRegistry](-data-provider-registry/index.md) | [jvm]<br>object [DataProviderRegistry](-data-provider-registry/index.md) |
| [DefaultProvider](-default-provider/index.md) | [jvm]<br>@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.CLASS](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-c-l-a-s-s/index.html)])<br>@API(status = API.Status.STABLE, since = &quot;3.0&quot;)<br>annotation class [DefaultProvider](-default-provider/index.md)(val priority: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = Int.MAX_VALUE) |
| [GeneratedParametersTest](-generated-parameters-test/index.md) | [jvm]<br>@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.FUNCTION](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-f-u-n-c-t-i-o-n/index.html)])<br>@API(status = API.Status.STABLE, since = &quot;3.0&quot;)<br>@TestTemplate<br>@ExtendWith(value = [[GeneratedParametersTestExtension::class](-generated-parameters-test-extension/index.md)])<br>annotation class [GeneratedParametersTest](-generated-parameters-test/index.md) |
| [GeneratedParametersTestExtension](-generated-parameters-test-extension/index.md) | [jvm]<br>class [GeneratedParametersTestExtension](-generated-parameters-test-extension/index.md) : TestTemplateInvocationContextProvider |
| [GeneratedParametersTestInvocationContext](-generated-parameters-test-invocation-context/index.md) | [jvm]<br>class [GeneratedParametersTestInvocationContext](-generated-parameters-test-invocation-context/index.md)(arguments: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt;) : TestTemplateInvocationContext |
| [GeneratedParametersTestMethodContext](-generated-parameters-test-method-context/index.md) | [jvm]<br>class [GeneratedParametersTestMethodContext](-generated-parameters-test-method-context/index.md)(context: ExtensionContext) |
