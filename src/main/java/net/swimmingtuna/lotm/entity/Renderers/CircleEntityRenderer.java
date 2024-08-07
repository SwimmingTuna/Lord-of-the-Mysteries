package net.swimmingtuna.lotm.entity.Renderers;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
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

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.solid()); // Use RenderType.solid() for no texture

        poseStack.pushPose();

        // Get the entity's position
        Vec3 entityPos = entity.position();
        poseStack.translate(-entityPos.x, -entityPos.y, -entityPos.z);

        Matrix4f matrix = poseStack.last().pose();

        // Render the sphere
        renderSphere(vertexConsumer, matrix, packedLight, 1.0f, 255, 255, 255); // White color

        poseStack.popPose();
    }

    private void renderSphere(VertexConsumer builder, Matrix4f matrix, int packedLight, float alpha, int r, int g, int b) {
        float thetaStep = (float) Math.PI / STACKS;
        float phiStep = (float) (2.0 * Math.PI) / SECTIONS;

        for (int i = 0; i < STACKS; i++) {
            float theta1 = i * thetaStep;
            float theta2 = (i + 1) * thetaStep;

            for (int j = 0; j < SECTIONS; j++) {
                float phi1 = j * phiStep;
                float phi2 = (j + 1) * phiStep;

                // Calculate the vertices of the two triangles for this segment
                Vec3 v1 = sphericalToCartesian(RADIUS, theta1, phi1);
                Vec3 v2 = sphericalToCartesian(RADIUS, theta1, phi2);
                Vec3 v3 = sphericalToCartesian(RADIUS, theta2, phi1);
                Vec3 v4 = sphericalToCartesian(RADIUS, theta2, phi2);

                addQuad(builder, matrix, v1, v2, v3, v4, packedLight, alpha, r, g, b);
            }
        }
    }

    private Vec3 sphericalToCartesian(float radius, float theta, float phi) {
        float x = radius * (float) Math.sin(theta) * (float) Math.cos(phi);
        float y = radius * (float) Math.cos(theta);
        float z = radius * (float) Math.sin(theta) * (float) Math.sin(phi);
        return new Vec3(x, y, z);
    }

    private void addQuad(VertexConsumer builder, Matrix4f matrix, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, int packedLight, float alpha, int r, int g, int b) {
        addVertex(builder, matrix, v1, packedLight, alpha, r, g, b);
        addVertex(builder, matrix, v2, packedLight, alpha, r, g, b);
        addVertex(builder, matrix, v3, packedLight, alpha, r, g, b);

        addVertex(builder, matrix, v2, packedLight, alpha, r, g, b);
        addVertex(builder, matrix, v4, packedLight, alpha, r, g, b);
        addVertex(builder, matrix, v3, packedLight, alpha, r, g, b);
    }

    private void addVertex(VertexConsumer builder, Matrix4f matrix, Vec3 pos, int packedLight, float alpha, int r, int g, int b) {
        builder.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(r, g, b, (int) (alpha * 255))
                .uv(0, 0)
                .overlayCoords(0, 0)
                .uv2(packedLight)
                .normal(1, 0, 0)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(CircleEntity entity) {
        return null; // No texture used
    }
}

