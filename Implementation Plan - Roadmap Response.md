# Implementation Plan: Roadmap Response

## Context

This document translates the feedback in `Project Feedback & Roadmap.md` into an execution plan for `junit-jupiter-params-generated`.

## Already Addressed

- Maven dependency scope in docs updated to `test` for runtime extension.
- Cartesian product behavior documented explicitly.
- Combinatorial warning added (state-space growth).
- Random generation made deterministic by default.
- Compatibility matrix added to README.
- `@CsvSource` vs generated comparison demonstrates combinatorial value.

## Current Progress (Session Snapshot)

### Completed

- Roadmap structure normalized (removed `1A/1B/1C` naming confusion).
- Phase priorities clarified:
  - Phase 1 focused on "I don't care" tuple filtering.
  - unique-key dedupe moved to optional backlog.
- `@ExpectedResult` rule clarified in plan:
  - must be the last test parameter.
- Documentation versioning infrastructure drafted/implemented in repo:
  - latest docs lane,
  - versioned docs lane,
  - `versions.json`,
  - Dokka version selector asset.

### In Progress

- Release-stack migration to match `smithy-jvm` style:
  - moved to `com.vanniktech.maven.publish`,
  - added aggregate release publish task (`publishProjectToMavenCentral`),
  - release workflow updated to call aggregate task.
- SNAPSHOT publishing path:
  - added aggregate snapshot task (`publishProjectSnapshotsToMavenCentral`),
  - main workflow updated to publish snapshots only when `version` is `*-SNAPSHOT`.

### Pending Validation / Next Step

- Validate end-to-end release workflow behavior in GitHub Actions (manual dry run).
- Confirm snapshot publishing succeeds on `main` with Central credentials.
- Clean up any remaining workflow/docs wording drift after first successful CI run.

## Guiding Principles

- Keep default behavior deterministic and predictable.
- Preserve Java and Kotlin interop.
- Catch configuration errors at compile time where possible.
- Ensure filtered/pruned tuples never become JUnit invocation contexts.
- Prefer additive, backward-compatible changes.

## Runtime Tuple Execution Order (Target)

1. Generate candidate tuples.
2. Apply filter transform (if configured).
3. Apply unique-key dedupe transform (if configured).
4. Apply sampling transform (if configured).
5. Resolve/inject expected result (if configured).
6. Build invocation contexts and display names.

This is execution order, not delivery-phase order.

## Phase 1: Tuple Filter ("I Don't Care" Scenarios)

### Goal

Allow developers to skip unnecessary Cartesian tuples when one parameter value makes others irrelevant.

### Proposed API

- Convention-first filter method:
  - For test method `testFoo(a, b, c)`, default is `filter_testFoo(a, b, c): Boolean`.
- Optional explicit override:
  - `@GeneratedParametersTest(filterMethod = "myFilter")`.
- Signature requirement:
  - same parameter count/order as test method.
  - return type must be `Boolean`/`boolean`.

### Resolution and Precedence

- If `filterMethod` is explicitly set, use only that method.
- Else if `filter_<testMethodName>` exists, use convention method.
- Else no filter is applied.

### Compile-Time Validation (KSP)

- Explicit filter:
  - method exists,
  - boolean return type,
  - compatible parameter count/order/types,
  - no ambiguous overload.
- Convention filter:
  - validate only if present.
  - if absent: silent no-filter fallback.
- No fuzzy matching required (optional warning-only near-match suggestions may be added later).

### Engine Behavior

- Apply filter before invocation-context creation.
- Filtered tuples are fully removed and never shown as tests by JUnit.

### Tests

- "I don't care" case:
  - if `A == 1`, keep one representative `B`;
  - if `A != 1`, keep all `B`.
- Convention and explicit filter paths.
- Invalid signatures fail at compile time.
- No filter present preserves baseline Cartesian behavior.
- Java and Kotlin coverage.

### Acceptance Criteria

- Users can encode short-circuit parameter logic declaratively.
- Misconfigurations are compile-time failures with actionable diagnostics.
- JUnit only sees retained tuples.

## Phase 2: IDE/JUnit Reproducibility and Display Names

### Goal

Ensure generated tuples can be rerun reliably from IntelliJ/JUnit and names are readable/stable.

### Scope

- No custom `runIndex` API in this phase.
- Rely on native JUnit/IDE invocation selection (for example, unique-id based reruns).

### Reproducibility Work

- Keep invocation ordering deterministic for post-transform tuples.
- Keep invocation identity/display output stable for deterministic inputs.
- Optional debug logging:
  - invocation number,
  - resolved arguments,
  - effective display name.

### Display Name Template Support

- Extend `@GeneratedParametersTest` with optional `name` template.
- Proposed placeholders:
  - `{index}`,
  - `{0}`, `{1}`, ...,
  - `{arguments}`.

### Tests

- Stable invocation ordering across reruns.
- Filtered tuples absent from invocation sequence.
- Template rendering for index/arguments/null values.
- Invalid placeholders follow one documented behavior (literal pass-through or fail-fast).

### Acceptance Criteria

- IntelliJ/JUnit reruns target the same tuple for deterministic inputs.
- Users can define readable, stable invocation names.

## Phase 3: Expected Result Oracle Injection

### Goal

Allow one expected parameter to be computed from generated input parameters.

### Proposed API

- Annotation: `@ExpectedResult(method = "calculateExpected")`.
- Exactly one parameter may have `@ExpectedResult`.
- `@ExpectedResult` must be on the last test parameter.

### Compile-Time Validation (KSP)

- Fail if `@ExpectedResult` is not on last parameter.
- Fail if multiple parameters use `@ExpectedResult`.
- Validate oracle method signature and return-type compatibility.

### Engine Behavior

- Resolve input tuple values first.
- Invoke oracle method.
- Inject oracle result into the last (`@ExpectedResult`) parameter.

### Tests

- Java and Kotlin happy paths.
- Wrong oracle signature/return type failures.
- Primitive/boxed/nullability edge cases.
- Compile-time failure when annotation is not last.

### Acceptance Criteria

- Expected-result parameter injection is deterministic and type-correct.
- Placement rule is enforced at compile time.

## Phase 4: Combination Strategies

### Goal

Support alternatives to full Cartesian product for large domains.

### Strategy Sequence

1. Sampled strategy.
2. Pairwise strategy.

### Proposed API

- Strategy config on `@GeneratedParametersTest`:
  - `CARTESIAN` (default),
  - `SAMPLED`,
  - `PAIRWISE` (later).

### Acceptance Criteria

- Cartesian stays default/backward compatible.
- Sampled caps count deterministically.
- Pairwise provides pair-coverage guarantees with tests.

## Phase 5: Lazy Combination Generation

### Goal

Move from eager materialization toward lazy tuple generation to reduce memory pressure.

### Current State

- Per-parameter option lists are materialized eagerly.
- Cartesian traversal iterates over prebuilt lists.

### Target State

- Tuple pipeline runs on lazy `Sequence` streams.
- Tuple generation is on-demand.
- Filter/sampling/dedupe/expected-result transforms remain deterministic under lazy flow.

### Tests

- Large theoretical spaces do not force full eager materialization.
- Single-invocation reruns do minimal work.
- Existing correctness tests still pass.

### Acceptance Criteria

- Memory behavior improves for large spaces without breaking determinism.

## Cross-Cutting Engineering Track: Internal Tuple Pipeline

This is internal implementation support, not a public roadmap phase.

### Problem

Feature logic can fragment across generator/extension classes without a common pipeline model.

### Proposed Internal Types (Non-Public)

- `TupleCandidate(values: List<Any?>, index: Long)`
- `TupleTransform { apply(input: Sequence<TupleCandidate>): Sequence<TupleCandidate> }`
- Planned transforms in execution order:
  - `FilterTransform` (Phase 1)
  - `UniqueKeyTransform` (optional backlog item)
  - `SamplingTransform` (Phase 4)
  - `ExpectedResultTransform` (Phase 3, final enrichment before invocation-context creation)

### Migration Strategy

1. Introduce pipeline abstractions with no behavior change.
2. Wrap current Cartesian output in zero-transform pipeline.
3. Add transforms as phases land.

### Internal Acceptance Criteria

- No behavior change when no new features are enabled.
- Transform order is deterministic and test-covered.
- Existing tests continue to pass.

## Optional Backlog: Unique-Key Deduplication

### Goal

Prune conceptually equivalent tuples by stable key.

### Status

Optional and demand-driven. Implement only if concrete use cases emerge after Phase 1.

## Risks and Mitigations

- Reflection complexity:
  - mitigate with strict KSP validation and clear compile-time errors.
- Feature interaction complexity:
  - mitigate with single pipeline execution model and ordering tests.
- Performance regressions:
  - mitigate via lazy-generation phase and deterministic benchmark tests.

## Suggested Delivery Sequence

1. Phase 1 (tuple filter).
2. Phase 2 (IDE/JUnit reproducibility + display names).
3. Phase 3 (expected-result injection).
4. Phase 4.1 (sampled strategy).
5. Phase 4.2 (pairwise strategy).
6. Phase 5 (lazy generation).
7. Optional backlog: unique-key dedupe.

Cross-cutting pipeline refactor work can be done incrementally during Phases 1-5.

## Definition of Done

- Public APIs documented with Java and Kotlin examples.
- New behavior covered by automated tests in `extension`.
- Backward compatibility maintained for existing usage.
- Error cases provide actionable diagnostics.
