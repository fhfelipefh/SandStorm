# Performance & Architecture Patterns for SandStorm (Minecraft Fabric)

Follow these architectural and optimization patterns across the entire codebase:

## 1. Tick Handler Throttling
- Never execute raycasts (e.g., `canSeeSkyFromBelowWater`, `clip`), heavy calculations, or block iterations every tick (20 times per second per player/entity).
- Always throttle periodic checks using modulo arithmetic: `if (player.tickCount % INTERVAL == 0)`.
- Cache and reuse calculation results within the same tick instead of recalculating across multiple handlers.

## 2. Stream Short-Circuiting in Spatial Scans
- When scanning bounding boxes with `BlockPos.betweenClosedStream`:
  - Never use `forEach` with external mutable variables (such as `AtomicBoolean`) to detect a condition, as `forEach` does not cancel stream iteration.
  - Use short-circuiting terminal operators: `anyMatch(predicate)` or `filter(predicate).findFirst()`.
  - Keep scan bounding box radii as compact as possible.

## 3. Strict Client vs Common Separation
- Code in `com.fhfelipefh.sandstorm.component`, `content`, `core`, `metrics`, and `survival` must never reference `net.minecraft.client` or `com.fhfelipefh.sandstorm.client`.
- Sending messages to players must use `Player.sendSystemMessage(Component)` rather than client-only HUD calls.

## 4. World Generation Standards (Minecraft 26.3+)
- Features referenced by `placed_feature` JSONs (`"feature": "sandstorm:<name>"`) must reside in `data/<namespace>/worldgen/feature/<name>.json`.
- Placed feature definitions must reside in `data/<namespace>/worldgen/placed_feature/<name>.json`.
- Keep parity between `feature` and `configured_feature` directories for broad mod loader compatibility.

## 5. Multilingual Localization Parity
- Every translation key added to `pt_br.json` must have exact matching keys in `en_us.json` and `es_es.json`.
