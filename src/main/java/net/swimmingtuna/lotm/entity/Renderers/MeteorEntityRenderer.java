package net.swimmingtuna.lotm.entity.Renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.Model.MeteorModel;
import net.swimmingtuna.lotm.entity.MeteorEntity;


public class MeteorEntityRenderer extends EntityRenderer<MeteorEntity> {
    public static final ResourceLocation METEOR_LOCATION = new ResourceLocation(LOTM.MOD_ID, "textures/models/meteor.png");
    public Model model;

    public MeteorEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new MeteorModel<>(pContext.bakeLayer(MeteorModel.METEOR_LOCATION));
    }

    @Override
    public void render(MeteorEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();

        VertexConsumer ivertexbuilder = buffers.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }

    @Override
    public  ResourceLocation getTextureLocation( MeteorEntity entity) { return METEOR_LOCATION; }

}
