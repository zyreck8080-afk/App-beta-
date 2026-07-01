## 2024-07-01 - DashboardScreen Recalculations
**Learning:** `DashboardScreen` calculates `totalInvestment` and `totalProfit` on every recomposition. Because `products` and `sales` are state collections, using standard `sumOf` inside the composable triggers an O(n) calculation every time the UI updates (even when the data hasn't changed).
**Action:** Use `remember` with keys `products` and `sales` to memoize the sums and prevent unnecessary re-computations during recomposition.
