## 2024-05-24 - Expensive Calendar instantiation in loops
**Learning:** Re-instantiating `java.util.Calendar` inside mapping functions like `groupBy` (e.g., in `FinancialChart.kt`) creates significant garbage collection pressure on Android, as Calendar carries heavy timezone and locale state. This is a common Android-specific performance anti-pattern that leads to GC pauses and UI stutters. Reusing a mutable `Calendar` instance inside a `groupBy` lambda works in Kotlin since it evaluates sequentially.
**Action:** Always reuse a single `Calendar` instance or migrate to `java.time` APIs when aggregating large lists of timestamps.

## 2024-05-24 - Do not commit test scripts and generated binaries
**Learning:** Avoid leaving intermediate testing files (`.class`, `.kt`, `.java` files created in the root for ad-hoc tests) and unrequested Gradle wrapper modifications when working on optimizations.
**Action:** Always clean up temporary test files or scripts created during the analysis phase using `git clean` or `git restore` before submitting code for review.
