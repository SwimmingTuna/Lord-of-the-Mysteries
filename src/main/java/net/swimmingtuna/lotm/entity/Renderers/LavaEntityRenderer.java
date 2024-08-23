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
import net.swimmingtuna.lotm.entity.LavaEntity;
import net.swimmingtuna.lotm.entity.Model.LavaEntityModel;
import net.swimmingtuna.lotm.entity.Model.StoneEntityModel;
import net.swimmingtuna.lotm.entity.StoneEntity;


public class LavaEntityRenderer extends EntityRenderer<LavaEntity> {
    public static final ResourceLocation LAVA_LOCATION = new ResourceLocation(LOTM.MOD_ID, "textures/models/lava_entity.png");
    public Model model;

    public LavaEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new LavaEntityModel<>(pContext.bakeLayer(LavaEntityModel.LAVA_ENTITY_LOCATION));
    }

    @Override
    public void render(LavaEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));

        VertexConsumer ivertexbuilder = buffers.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }

    @Override
    public  ResourceLocation getTextureLocation( LavaEntity entity) { return LAVA_LOCATION; }

}
