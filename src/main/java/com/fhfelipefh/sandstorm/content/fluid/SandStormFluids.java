package com.fhfelipefh.sandstorm.content.fluid;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.FlowingFluid;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

public class SandStormFluids {

    static {
        ensureRegistryOpen();
    }

    private static void ensureRegistryOpen() {
        try {
            Field holdersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
            holdersField.setAccessible(true);
            if (holdersField.get(BuiltInRegistries.FLUID) == null) {
                holdersField.set(BuiltInRegistries.FLUID, new IdentityHashMap<>());
                Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
                frozenField.setAccessible(true);
                frozenField.set(BuiltInRegistries.FLUID, false);
            }
        } catch (Exception ignored) {
        }
    }

    public static final FlowingFluid RADIOACTIVE_SLUDGE = Registry.register(
            BuiltInRegistries.FLUID,
            SandStormMod.id("radioactive_sludge"),
            new RadioactiveSludgeFluid.Source()
    );

    public static final FlowingFluid FLOWING_RADIOACTIVE_SLUDGE = Registry.register(
            BuiltInRegistries.FLUID,
            SandStormMod.id("flowing_radioactive_sludge"),
            new RadioactiveSludgeFluid.Flowing()
    );

    public static final FlowingFluid SYNAPTIC_FLUID = Registry.register(
            BuiltInRegistries.FLUID,
            SandStormMod.id("synaptic_fluid"),
            new SynapticFluid.Source()
    );

    public static final FlowingFluid FLOWING_SYNAPTIC_FLUID = Registry.register(
            BuiltInRegistries.FLUID,
            SandStormMod.id("flowing_synaptic_fluid"),
            new SynapticFluid.Flowing()
    );

    public static void initialize() {
    }
}
