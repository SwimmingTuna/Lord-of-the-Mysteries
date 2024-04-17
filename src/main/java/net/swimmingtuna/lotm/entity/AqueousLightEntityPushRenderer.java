package net.swimmingtuna.lotm.entity;


import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.swimmingtuna.lotm.LOTM;

public class AqueousLightEntityPushRenderer extends EntityRenderer<AqueousLightEntityPush> {
    public AqueousLightEntityPushRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(AqueousLightEntityPush entity, float entityYaw, float partialTicks, com.mojang.blaze3d.vertex.PoseStack matrixStack, net.minecraft.client.renderer.MultiBufferSource bufferSource, int packedLight) {
        // Implement your custom rendering logic here
        super.render(entity, entityYaw, partialTicks, matrixStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(AqueousLightEntityPush pEntity) {
        return new ResourceLocation(LOTM.MOD_ID, "entity/aqueous_light");
    }
}