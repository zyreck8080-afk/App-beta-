## 2026-07-01 - Avoid Calendar allocations in loops
**Learning:** Re-instantiating `java.util.Calendar` inside mapping/grouping loops (especially in Jetpack Compose UI components doing data transformation on-the-fly) creates significant memory pressure and CPU overhead due to its expensive initialization process.
**Action:** When grouping or filtering by date, reuse a single `Calendar` instance created outside the loop, or migrate to `java.time` APIs if possible.
