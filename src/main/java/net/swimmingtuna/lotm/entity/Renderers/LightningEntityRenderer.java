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
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.LightningEntity;
import org.joml.Matrix4f;

import java.util.List;

public class LightningEntityRenderer extends EntityRenderer<LightningEntity> {
    private static final float LINE_WIDTH = 0.1f; // Adjust this value to change line thickness
    private static final int SEGMENTS_PER_POINT = 8; // Increase this for smoother curves

    public LightningEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(LightningEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        List<Vec3> positions = entity.getPositions();
        if (positions.size() < 2) return;

        VertexConsumer mainBuilder = buffer.getBuffer(RenderType.leash());
        VertexConsumer firstOutlineBuilder = buffer.getBuffer(RenderType.leash());
        VertexConsumer secondOutlineBuilder = buffer.getBuffer(RenderType.leash());

        poseStack.pushPose();

        Vec3 entityPos = entity.position();
        poseStack.translate(-entityPos.x, -entityPos.y, -entityPos.z);

        Matrix4f matrix = poseStack.last().pose();

        // Render second outline (outermost)
        //renderSmoothLine(secondOutlineBuilder, matrix, positions, LINE_WIDTH + 0.2f, packedLight, 0.7f, 200, 200, 255);

        // Render first outline
        //renderSmoothLine(firstOutlineBuilder, matrix, positions, LINE_WIDTH + 0.1f, packedLight, 0.8f, 220, 220, 255);

        // Render main line
        renderSmoothLine(mainBuilder, matrix, positions, LINE_WIDTH, packedLight, 1.0f, 246, 255, 155);

        poseStack.popPose();
    }



    private void renderSmoothLine(VertexConsumer builder, Matrix4f matrix, List<Vec3> positions, float width, int packedLight, float alpha, int r, int g, int b) {
        Vec3 cameraPos = this.entityRenderDispatcher.camera.getPosition();

        for (int i = 1; i < positions.size(); i++) {
            Vec3 prev = positions.get(i - 1);
            Vec3 current = positions.get(i);

            Vec3 direction = current.subtract(prev).normalize();
            Vec3 cameraToLine = prev.subtract(cameraPos).normalize();
            Vec3 perpendicular = direction.cross(cameraToLine).normalize().scale(width / 2);

            for (int j = 0; j < SEGMENTS_PER_POINT; j++) {
                float t1 = (float) j / SEGMENTS_PER_POINT;
                float t2 = (float) (j + 1) / SEGMENTS_PER_POINT;

                Vec3 pos1 = prev.lerp(current, t1);
                Vec3 pos2 = prev.lerp(current, t2);

                addQuad(builder, matrix, pos1, pos2, perpendicular, packedLight, alpha, r, g, b);
            }
        }
    }

    private void addQuad(VertexConsumer builder, Matrix4f matrix, Vec3 pos1, Vec3 pos2, Vec3 perpendicular, int packedLight, float alpha, int r, int g, int b) {
        addVertex(builder, matrix, pos1.add(perpendicular), packedLight, alpha, r, g, b);
        addVertex(builder, matrix, pos1.subtract(perpendicular), packedLight, alpha, r, g, b);
        addVertex(builder, matrix, pos2.subtract(perpendicular), packedLight, alpha, r, g, b);
        addVertex(builder, matrix, pos2.add(perpendicular), packedLight, alpha, r, g, b);

        // Back face
        addVertex(builder, matrix, pos2.add(perpendicular), packedLight, alpha, r, g, b);
        addVertex(builder, matrix, pos2.subtract(perpendicular), packedLight, alpha, r, g, b);
        addVertex(builder, matrix, pos1.subtract(perpendicular), packedLight, alpha, r, g, b);
        addVertex(builder, matrix, pos1.add(perpendicular), packedLight, alpha, r, g, b);
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
    public ResourceLocation getTextureLocation(LightningEntity entity) {
        return new ResourceLocation(LOTM.MOD_ID, "textures/entity/lightning.png");
    }
}