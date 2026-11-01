package com.fhfelipefh.sandstorm.core;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.lang.reflect.Method;

public final class PayloadRegistryHelper {

    private static final Method CLIENTBOUND_METHOD;
    private static final Method SERVERBOUND_METHOD;
    private static final Method REGISTER_METHOD;

    static {
        Method clientMethod = null;
        Method serverMethod = null;
        Method regMethod = null;

        try {
            clientMethod = PayloadTypeRegistry.class.getMethod("playS2C");
        } catch (NoSuchMethodException e) {
            try {
                clientMethod = PayloadTypeRegistry.class.getMethod("clientboundPlay");
            } catch (NoSuchMethodException ex) {
                clientMethod = null;
            }
        }

        try {
            serverMethod = PayloadTypeRegistry.class.getMethod("playC2S");
        } catch (NoSuchMethodException e) {
            try {
                serverMethod = PayloadTypeRegistry.class.getMethod("serverboundPlay");
            } catch (NoSuchMethodException ex) {
                serverMethod = null;
            }
        }

        for (Method method : PayloadTypeRegistry.class.getMethods()) {
            if (method.getName().equals("register") && method.getParameterCount() == 2) {
                regMethod = method;
                break;
            }
        }

        if (clientMethod != null) {
            clientMethod.setAccessible(true);
        }
        if (serverMethod != null) {
            serverMethod.setAccessible(true);
        }
        if (regMethod != null) {
            regMethod.setAccessible(true);
        }

        CLIENTBOUND_METHOD = clientMethod;
        SERVERBOUND_METHOD = serverMethod;
        REGISTER_METHOD = regMethod;
    }

    private PayloadRegistryHelper() {
    }

    public static <T extends CustomPacketPayload> void registerClientbound(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec
    ) {
        try {
            if (CLIENTBOUND_METHOD == null) {
                throw new IllegalStateException("PayloadTypeRegistry clientbound method (playS2C / clientboundPlay) not found");
            }
            Object registry = CLIENTBOUND_METHOD.invoke(null);
            invokeRegister(registry, type, codec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register clientbound payload " + type.id(), e);
        }
    }

    public static <T extends CustomPacketPayload> void registerServerbound(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec
    ) {
        try {
            if (SERVERBOUND_METHOD == null) {
                throw new IllegalStateException("PayloadTypeRegistry serverbound method (playC2S / serverboundPlay) not found");
            }
            Object registry = SERVERBOUND_METHOD.invoke(null);
            invokeRegister(registry, type, codec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register serverbound payload " + type.id(), e);
        }
    }

    private static void invokeRegister(Object registry, CustomPacketPayload.Type<?> type, StreamCodec<?, ?> codec) throws Exception {
        if (REGISTER_METHOD != null) {
            REGISTER_METHOD.invoke(registry, type, codec);
            return;
        }
        for (Method method : registry.getClass().getMethods()) {
            if (method.getName().equals("register") && method.getParameterCount() == 2) {
                method.setAccessible(true);
                method.invoke(registry, type, codec);
                return;
            }
        }
        throw new NoSuchMethodException("Could not find register(Type, StreamCodec) on " + registry.getClass().getName());
    }
}
