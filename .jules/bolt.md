## 2024-01-27 - Optimized Database and UI State Flow
**Learning:** Room aggregation with SQL `SUM()` and `COALESCE()` is significantly more efficient than summing collections in Kotlin, especially for growing datasets. Additionally, static "start of day" calculations in ViewModels create stale data bugs if the app stays open past midnight; a reactive `startOfDayFlow` ensures correct date-based filtering.
**Action:** Always prefer SQL-side aggregation for summaries and use reactive flows for time-sensitive query parameters to ensure both performance and correctness.
