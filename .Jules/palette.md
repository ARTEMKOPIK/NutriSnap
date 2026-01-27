## 2025-05-14 - [Feedback & Loading States]
**Learning:** In AI-driven apps where analysis takes time, silent failures or silent successes (just updating a number) lead to poor UX. Users need explicit confirmation of results and visual indication that the app is busy.
**Action:** Always implement a `Loading` state that disables interactive elements and a `Snackbar` or similar feedback mechanism for the final result (success or error).

## 2025-05-14 - [Groundedness in Planning]
**Learning:** Detailed code review of the full file is essential before planning, as truncations in the agent's view can lead to incorrect assumptions about line numbers and existing code structure.
**Action:** Use multi-step reading (e.g., `sed` or `grep`) to verify the exact context of code changes, especially for large UI files.
