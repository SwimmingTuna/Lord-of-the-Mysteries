package net.swimmingtuna.lotm.entity.Model;// Made with Blockbench 4.9.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.swimmingtuna.lotm.LOTM;

public class WindCushionModel<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation WIND_CUSHION_LOCATION = new ModelLayerLocation(new ResourceLocation(LOTM.MOD_ID, "wind_cushion"), "main1");
	private final ModelPart main;

	public WindCushionModel(ModelPart root) {
		this.main = root.getChild("main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition floor = root.addOrReplaceChild("floor", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition floorsquare = floor.addOrReplaceChild("floorsquare", CubeListBuilder.create().texOffs(0, 30).addBox(-5.0F, -1.0F, 5.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = floorsquare.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(22, 28).addBox(-5.0F, -1.0F, 5.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r2 = floorsquare.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 28).addBox(-5.0F, -1.0F, 5.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r3 = floorsquare.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 24).addBox(-5.0F, -1.0F, 5.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition floorcirlce = floor.addOrReplaceChild("floorcirlce", CubeListBuilder.create().texOffs(32, 24).addBox(-3.0F, -1.0F, -7.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 6).addBox(-2.0F, -1.0F, -8.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 4).addBox(-2.0F, -1.0F, 7.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(14, 32).addBox(6.0F, -1.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(32, 22).addBox(-3.0F, -1.0F, 6.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(32, 8).addBox(-7.0F, -1.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r4 = floorcirlce.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.0F, -8.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 2).addBox(-2.0F, -1.0F, 7.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition level1 = root.addOrReplaceChild("level1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition level1part1 = level1.addOrReplaceChild("level1part1", CubeListBuilder.create().texOffs(12, 11).addBox(-5.0F, -2.0F, -5.0F, 1.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(23, 18).addBox(-5.0F, -2.0F, 4.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-4.0F, -2.0F, -5.0F, 9.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 11).addBox(4.0F, -2.0F, -5.0F, 1.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition level1part2 = level1.addOrReplaceChild("level1part2", CubeListBuilder.create().texOffs(32, 15).addBox(-4.0F, -2.0F, -4.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(32, 20).addBox(-4.0F, -2.0F, 3.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(23, 10).addBox(3.0F, -2.0F, -4.0F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(23, 0).addBox(-4.0F, -2.0F, -4.0F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition level2 = root.addOrReplaceChild("level2", CubeListBuilder.create().texOffs(22, 32).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 26).addBox(-3.0F, -3.0F, 2.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(16, 21).addBox(-2.0F, -3.0F, -3.0F, 5.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(32, 0).addBox(-3.0F, -3.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition helperpart = root.addOrReplaceChild("helperpart", CubeListBuilder.create().texOffs(12, 0).addBox(-5.0F, -2.0F, -5.0F, 1.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(0, 22).addBox(-5.0F, -2.0F, 4.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(22, 30).addBox(-4.0F, -2.0F, -5.0F, 9.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(4.0F, -2.0F, -5.0F, 1.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	private void animateModelParts(ModelPart part, AnimationDefinition animation, String animationName, float ageInTicks) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}