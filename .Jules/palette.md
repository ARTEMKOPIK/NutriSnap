## 2025-05-14 - [Feedback & Loading States]
**Learning:** In AI-driven apps where analysis takes time, silent failures or silent successes (just updating a number) lead to poor UX. Users need explicit confirmation of results and visual indication that the app is busy.
**Action:** Always implement a `Loading` state that disables interactive elements and a `Snackbar` or similar feedback mechanism for the final result (success or error).

## 2025-05-14 - [Groundedness in Planning]
**Learning:** Detailed code review of the full file is essential before planning, as truncations in the agent's view can lead to incorrect assumptions about line numbers and existing code structure.
**Action:** Use multi-step reading (e.g., `sed` or `grep`) to verify the exact context of code changes, especially for large UI files.

## 2025-05-14 - [Balanced UI Centering]
**Learning:** In horizontally structured navigation bars (like onboarding footers), dynamic button presence (e.g., "Skip" appearing only on early pages) or varying button widths can cause the center element (like page indicators) to shift visually, creating a jarring "jank" feeling.
**Action:** Use equal-weight containers (`Box(Modifier.weight(1f))`) for the left and right slots to ensure the center element remains perfectly anchored at the screen's center regardless of the content in the side slots.

## 2025-05-14 - [Input Efficiency & Clarity]
**Learning:** Mobile users expect efficient input workflows. Missing keyboard "Send" actions or a way to quickly clear long text entries creates friction in food-logging apps where speed is key.
**Action:** Always implement `KeyboardOptions(imeAction = ImeAction.Send)` with corresponding `KeyboardActions` and a conditional "Clear" button for text inputs to improve efficiency and reduce frustration.

## 2025-05-14 - [Layout Stability & Counting Delight]
**Learning:** Layout jank caused by appearing/disappearing loaders creates a "cheap" feeling. Instant number updates for stats also feel jarring and "mechanical".
**Action:** Always wrap loaders in fixed-height containers to preserve vertical space and use animate*AsState for numerical updates to provide a smooth, premium "counting" effect.

## 2025-05-14 - [Camera Screen Escape Hatch]
**Learning:** Full-screen camera overlays can trap users if they don't provide a visible exit button or don't handle the system back gesture. This is especially frustrating if the user opened the camera by mistake.
**Action:** Always include a `BackHandler` and a visible `IconButton` (e.g., Close/X) in full-screen camera components to ensure an easy "escape hatch".
