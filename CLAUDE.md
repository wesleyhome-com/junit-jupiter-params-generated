# CLAUDE.md

Read [DEVELOPERS.md](DEVELOPERS.md) for the build, tests, release and docs publishing. The rules below
apply to every change, in every session.

## Every change

- Open an issue, then a pull request that closes it (`Closes #N`). Branch before committing: `main`
  accepts only pull requests, and they must pass the `build` and `changelog` checks.
- Add a `CHANGELOG.md` entry under `[Unreleased]` and a label, as "Changelog and Labels" in
  DEVELOPERS.md describes. The `changelog` check fails the pull request otherwise.
- Before calling a change non-breaking, look for behavior changes as well as removed APIs: published
  POM scopes and versions, defaults, display names, generated values and invocation counts. Those go
  under **Upgrade notes**, with the `breaking` label.
- No AI attribution anywhere on GitHub: no "Generated with" lines in pull requests or issues, and no
  `Co-Authored-By` trailers for an AI in commits.

## Releases

Issues are grouped into release milestones.

- **Patch:** fixes only. Tests that already run as written keep the same names, values and
  invocation counts.
- **Minor:** new features and opt-in switches.
- **Major:** anything that changes names, generated values or invocation counts for tests that pass
  today.

Minimum versions (Java 17, Kotlin 2.4, JUnit 6.0.3) don't rise within a major.

## Code

Code should explain itself. Comment only what reading the code can't tell you: why a choice was
made, an outside constraint, a non-obvious consequence. A handful of comments per file at most.
