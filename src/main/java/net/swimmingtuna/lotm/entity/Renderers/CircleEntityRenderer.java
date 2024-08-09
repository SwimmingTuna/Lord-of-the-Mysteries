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
    private static final int STACKS = 16; // Number of vertical divisions
    private static final int SECTIONS = 16; // Number of horizontal divisions
    private static final float RADIUS = 1.0f; // Sphere radius

    public CircleEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(CircleEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        poseStack.pushPose();

        // Translate to entity position
        poseStack.translate(0, 0.5, 0);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.lines());
        Matrix4f matrix = poseStack.last().pose();

        // Render the sphere
        for (int i = 0; i <= STACKS; i++) {
            float phi = (float) (Math.PI * i / STACKS);
            for (int j = 0; j <= SECTIONS; j++) {
                float theta = (float) (2 * Math.PI * j / SECTIONS);

                float x = RADIUS * (float) (Math.sin(phi) * Math.cos(theta));
                float y = RADIUS * (float) (Math.cos(phi));
                float z = RADIUS * (float) (Math.sin(phi) * Math.sin(theta));

                vertexConsumer.vertex(matrix, x, y, z)
                        .color(255, 255, 255, 255)
                        .normal(x, y, z)
                        .endVertex();

                // Connect to next point in the same stack
                if (j < SECTIONS) {
                    float nextTheta = (float) (2 * Math.PI * (j + 1) / SECTIONS);
                    float nextX = RADIUS * (float) (Math.sin(phi) * Math.cos(nextTheta));
                    float nextZ = RADIUS * (float) (Math.sin(phi) * Math.sin(nextTheta));

                    vertexConsumer.vertex(matrix, nextX, y, nextZ)
                            .color(255, 255, 255, 255)
                            .normal(nextX, y, nextZ)
                            .endVertex();
                }

                // Connect to the point in the next stack
                if (i < STACKS) {
                    float nextPhi = (float) (Math.PI * (i + 1) / STACKS);
                    float nextY = RADIUS * (float) (Math.cos(nextPhi));
                    float nextX = RADIUS * (float) (Math.sin(nextPhi) * Math.cos(theta));
                    float nextZ = RADIUS * (float) (Math.sin(nextPhi) * Math.sin(theta));

                    vertexConsumer.vertex(matrix, nextX, nextY, nextZ)
                            .color(255, 255, 255, 255)
                            .normal(nextX, nextY, nextZ)
                            .endVertex();
                }
            }
        }

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(CircleEntity entity) {
        return null; // No texture used
    }
}