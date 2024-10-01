package net.swimmingtuna.lotm.entity.Renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.swimmingtuna.lotm.entity.CircleEntity;
import org.joml.Matrix4f;

public class CircleEntityRenderer extends EntityRenderer<CircleEntity> {
    private static final int STACKS = 16;
    private static final int SECTIONS = 16;
    private static final float RADIUS = 1.0f;

    public CircleEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(CircleEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0.5, 0);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.lines());
        Matrix4f matrix = poseStack.last().pose();

        for (int i = 0; i <= STACKS; i++) {
            float phi = (float) (Math.PI * i / STACKS);
            renderCircle(matrix, vertexConsumer, phi);
        }

        for (int j = 0; j < SECTIONS; j++) {
            float theta = (float) (2 * Math.PI * j / SECTIONS);
            renderArc(matrix, vertexConsumer, theta);
        }

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderCircle(Matrix4f matrix, VertexConsumer vertexConsumer, float phi) {
        float y = RADIUS * (float) (Math.cos(phi));
        float radiusAtHeight = RADIUS * (float) (Math.sin(phi));

        for (int j = 0; j <= SECTIONS; j++) {
            float theta = (float) (2 * Math.PI * j / SECTIONS);
            float x = radiusAtHeight * (float) (Math.cos(theta));
            float z = radiusAtHeight * (float) (Math.sin(theta));

            vertexConsumer.vertex(matrix, x, y, z)
                    .color(255, 255, 255, 255)
                    .normal(x, y, z)
                    .endVertex();

            if (j < SECTIONS) {
                float nextTheta = (float) (2 * Math.PI * (j + 1) / SECTIONS);
                float nextX = radiusAtHeight * (float) (Math.cos(nextTheta));
                float nextZ = radiusAtHeight * (float) (Math.sin(nextTheta));

                vertexConsumer.vertex(matrix, nextX, y, nextZ)
                        .color(255, 255, 255, 255)
                        .normal(nextX, y, nextZ)
                        .endVertex();
            }
        }
    }

    private void renderArc(Matrix4f matrix, VertexConsumer vertexConsumer, float theta) {
        float x = RADIUS * (float) (Math.cos(theta));
        float z = RADIUS * (float) (Math.sin(theta));

        for (int i = 0; i <= STACKS; i++) {
            float phi = (float) (Math.PI * i / STACKS);
            float y = RADIUS * (float) (Math.cos(phi));
            float radiusAtHeight = RADIUS * (float) (Math.sin(phi));
            float xAtHeight = radiusAtHeight * (float) (Math.cos(theta));
            float zAtHeight = radiusAtHeight * (float) (Math.sin(theta));

            vertexConsumer.vertex(matrix, xAtHeight, y, zAtHeight)
                    .color(255, 255, 255, 255)
                    .normal(xAtHeight, y, zAtHeight)
                    .endVertex();

            if (i < STACKS) {
                float nextPhi = (float) (Math.PI * (i + 1) / STACKS);
                float nextY = RADIUS * (float) (Math.cos(nextPhi));
                float nextRadiusAtHeight = RADIUS * (float) (Math.sin(nextPhi));
                float nextXAtHeight = nextRadiusAtHeight * (float) (Math.cos(theta));
                float nextZAtHeight = nextRadiusAtHeight * (float) (Math.sin(theta));

                vertexConsumer.vertex(matrix, nextXAtHeight, nextY, nextZAtHeight)
                        .color(255, 255, 255, 255)
                        .normal(nextXAtHeight, nextY, nextZAtHeight)
                        .endVertex();
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(CircleEntity entity) {
        return null;
    }
}