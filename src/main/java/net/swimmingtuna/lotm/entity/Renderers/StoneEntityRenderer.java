package net.swimmingtuna.lotm.entity.Renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.Model.StoneEntityModel;
import net.swimmingtuna.lotm.entity.StoneEntity;


public class StoneEntityRenderer extends EntityRenderer<StoneEntity> {
    public static final ResourceLocation STONE_LOCATION = new ResourceLocation(LOTM.MOD_ID, "textures/models/stoneentity.png");
    public Model model;

    public StoneEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new StoneEntityModel<>(pContext.bakeLayer(StoneEntityModel.STONE_MODEL_LOCATION));
    }

    @Override
    public void render(StoneEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));

        VertexConsumer vertexConsumer = buffers.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(StoneEntity entity) {
        return STONE_LOCATION;
    }

}
