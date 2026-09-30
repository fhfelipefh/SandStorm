package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.storage.QuantumDiskDriveBlock;
import com.fhfelipefh.sandstorm.content.storage.QuantumDiskDriveBlockEntity;
import com.fhfelipefh.sandstorm.content.storage.QuantumDiskStorage;
import com.fhfelipefh.sandstorm.content.storage.QuantumStorageCartridgeItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

public class QuantumDiskDriveBlockEntityRenderer implements BlockEntityRenderer<QuantumDiskDriveBlockEntity, QuantumDiskDriveRenderState> {

    private final Font font;

    public QuantumDiskDriveBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.font();
    }

    @Override
    public QuantumDiskDriveRenderState createRenderState() {
        return new QuantumDiskDriveRenderState();
    }

    @Override
    public void extractRenderState(QuantumDiskDriveBlockEntity entity, QuantumDiskDriveRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        if (entity.getBlockState().hasProperty(QuantumDiskDriveBlock.FACING)) {
            state.facing = entity.getBlockState().getValue(QuantumDiskDriveBlock.FACING);
        }
        Level level = entity.getLevel();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
        state.lastActivityTick = entity.getLastActivityTick();
        state.isIoActive = (state.animationTicks - state.lastActivityTick) >= 0.0f && (state.animationTicks - state.lastActivityTick) < 18.0f;

        long totalStored = 0;
        long totalCapacity = 0;
        int activeCount = 0;

        for (int i = 0; i < 8; i++) {
            ItemStack cartridge = entity.getItem(i);
            if (!cartridge.isEmpty() && cartridge.getItem() instanceof QuantumStorageCartridgeItem cItem) {
                state.hasCartridge[i] = true;
                long stored = QuantumDiskStorage.getTotalItemCount(cartridge);
                long cap = cItem.getTier().getCapacity();
                state.storedCount[i] = stored;
                state.capacity[i] = cap;
                int pct = cap > 0 ? (int) ((stored * 100) / cap) : 0;
                state.fillPercentage[i] = pct;
                totalStored += stored;
                totalCapacity += cap;
                activeCount++;
            } else {
                state.hasCartridge[i] = false;
                state.storedCount[i] = 0;
                state.capacity[i] = 0;
                state.fillPercentage[i] = 0;
            }
        }
        state.totalStored = totalStored;
        state.totalCapacity = totalCapacity;
        state.activeDisks = activeCount;
    }

    @Override
    public void submit(QuantumDiskDriveRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        float yRot = -state.facing.toYRot();
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.translate(-0.5, -0.5, -0.5);

        renderFrontPanel(state, poseStack, collector);
        renderHolographicDisplay(state, poseStack, collector);

        poseStack.popPose();
    }

    private void renderFrontPanel(QuantumDiskDriveRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        float ticks = state.animationTicks;
        float pulse = (Mth.sin(ticks * 0.15f) + 1.0f) * 0.5f;

        collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
            for (int i = 0; i < 8; i++) {
                int col = i % 2;
                int row = i / 2;

                float cx = col == 0 ? 0.8125f : 0.375f;
                float cy = 0.8125f - (row * 0.1875f);
                float cz = -0.002f;

                boolean hasDisk = state.hasCartridge[i];
                int pct = state.fillPercentage[i];

                int r;
                int g;
                int b;
                int a;

                if (!hasDisk) {
                    r = 35;
                    g = 45;
                    b = 55;
                    a = 160;
                } else if (state.isIoActive && (Mth.sin(ticks * 1.8f + i * 1.5f) > 0.0f)) {
                    r = 128;
                    g = 216;
                    b = 255;
                    a = 255;
                } else if (pct >= 95) {
                    r = 255;
                    g = (int) (23 + pulse * 40);
                    b = 68;
                    a = (int) (200 + pulse * 55);
                } else if (pct >= 80) {
                    r = 255;
                    g = 179;
                    b = 0;
                    a = (int) (180 + pulse * 50);
                } else if (pct >= 50) {
                    r = 0;
                    g = 230;
                    b = 118;
                    a = (int) (180 + pulse * 50);
                } else {
                    r = 0;
                    g = 229;
                    b = 255;
                    a = (int) (180 + pulse * 50);
                }

                float rLed = 0.022f;
                drawLedDiamond(consumer, pose, cx, cy, cz, rLed, r, g, b, a);

                float trayStartX = col == 0 ? 0.76f : 0.32f;
                float trayEndX = col == 0 ? 0.58f : 0.14f;
                float trayW = Math.abs(trayEndX - trayStartX);

                consumer.addVertex(pose, trayStartX, cy, cz).setColor(24, 35, 44, 180).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
                consumer.addVertex(pose, trayEndX, cy, cz).setColor(24, 35, 44, 180).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);

                if (hasDisk) {
                    float fillFrac = Math.max(0.08f, Math.min(1.0f, pct / 100.0f));
                    float fillEndX = trayStartX - (trayW * fillFrac);
                    consumer.addVertex(pose, trayStartX, cy, cz - 0.0005f).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.5f);
                    consumer.addVertex(pose, fillEndX, cy, cz - 0.0005f).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.5f);
                }
            }

            float busY = 0.0625f;
            float busZ = -0.002f;
            int busA = (int) (140 + pulse * 70);
            consumer.addVertex(pose, 0.88f, busY, busZ).setColor(0, 229, 255, busA).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.5f);
            consumer.addVertex(pose, 0.12f, busY, busZ).setColor(0, 229, 255, busA).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.5f);

            if (state.isIoActive) {
                float packetX = 0.88f - ((ticks * 0.12f) % 0.76f);
                consumer.addVertex(pose, packetX + 0.03f, busY, busZ - 0.001f).setColor(255, 255, 255, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(3.5f);
                consumer.addVertex(pose, packetX - 0.03f, busY, busZ - 0.001f).setColor(255, 255, 255, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(3.5f);
            }
        });
    }

    private void drawLedDiamond(VertexConsumer consumer, PoseStack.Pose pose,
                                float cx, float cy, float cz, float radius,
                                int r, int g, int b, int a) {
        consumer.addVertex(pose, cx, cy + radius, cz).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(2.5f);
        consumer.addVertex(pose, cx + radius, cy, cz).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(2.5f);

        consumer.addVertex(pose, cx + radius, cy, cz).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(2.5f);
        consumer.addVertex(pose, cx, cy - radius, cz).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(2.5f);

        consumer.addVertex(pose, cx, cy - radius, cz).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(2.5f);
        consumer.addVertex(pose, cx - radius, cy, cz).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(2.5f);

        consumer.addVertex(pose, cx - radius, cy, cz).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(2.5f);
        consumer.addVertex(pose, cx, cy + radius, cz).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(2.5f);
    }

    private void renderHolographicDisplay(QuantumDiskDriveRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        if (state.activeDisks == 0 && !state.isIoActive) {
            return;
        }

        String line1 = state.isIoActive ? "⚡ I/O ACTIVE ⚡" : "QUANTUM DISK ARRAY";
        String line2 = state.activeDisks + "/8 DISKS • " + String.format(Locale.ROOT, "%,d / %,d", state.totalStored, state.totalCapacity);
        int color1 = state.isIoActive ? 0xFF80D8FF : 0xFF00E5FF;
        int color2 = 0xFFB0BEC5;

        int w1 = this.font.width(line1);
        int w2 = this.font.width(line2);

        poseStack.pushPose();
        poseStack.translate(0.5, 1.15, 0.5);
        poseStack.scale(0.007f, -0.007f, 0.007f);

        collector.submitText(poseStack, -w1 / 2.0f, -10.0f, Component.literal(line1).getVisualOrderText(), false, Font.DisplayMode.NORMAL, color1, 0x80000000, 0xF000F0, 0);
        collector.submitText(poseStack, -w2 / 2.0f, 2.0f, Component.literal(line2).getVisualOrderText(), false, Font.DisplayMode.NORMAL, color2, 0x80000000, 0xF000F0, 0);

        poseStack.popPose();
    }
}
