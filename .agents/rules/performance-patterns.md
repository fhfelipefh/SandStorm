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

## 6. Server Inventory Mutation & Slot Synchronization
- Whenever modifying the player's inventory directly on the server (e.g., `player.getInventory().add(stack)` in quest claims, loot deliveries, or machine interactions):
  - Always call `player.containerMenu.broadcastChanges()` and `player.inventoryMenu.broadcastChanges()` immediately after.
  - Without this, the client HUD, hotbar, and open screens will not receive slot update packets until another container event triggers.

## 7. Centralized Notification & Sound Deduplication
- Do not emit quest, directive, or milestone notifications from arbitrary tick handlers or subsystems.
- All quest-ready notifications must be routed exclusively through `QuestRewardHandler.checkPlayerNotifications()`.
- Maintain active deduplication sets (`notified`) to ensure notifications are never dispatched across multiple channels (e.g., Chat vs. Action Bar) for the same event.

## 8. Custom GUI Interactive Feedback Invariant
- In custom screens and widgets, mouse click handlers on interactive buttons must never fail silently.
- If a click occurs within a button hitbox but requirements/conditions are not met, always provide immediate user feedback (e.g., explanatory status tooltip and low-pitch click sound).

## 9. Zero Unused Imports Invariant
- The automated architecture test `NoUnusedImportsArchitectureTest` scans all Java files in `src/main`, `src/client`, and `src/test`.
- Whenever refactoring code or removing method invocations, immediately prune obsolete import statements to keep the build green.

## 10. Local Runtime & Mod Jar Parity
- External Minecraft instances running via `play.bat` or the official launcher load `.jar` files from `%APPDATA%\.minecraft\mods\`.
- After modifying network payloads, screens, or server logic, ensure a fresh jar is built via `./gradlew build -x test` and copied to the mods folder before launching the game.

## 11. Unit Testing Block Entities with Frozen Registries
- Calling `Bootstrap.bootStrap()` freezes `BuiltInRegistries.BLOCK`. Loading `SandStormBlocks` statically in unit tests throws `IllegalStateException: This registry can't create intrusive holders`.
- All custom `BlockEntity` classes must provide an overloaded constructor accepting `(BlockEntityType<?> type, BlockPos pos, BlockState state)`.
- Unit tests must instantiate the block entity using dummy types (e.g. `BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState()`) rather than referencing `SandStormBlocks.<TYPE>`.

## 12. Item Data Component Binding in Unit Tests
- Instantiating `new ItemStack(...)` in unit tests after `Bootstrap.bootStrap()` throws `NullPointerException: Components not bound yet` in Minecraft 1.21.4 / 26.3.
- In `@BeforeAll static void setup()`, iterate through `BuiltInRegistries.ITEM` and bind empty components if unbound:
  ```java
  for (Item item : BuiltInRegistries.ITEM) {
      if (!item.builtInRegistryHolder().areComponentsBound()) {
          item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
      }
  }
  ```

## 13. Transparent & Open-Mesh Block Occlusion Invariant
- Custom blocks with non-solid, cutout, open-mesh, or transparent geometry (e.g. machinery, filters, pipes, terraformers) must declare `.noOcclusion()` in `BlockBehaviour.Properties`.
- Missing `.noOcclusion()` causes neighboring block faces (floors/walls) to be culled by the rendering engine, creating X-ray artifacts through the terrain. Verified by `TransparentBlockOcclusionArchitectureTest`.

## 14. Language & Documentation Convention (pt-BR)
- All implementation plans (`implementation_plan.md`), walkthroughs (`walkthrough.md`), and technical design proposals for this repository must be authored in Brazilian Portuguese (`pt-BR`).
