package net.swimmingtuna.lotm.entity.Renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.swimmingtuna.lotm.entity.RoarEntity;
import net.swimmingtuna.lotm.entity.StormSealEntity;

public class StormSealEntityRenderer extends EntityRenderer<StormSealEntity> {


    public StormSealEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(StormSealEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
    @Override
    public ResourceLocation getTextureLocation(StormSealEntity entity) {
        return null;
    }
}