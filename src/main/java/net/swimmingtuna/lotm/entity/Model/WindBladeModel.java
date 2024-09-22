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

public class WindBladeModel<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation WIND_BLADE_LOCATION = new ModelLayerLocation(new ResourceLocation(LOTM.MOD_ID, "wind_blade"), "main1");
	private final ModelPart main;

	public WindBladeModel(ModelPart root) {
		this.main = root.getChild("main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bb_main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(12, 4).addBox(0.0F, -1.0F, 2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(12, 2).addBox(0.0F, -1.0F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(12, 0).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(0, 12).addBox(0.0F, -2.0F, 2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(9, 11).addBox(0.0F, -2.0F, 4.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(3, 11).addBox(0.0F, -3.0F, 4.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(6, 10).addBox(0.0F, -4.0F, 5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(0, 10).addBox(0.0F, -3.0F, 5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(9, 9).addBox(0.0F, -5.0F, 5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(9, 7).addBox(0.0F, -6.0F, 5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(9, 5).addBox(0.0F, -7.0F, 5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(9, 3).addBox(0.0F, -8.0F, 5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(3, 9).addBox(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(9, 1).addBox(0.0F, -6.0F, 6.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(6, 8).addBox(0.0F, -7.0F, 6.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(0, 8).addBox(0.0F, -9.0F, 5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(3, 7).addBox(0.0F, -10.0F, 5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(6, 6).addBox(0.0F, -8.0F, 6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(6, 4).addBox(0.0F, -11.0F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(6, 2).addBox(0.0F, -12.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(6, 0).addBox(0.0F, -12.0F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 6).addBox(0.0F, -2.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(3, 5).addBox(0.0F, -11.0F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 4).addBox(0.0F, -12.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(3, 3).addBox(0.0F, -13.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(3, 1).addBox(0.0F, -13.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 2).addBox(0.0F, -13.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(0.0F, -13.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1F, 6.0F, -1.0F,0,0,0));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}