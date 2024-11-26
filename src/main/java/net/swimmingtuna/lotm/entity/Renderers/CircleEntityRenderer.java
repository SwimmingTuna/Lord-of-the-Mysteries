package net.swimmingtuna.lotm.entity.Renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.CircleEntity;

public class CircleEntityRenderer extends EntityRenderer<CircleEntity> {
    public static final ResourceLocation CIRCLE_LOCATION = new ResourceLocation(LOTM.MOD_ID, "textures/models/circle_entity_texture_red.png");

    public CircleEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(CircleEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

    }

    @Override
    public ResourceLocation getTextureLocation(CircleEntity entity) {
        return null;
    }
}