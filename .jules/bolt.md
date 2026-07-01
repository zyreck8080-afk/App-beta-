## 2024-06-27 - Unmemoized O(N) calculations in Compose
**Learning:** Performing list iterations (like `sumOf`) directly inside a Composable without `remember` causes those expensive O(N) operations to run on the main thread during every single recomposition (e.g., during scroll or animation).
**Action:** Always memoize derived state or expensive calculations in Compose using `remember(keys)` or handle them in the ViewModel to prevent main thread blocking and ensure smooth 60fps UI.
