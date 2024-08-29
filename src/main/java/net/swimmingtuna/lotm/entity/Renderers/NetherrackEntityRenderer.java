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
import net.swimmingtuna.lotm.entity.Model.NetherrackEntityModel;
import net.swimmingtuna.lotm.entity.NetherrackEntity;


public class NetherrackEntityRenderer extends EntityRenderer<NetherrackEntity> {
    public static final ResourceLocation NETHERRACK_LOCATION = new ResourceLocation(LOTM.MOD_ID, "textures/models/netherrackentity.png");
    public Model model;

    public NetherrackEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new NetherrackEntityModel<>(pContext.bakeLayer(NetherrackEntityModel.NETHERRACK_MODEL_LOCATION));
    }

    @Override
    public void render(NetherrackEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));

        VertexConsumer ivertexbuilder = buffers.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }

    @Override
    public  ResourceLocation getTextureLocation( NetherrackEntity entity) { return NETHERRACK_LOCATION; }

}
