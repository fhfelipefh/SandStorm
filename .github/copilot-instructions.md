# SandStorm Copilot Instructions

## Build, test, and run commands

This is a Fabric/Loom Minecraft mod targeting Minecraft 26.3 (compatible with 26.2-26.3), Fabric Loader 0.19.5, Fabric API 0.160.7+26.3, and Java 25. The project uses split common/client source sets and JUnit 5 tests.

Use the Gradle wrapper from the repository root:

```text
gradlew.bat test
gradlew.bat build
gradlew.bat test --tests com.fhfelipefh.sandstorm.content.item.AssetIntegrityTest
gradlew.bat test --tests com.fhfelipefh.sandstorm.architecture.ServerDeadlockPreventionArchitectureTest
gradlew.bat runClient
```

On Unix-like environments, use the equivalent `./gradlew` commands. `test` runs the JUnit Platform suite, including architecture, registry, asset, recipe, world-generation, gameplay, and client-model tests. `build` is the required packaging and compilation check. There is no separately configured lint task; architecture tests provide much of the repository's static enforcement. For an installed Minecraft instance, `play.bat` builds with tests skipped when needed, synchronizes the current mod jar into `%APPDATA%\.minecraft\mods\`, and can launch the client.

The showcase world is launched through `run\start_showcase.bat` with the `SandStorm_Showcase` quick-play world. Before changing showcase setup, run the focused `ShowcaseWorldSetupTest`; do not clone `run\world` into the showcase save.

## Architecture

`src/main/java/com/fhfelipefh/sandstorm/core/SandStormMod.java` is the common Fabric entrypoint. It registers payload codecs, sounds, items, blocks, menus, entities, world systems, survival systems, commands, and recipe unlock handling. `src/client/java/com/fhfelipefh/sandstorm/client/SandStormClient.java` is the client entrypoint and owns menu screens, block/entity renderers, HUD, particles, key mappings, client weather effects, armor rendering, and client networking.

Common gameplay is organized by domain under `content`: `item`, `block`, `block/entity`, `entity`, `gui`, `network`, `quest`, `recipe`, `satellite`, `survival`, `world`, `command`, and `sound`. Reusable state/data components live under `component`; metrics and utility code are separate. Block entities and managers implement machines, energy/transfer behavior, inventory processing, survival systems, quests, weather, terraforming, procedural ruins, spaceship landing, and orbital systems.

Registration is centralized: `SandStormItems` owns item registration and the `SANDSTORM_TAB` creative tab; `SandStormBlocks` owns blocks, block items, and block entity types; analogous classes register entities, menus, sounds, and payloads. A feature usually spans a registered object, a common behavior/block entity, a client screen or renderer when applicable, data resources, and focused tests.

Resources are data-driven under `src/main/resources`: `assets/sandstorm` contains models, blockstates, item definitions, textures, sounds, and translations; `data/sandstorm` contains recipes, tags, loot tables, structures, and worldgen JSON. `SandStormWorldGen` attaches placed features to desert biomes, while JSON files define the configured/placed feature details. Keep referenced identifiers and resource paths synchronized with Java registrations.

## Repository-specific conventions

- Do not add comments to source code. This applies to Java and other code files and is enforced by `ZeroCommentsArchitectureTest`.
- Do not use fully qualified class names inside code. Add explicit imports at the top, and remove imports that become unused. `NoInlineImportsArchitectureTest` and `NoUnusedImportsArchitectureTest` scan common, client, and test Java sources.
- Preserve the common/client boundary. Classes outside `..client..` must not depend on `net.minecraft.client` or client packages. Components must remain independent of `content` and `core`; metrics must remain client-independent and use `*Tracker` names.
- Server chunk-load handlers must not synchronously call `level.getServer().execute()` to place blocks or generate structures. Queue work from `ServerChunkEvents.CHUNK_LOAD` and consume it from `ServerTickEvents.END_SERVER_TICK`, checking `level.isLoaded(pos)` before mutation. Do not call `saveAndJoin()`; saved-data mutations use `setDirty()` and vanilla persistence.
- Throttled server/client tick work is the norm. Expensive raycasts, scans, block iteration, and similar work must run at a modulo interval rather than every tick. Spatial scans should use short-circuiting `anyMatch` or `findFirst`, not `forEach` with mutable state.
- Direct server-side inventory mutations must immediately call both `player.containerMenu.broadcastChanges()` and `player.inventoryMenu.broadcastChanges()`.
- Route quest/directive/milestone readiness notifications through `QuestRewardHandler.checkPlayerNotifications()` and preserve deduplication sets so one event is not emitted through multiple channels.
- Interactive custom GUI buttons must provide immediate feedback when a click is valid but its requirements are not met; do not silently ignore the click.
- Transparent, cutout, open-mesh, or machine geometry blocks require `.noOcclusion()` in their block properties. Keep `ItemGuiExhibitionArchitectureTest` and `TransparentBlockOcclusionArchitectureTest` green.
- Every registered item needs the matching 1.21.4+ item definition JSON, item model JSON, and valid PNG texture. Every registered block needs blockstate, block model, block item definition, and referenced block textures. New items and blocks must be visible through `SANDSTORM_TAB`.
- Item definitions use the modern wrapper form with `model.type = "minecraft:model"` and `model.model = "sandstorm:item/<name>"`. Item models normally use `minecraft:item/generated` with a valid `layer0`. In particular, `*_rail` item models must not inherit a block model.
- Recipes under `data/sandstorm/recipe` may reference only registered `sandstorm:*` identifiers. Verify new or changed recipes with `JeiCompatibilityTest` and ensure every resulting item has its asset definition.
- Keep `en_us.json`, `pt_br.json`, and `es_es.json` translation keys in exact parity. Do not put raw `\n` or `\r` in translation strings, `Component` literals, or tooltip text. Multiline tooltips use `List<Component>` and the frame tooltip API; keep individual UI lines under 50 visible characters.
- New worldgen references must have matching files in `data/sandstorm/worldgen/feature` and `data/sandstorm/worldgen/placed_feature`; preserve configured/placed feature naming parity.
- Unit tests run against Minecraft registries that may already be frozen. Block entity tests should use constructors accepting `(BlockEntityType<?>, BlockPos, BlockState)` and dummy vanilla types/states instead of loading `SandStormBlocks` statics. Tests that create `ItemStack` after bootstrap must bind empty data components for unbound built-in items first.
- Keep generated build/runtime output out of source changes. Temporary generators belong in the agent scratch area and must be removed; do not leave untracked `scratch`, `.tmp`, or similar files.
- For plans, walkthroughs, and technical design proposals, use Brazilian Portuguese (`pt-BR`), matching the repository's documentation convention.

When adding a cross-cutting invariant, add or update a focused architecture test under `src/test/java/com/fhfelipefh/sandstorm/architecture` so the behavior is enforced on future changes.
