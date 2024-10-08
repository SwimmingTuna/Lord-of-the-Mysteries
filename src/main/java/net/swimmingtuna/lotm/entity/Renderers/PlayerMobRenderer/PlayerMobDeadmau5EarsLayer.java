package net.swimmingtuna.lotm.entity.Renderers.PlayerMobRenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.swimmingtuna.lotm.entity.PlayerMobEntity;
import net.swimmingtuna.lotm.util.PlayerMobs.TextureUtils;

public class PlayerMobDeadmau5EarsLayer extends RenderLayer<PlayerMobEntity, PlayerModel<PlayerMobEntity>> {

    public PlayerMobDeadmau5EarsLayer(RenderLayerParent<PlayerMobEntity, PlayerModel<PlayerMobEntity>> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, PlayerMobEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if ("deadmau5".equals(entity.getName().getString()) && !entity.isInvisible()) {
            VertexConsumer consumer = bufferIn.getBuffer(RenderType.entitySolid(TextureUtils.getPlayerSkin(entity)));
            int i = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);

            for (int j = 0; j < 2; ++j) {
                float pitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
                matrixStackIn.pushPose();

                if (entity.isBaby()) {
                    pitch *= -0.5f;
                }

                matrixStackIn.mulPose(Axis.YP.rotationDegrees(netHeadYaw));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(pitch));

                if (entity.isBaby()) {
                    matrixStackIn.scale(0.7F, 0.7F, 0.7F);
                    matrixStackIn.translate(0.0F, 1.05F, 0.0F);
                }

                matrixStackIn.translate(0.375F * (float) (j * 2 - 1), 0.0D, 0.0D);
                matrixStackIn.translate(0.0D, -0.375D, 0.0D);

                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-pitch));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(-netHeadYaw));

                float size = 4F / 3F;
                matrixStackIn.scale(size, size, size);

                this.getParentModel().renderEars(matrixStackIn, consumer, packedLightIn, i);
                matrixStackIn.popPose();
            }
        }
    }
}
