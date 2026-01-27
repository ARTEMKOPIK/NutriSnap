## 2024-01-27 - Optimized Database and UI State Flow
**Learning:** Room aggregation with SQL `SUM()` and `COALESCE()` is significantly more efficient than summing collections in Kotlin, especially for growing datasets. Additionally, static "start of day" calculations in ViewModels create stale data bugs if the app stays open past midnight; a reactive `startOfDayFlow` ensures correct date-based filtering.
**Action:** Always prefer SQL-side aggregation for summaries and use reactive flows for time-sensitive query parameters to ensure both performance and correctness.

## 2024-01-28 - Reactive Flow and UI Recomposition Optimization
**Learning:** Even with reactive flows, `flatMapLatest` can trigger redundant work if the upstream emits same values (e.g., hourly "start of day" checks). `distinctUntilChanged()` is vital here. Also, collecting state at the top-level Composable is a major anti-pattern; moving `collectAsState` to sub-components significantly reduces recomposition overhead for static parts of the screen.
**Action:** Always use `distinctUntilChanged()` on time-based triggers and localize `collectAsState` to the smallest possible Composable scope.
