package net.swimmingtuna.lotm.entity.Model;// Made with Blockbench 4.9.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.swimmingtuna.lotm.LOTM;

public class MeteorNoLevelModel<T extends Entity> extends EntityModel<T> {
 	public static final ModelLayerLocation METEOR_LOCATION = new ModelLayerLocation(new ResourceLocation(LOTM.MOD_ID, "meteor_no_hurt"), "main1");
	private final ModelPart main;

	public MeteorNoLevelModel(ModelPart root) {
		this.main = root.getChild("main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 40).addBox(-3.0F, -9.75F, 2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 12).addBox(-5.0F, -8.0F, -3.0F, 10.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cube_r1 = bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(32, 40).addBox(4.7F, -8.0F, -1.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition cube_r2 = bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(38, 42).addBox(4.7F, -8.0F, -1.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.3562F, 0.0F));

		PartDefinition cube_r3 = bone.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 44).addBox(4.7F, -8.0F, -1.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.3562F, 0.0F));

		PartDefinition cube_r4 = bone.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(6, 44).addBox(4.7F, -8.0F, -1.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition cube_r5 = bone.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -3.0F, 10.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(0, 24).addBox(-3.0F, -10.0F, -3.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(38, 34).addBox(-3.0F, -9.75F, 2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r6 = bone.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(18, 24).addBox(-3.0F, -9.75F, 2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, 0.0F, 0.0F, 3.1416F));

		PartDefinition cube_r7 = bone.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(26, 0).addBox(-3.0F, -7.5F, 5.5F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, 0.3927F, 0.0F, -3.1416F));

		PartDefinition cube_r8 = bone.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(26, 12).addBox(-3.0F, -9.75F, 2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, 0.0F, -1.5708F, -3.1416F));

		PartDefinition cube_r9 = bone.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(24, 28).addBox(-3.0F, -7.5F, 5.5F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, 0.0F, -1.5708F, -2.7489F));

		PartDefinition cube_r10 = bone.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(32, 4).addBox(-3.0F, -9.75F, 2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, 0.0F, 1.5708F, 3.1416F));

		PartDefinition cube_r11 = bone.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(32, 8).addBox(-3.0F, -7.5F, 5.5F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, 0.0F, 1.5708F, 2.7489F));

		PartDefinition cube_r12 = bone.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(32, 16).addBox(-3.0F, -7.5F, 5.5F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, -2.7489F, 0.0F, 0.0F));

		PartDefinition cube_r13 = bone.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(32, 20).addBox(-3.0F, -9.75F, 2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, -3.1416F, 0.0F, 0.0F));

		PartDefinition cube_r14 = bone.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(24, 32).addBox(-3.0F, -9.75F, 2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

		PartDefinition cube_r15 = bone.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(34, 24).addBox(-3.0F, -7.5F, 5.5F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7489F, 0.0F, 3.1416F));

		PartDefinition cube_r16 = bone.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(24, 36).addBox(-3.0F, -9.75F, 2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r17 = bone.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(38, 30).addBox(-3.0F, -7.5F, 5.5F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, -0.3927F));

		PartDefinition cube_r18 = bone.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(38, 38).addBox(-3.0F, -7.5F, 5.5F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.3927F));

		PartDefinition cube_r19 = bone.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(16, 40).addBox(-3.0F, -7.5F, 5.5F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}
	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}