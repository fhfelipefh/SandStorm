# No Inline Imports in Code

Always place imports at the top of the file. Never use inline imports (fully qualified class names) inside class bodies, methods, fields, parameters, or expressions.

## Rules
- Do not use fully qualified class names anywhere in the code body (e.g. `net.minecraft.world.item.Rarity.UNCOMMON`, `com.fhfelipefh.sandstorm.content.block.SandStormBlocks.PRINTER_3D`, `java.util.List`, etc.).
- Always import the required class, interface, enum, record, or static member at the top of the file using standard `import` or `import static` statements.
- When there is a name collision between two classes with the same simple name, prefer importing the primary class and aliasing or refactoring rather than writing inline fully-qualified paths.
- All Java source files (`src/main/java`, `src/client/java`, `src/test/java`) must follow this convention and are verified by `NoInlineImportsArchitectureTest`.
