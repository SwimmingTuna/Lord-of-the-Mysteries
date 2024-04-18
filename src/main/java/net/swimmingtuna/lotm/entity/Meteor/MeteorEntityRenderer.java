package net.swimmingtuna.lotm.entity.Meteor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.swimmingtuna.lotm.LOTM;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class MeteorEntityRenderer extends EntityRenderer<MeteorEntity> {
    public static final ResourceLocation METEOR_LOCATION = new ResourceLocation(LOTM.MOD_ID, "textures/models/meteor.png");
    private final MeteorModel model;
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(METEOR_LOCATION);

    public MeteorEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new MeteorModel(pContext.bakeLayer(ModelLayers.TRIDENT));
    }

    @Override
    public void render(MeteorEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        PoseStack.Pose lastPose = poseStack.last();
        Matrix4f pose = lastPose.pose();
        Matrix3f normal = lastPose.normal();
        VertexConsumer builder = buffers.getBuffer(RENDER_TYPE);

        vertex(builder, pose, normal, packedLight, 0.0f, 0, 0, 1);
        vertex(builder, pose, normal, packedLight, 1.0f, 0, 1, 1);
        vertex(builder, pose, normal, packedLight, 1.0f, 1, 1, 0);
        vertex(builder, pose, normal, packedLight, 0.0f, 1, 0, 0);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }

    private static void vertex(VertexConsumer builder, Matrix4f pose, Matrix3f normal, int packedLight, float x, int y, int u, int v) {
        builder.vertex(pose, x - 0.5f, (float) y - 0.25f, 0.0f)
                .color(255, 255, 255, 255)
                .uv((float) u, (float) v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0.0f, 1.0f, 0.0f)
                .endVertex();
    }

    @Override public ResourceLocation getTextureLocation(MeteorEntity entity) { return METEOR_LOCATION; }

}
