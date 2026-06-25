## 2024-06-25 - Jetpack Compose Recomposition Bottleneck
**Learning:** In `DashboardScreen.kt`, computing aggregates via `sumOf` directly inside the Composable function body triggers O(N) recalculations on *every single recomposition* (e.g. state changes not even related to the data like scrolling or toggling UI elements).
**Action:** Always wrap expensive list iterations or object allocations inside a `remember(key1, key2) { ... }` block in Jetpack Compose to memoize the result based on data dependencies.
