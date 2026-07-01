## 2026-07-01 - [O(N) Optimization in Grouped Mappings]
**Learning:** Redundant `sumOf` loops inside mapping functions create unnecessary O(N) traversals per group. Accumulating properties using a single `for` loop over the group achieves the same result in O(N).
**Action:** Identify `sumOf`, `maxOf`, or `minOf` chains over identical collections in a single map and refactor them into a single-pass loop.
