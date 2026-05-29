# **LLM-Ready Technical Specification: junit-jupiter-params-generated**

**Target Audience:** AI Development Agents / Senior Software Engineers

**Project Context:** Enhancing a JUnit 5 Parameterized Testing Extension with Combinatorial Logic and Constraints.

## **1\. Project Identity & Philosophical Foundation**

junit-jupiter-params-generated is designed to solve the rigidity of standard JUnit 5 parameterized tests.

* **Core Philosophy:** Declarative testing. The developer should define the *domain of data* via annotations, and the framework should handle the *generation of scenarios*.  
* **The "Redundancy" Problem:** Our discussions identified a key pain point: the "Cartesian Explosion." Running every combination is often wasteful if certain parameters are ignored by the code under certain conditions (Short-circuiting) or if combinations are conceptually equivalent (Symmetry).

## **2\. Technical Architecture Overview**

The system is divided into three primary components that an LLM must understand before generating code:

1. **Annotation Processor (/annotation-processor):** Performs compile-time validation. It ensures that range bounds are logical (min \< max) and types match.  
2. **Extension Engine (/extension):** Implements TestTemplateInvocationContextProvider. It intercepts the test lifecycle, scans for annotations, and generates TestTemplateInvocationContext objects.  
3. **Data Providers (/provider):** The ParameterDataProvider\<T\> interface is the strategy for generating data lists for specific types.

## **3\. Detailed Feature Specifications (LLM Roadmap)**

### **Phase 1: Constraint Logic (The "Sister Function")**

**Requirement:** Implement a way to prune the Cartesian product based on developer-defined rules.

* **Prompting Context:** The LLM should look at implementing a filter attribute in @GeneratedParametersTest.  
* **Mechanism:** Reflection-based invocation of a "Sister Function" within the same test class.  
* **Signature Pattern:** (arg0, arg1, ... argN) \-\> Boolean.  
* **Logic:** Before generating a TestTemplateInvocationContext, the engine must invoke this filter. If it returns false, that specific tuple is skipped.

### **Phase 2: Equivalence & Symmetry Mapping**

**Requirement:** Prevent "conceptually redundant" tests.

* **Scenario:** Testing a sum(a, b) function where (1, 2\) and (2, 1\) are redundant.  
* **Mechanism:** Introduce a uniqueKey extractor.  
* **Implementation:** A user-provided function that transforms the argument tuple into a "Stable Key" (e.g., sorting the inputs and concatenating).  
* **Internal State:** The extension must maintain a Set\<String\> of keys already seen during the current test template generation.

### **Phase 3: The "Expected Result" Oracle (Injection Logic)**

**Requirement:** Allow the test method signature to include an expected value generated dynamically.

* **Requirement:** An @ExpectedResult(method \= "calculate") annotation.  
* **LLM Task:** Modify the extension logic to identify which parameter is the "Result." The engine must first resolve all "Input" parameters, then pass those values into the "Oracle Method" to populate the result parameter for that specific invocation.

## **4\. Combinatorial Strategies (Optimization)**

An LLM should be tasked with implementing alternatives to the default Cartesian Product:

* **Pairwise (2-wise):** Implementing an algorithm (like IPO) to find a covering array where every possible pair of parameter values is tested at least once.  
* **Sampled:** A limit or random strategy that picks ![][image1] iterations from a massive potential state space.

## **5\. Known Constraints & Maintenance Notes**

* **Maven Scope:** All installation documentation MUST specify test scope.  
* **Type Safety:** The library relies heavily on Kotlin's type system but must maintain seamless Java interop.  
* **Performance:** Data generation should ideally be lazy to avoid memory pressure when state spaces exceed ![][image2] combinations.

## **6\. Discussion History (Context for LLM)**

During the design phase, we discussed:

* **Why not CSVSource?** CSV is static and hard to maintain for many parameters. This library is generative; adding one value to a range can create dozens of new tests automatically.  
* **Short-Circuiting:** If Param A is null, Param B is often ignored. The library needs to allow the dev to signal this so the engine doesn't test every variation of B against a null A.  
* **Oracle Problem:** High-level testing requires the "answer" to be linked to the "input." We decided on a method-reference approach to keep test bodies clean.

**Instruction to LLM:** Use the above architecture and roadmap to generate a implementation plan for the "Sister Function" filter, ensuring it integrates with the existing TestTemplateInvocationContext generation logic.

[image1]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAAXCAYAAAAGAx/kAAABH0lEQVR4XmNgGAUkATk5OS15efk7QPwfCX+TlZW1BckD2ZPR5O4rKSmpoZsDB0AFlkD8E4hvA7EkTFxFRYUdyF8PtLBeXFycG1kPViAjI8MJVLwDqOmfgoKCB1SYEcgvBWEQG1k9XgA0IALq/OXGxsasUEO6QWx0tXiBoqKiOFDjdSB+D8TNQDyZZENgAKi5FeQqoOsOAQOVH12eaAA0xAsUTkB8gmyDgJo1gXgfMNBvgQxDCnTiAMhmoKZVwDAyA/GRwwqYnnTQ1WMFIEOAGtYBsTeyONDgBmhYNSCLYwVAhYpAb2wEKi5ElwOK2wDlfwPlTklLSwujy4MBUFEsUNEvkI1Q/BeowR8mD+RngcSQ5YF6dgITrRCyOaNgRAMAuiJQyHB0jy0AAAAASUVORK5CYII=>

[image2]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABsAAAAXCAYAAAD6FjQuAAAB1klEQVR4Xu2UTyhEURTGRyiiJI1p/r43M0qUqJHFZClLTbEQewspC0VZyZ8FS1lZmcWwUjZWpFmbnYWNhZSytGEpfmfmXl3Xe28kFspXX++875x7vnvvu++GQn8Vruu2pFKpOcdx8ul0moczw3u/XfcjCIfD7RiU4St8hEu5XK7ZrvsRKLNNedo5T7g1TNu6BtvTS8MduC/blEgkWnVOmW2xdaP0KBBHzbFVIPbBeXgBXygs2jUCcpPwmvyQarwBzzKZTIfklXYI8/F4vAvTEyY3YjfpUzPJw3svs1gsliR3A2e1RrNO3itwwazVQF+FR4RNdk6SUXjnZSYm8BmDnCE3oJVgWVbFKgaJj6WPJKld0TljTA11zHY9zOQbF9Ef0DNwDB7obVVj9ggbzDFVBJmppn5mVV0OC/G6HDDeF4nP+XYJs/4dfmbG/xNopqTGbDbbLZTYrP0AP7NIJNIms/yi2dfgZybwa+qn10WQGfqWV1Nldu/7bfwQZJZMJifIvciJ0xp1LWinQonN+rrQZrAUso6r3Ag0vIRrWuP09ciq3IDr7RNktjJIZu7UbmzhE02uaDig61jdMPot9ctwirhCzfav3exyMjEdx6QgV5id/8c/vo03JqiTnoCKmTwAAAAASUVORK5CYII=>