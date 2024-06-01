package net.swimmingtuna.lotm.entity.Renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.Model.WindCushionModel;
import net.swimmingtuna.lotm.entity.WindCushionEntity;


public class WindCushionRenderer extends EntityRenderer<WindCushionEntity> {
    public static final ResourceLocation WIND_CUSHION_LOCATION = new ResourceLocation(LOTM.MOD_ID, "textures/models/wind_cushion.png");
    public Model model;

    public WindCushionRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new WindCushionModel<>(pContext.bakeLayer(WindCushionModel.WIND_CUSHION_LOCATION));
    }

    @Override
    public void render(WindCushionEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();
        LivingEntity owner = (LivingEntity) entity.getOwner();
        float ownerPitch = owner.getPersistentData().getInt("windCushionXRot");
        float ownerYaw = owner.getPersistentData().getInt("windCushionYRot");
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entity.getXRot(), ownerYaw)));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.getYRot(), ownerPitch)));


        VertexConsumer ivertexbuilder = buffers.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.5F);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }
    @Override
    public  ResourceLocation getTextureLocation( WindCushionEntity entity) { return WIND_CUSHION_LOCATION; }

}