package net.swimmingtuna.lotm.entity.Model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.DragonBreathEntity;
import org.jetbrains.annotations.NotNull;

public class DragonBreathModel extends EntityModel<DragonBreathEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(LOTM.MOD_ID, "dragon_breath"), "main");

    private final ModelPart body1;
    private final ModelPart body2;
    private final ModelPart body3;
    private final ModelPart body4;

    public DragonBreathModel(ModelPart root) {
        this.body1 = root.getChild("body1");
        this.body2 = root.getChild("body2");
        this.body3 = root.getChild("body3");
        this.body4 = root.getChild("body4");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        part.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, CubeDeformation.NONE), PartPose.offset(0.0F, 2.0F, 0.0F));
        part.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, CubeDeformation.NONE), PartPose.offset(0.0F, 2.0F, 0.0F));
        part.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, CubeDeformation.NONE), PartPose.offset(0.0F, 2.0F, 0.0F));
        part.addOrReplaceChild("body4", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, CubeDeformation.NONE), PartPose.offset(0.0F, 2.0F, 0.0F));

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.body4.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.body3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.body2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.body1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private void setScale(ModelPart part, float xScale, float yScale, float zScale) {
        part.xScale = xScale;
        part.yScale = yScale;
        part.zScale = zScale;
    }

    @Override
    public void setupAnim(@NotNull DragonBreathEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float fraction = Math.min(1.0F, ageInTicks / DragonBreathEntity.CHARGE);
        float scale = (float) (Math.pow(fraction, 0.5F) * 3 + 0.05F * Math.cos(ageInTicks * 3));

        this.setScale(this.body4, scale * 0.4F, scale * 0.4F, scale * 0.4F);
        this.setScale(this.body3, scale * 0.6F, scale * 0.6F, scale * 0.6F);
        this.setScale(this.body2, scale * 0.8F, scale * 0.8F, scale * 0.8F);
        this.setScale(this.body1, scale, scale, scale);
    }
}

