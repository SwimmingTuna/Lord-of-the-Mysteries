package net.swimmingtuna.lotm.entity.Renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.swimmingtuna.lotm.entity.RoarEntity;

public class RoarEntityRenderer extends EntityRenderer<RoarEntity> {

    public RoarEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(RoarEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(RoarEntity entity) {
        return null;
    }
}