package com.iafenvoy.netherite.client.render;

import com.iafenvoy.netherite.block.entity.NetheriteBeaconBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

@Environment(EnvType.CLIENT)
public class NetheriteBeaconBlockEntityRenderer implements BlockEntityRenderer<NetheriteBeaconBlockEntity> {
    public static final Identifier BEAM_TEXTURE = Identifier.of("textures/entity/beacon_beam.png");

    public NetheriteBeaconBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    private static void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, float f, long l, int i, int j, float[] fs) {
        renderLightBeam(matrixStack, vertexConsumerProvider, BEAM_TEXTURE, f, 1.0F, l, i, j, fs, 0.2F, 0.25F);
    }

    public static void renderLightBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier textureId, float tickDelta, float heightScale, long worldTime, int yOffset, int maxY, float[] color, float innerRadius, float outerRadius) {
        int m = yOffset + maxY;
        matrices.push();
        matrices.translate(0.5D, 0.0D, 0.5D);
        float n = ((float) Math.floorMod(worldTime, 40L)) + tickDelta;
        float o = maxY < 0 ? n : -n;
        float p = MathHelper.fractionalPart(o * 0.2F - (float) MathHelper.floor(o * 0.1F));
        float q = color[0];
        float r = color[1];
        float s = color[2];
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(n * 2.25F - 45.0F));
        float af;
        float ai;
        float aj = -innerRadius;
        float aa = -innerRadius;
        float ap = -1.0F + p;
        float aq = maxY * heightScale * (0.5F / innerRadius) + ap;
        renderBeamLayer(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, false)), q, r, s, 1.0F, yOffset, m, 0.0F, innerRadius, innerRadius, 0.0F, aj, 0.0F, 0.0F, aa, 0.0F, 1.0F, aq, ap);
        matrices.pop();
        af = -outerRadius;
        float ag = -outerRadius;
        ai = -outerRadius;
        aj = -outerRadius;
        ap = -1.0F + p;
        aq = maxY * heightScale + ap;
        renderBeamLayer(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, true)), q, r, s, 0.125F, yOffset, m, af, ag, outerRadius, ai, aj, outerRadius, outerRadius, outerRadius, 0.0F, 1.0F, aq, ap);
        matrices.pop();
    }

    private static void renderBeamLayer(MatrixStack matrixStack, VertexConsumer vertexConsumer, float f, float g, float h, float i, int j, int k, float l, float m, float n, float o, float p, float q, float r, float s, float t, float u, float v, float w) {
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        renderBeamFace(entry, matrix4f, vertexConsumer, f, g, h, i, j, k, l, m, n, o, t, u, v, w);
        renderBeamFace(entry, matrix4f, vertexConsumer, f, g, h, i, j, k, r, s, p, q, t, u, v, w);
        renderBeamFace(entry, matrix4f, vertexConsumer, f, g, h, i, j, k, n, o, r, s, t, u, v, w);
        renderBeamFace(entry, matrix4f, vertexConsumer, f, g, h, i, j, k, p, q, l, m, t, u, v, w);
    }

    private static void renderBeamFace(MatrixStack.Entry entry, Matrix4f matrix4f, VertexConsumer vertexConsumer, float f, float g, float h, float i, int j, int k, float l, float m, float n, float o, float p, float q, float r, float s) {
        renderBeamVertex(entry, matrix4f, vertexConsumer, f, g, h, i, k, l, m, q, r);
        renderBeamVertex(entry, matrix4f, vertexConsumer, f, g, h, i, j, l, m, q, s);
        renderBeamVertex(entry, matrix4f, vertexConsumer, f, g, h, i, j, n, o, p, s);
        renderBeamVertex(entry, matrix4f, vertexConsumer, f, g, h, i, k, n, o, p, r);
    }

    private static void renderBeamVertex(MatrixStack.Entry entry, Matrix4f matrix4f, VertexConsumer vertexConsumer, float f, float g, float h, float i, int j, float k, float l, float u, float v) {
        vertexConsumer.vertex(matrix4f, k, j, l).color(f, g, h, i).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(entry, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public void render(NetheriteBeaconBlockEntity beaconBlockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        assert beaconBlockEntity.getWorld() != null;
        long l = beaconBlockEntity.getWorld().getTime();
        List<NetheriteBeaconBlockEntity.BeamSegment> list = beaconBlockEntity.getBeamSegments();
        int k = 0;

        for (int m = 0; m < list.size(); ++m) {
            NetheriteBeaconBlockEntity.BeamSegment beamSegment = list.get(m);
            render(matrixStack, vertexConsumerProvider, tickDelta, l, k, m == list.size() - 1 ? 1024 : beamSegment.getHeight(), beamSegment.getColor());
            k += beamSegment.getHeight();
        }
    }

    @Override
    public boolean rendersOutsideBoundingBox(NetheriteBeaconBlockEntity blockEntity) {
        return true;
    }
}
