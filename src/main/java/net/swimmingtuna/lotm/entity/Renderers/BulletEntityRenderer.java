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
import net.swimmingtuna.lotm.entity.DeathKnellBulletEntity;
import net.swimmingtuna.lotm.entity.Model.BulletModel;
import net.swimmingtuna.lotm.entity.RoarEntity;


public class BulletEntityRenderer extends EntityRenderer<DeathKnellBulletEntity> {
    public final Model model;

    public BulletEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new BulletModel<>(context.bakeLayer(BulletModel.BULLET_LOCATION));
    }

    @Override
    public void render(DeathKnellBulletEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(DeathKnellBulletEntity entity) {
        return null;
    }
}
