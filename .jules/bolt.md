## 2024-07-01 - Optimizing Redundant O(N) calculations in Compose
**Learning:** O(N) operations in Compose UI layer, especially inside the core recomposition loop without `remember`, can trigger significant frame drops by forcing recalculation on every layout pass.
**Action:** Always wrap heavy list traversals (like `.sumOf` or mapping operations over large collections) inside `remember(keys...)` blocks so that they only recalculate when their direct inputs change.
