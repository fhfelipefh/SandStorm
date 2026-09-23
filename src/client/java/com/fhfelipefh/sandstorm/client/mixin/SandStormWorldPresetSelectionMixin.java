package com.fhfelipefh.sandstorm.client.mixin;

import com.fhfelipefh.sandstorm.content.world.SandStormWorldPresets;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

@Mixin(WorldCreationUiState.class)
public abstract class SandStormWorldPresetSelectionMixin {

    @Shadow
    public abstract List<WorldCreationUiState.WorldTypeEntry> getNormalPresetList();

    @Shadow
    public abstract void setWorldType(WorldCreationUiState.WorldTypeEntry worldType);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void sandstorm$setDefaultWorldPreset(Path savesFolder, WorldCreationContext settings, Optional<ResourceKey<WorldPreset>> preset, OptionalLong seed, CallbackInfo ci) {
        if (seed.isEmpty() && (preset.isEmpty() || (preset.isPresent() && preset.get().equals(WorldPresets.NORMAL)))) {
            for (WorldCreationUiState.WorldTypeEntry entry : getNormalPresetList()) {
                if (entry.preset() != null && entry.preset().is(SandStormWorldPresets.DESERT_PLANET)) {
                    setWorldType(entry);
                    break;
                }
            }
        }
    }
}
