package net.swimmingtuna.lotm.entity.Renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.DragonBreathEntity;
import net.swimmingtuna.lotm.entity.Model.DragonBreathModel;
import net.swimmingtuna.lotm.util.LOTMRenderTypes;
import net.swimmingtuna.lotm.util.ParticleColors;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class DragonBreathRenderer extends EntityRenderer<DragonBreathEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(LOTM.MOD_ID, "textures/models/dragon_breath.png");
    private static final ResourceLocation CHARGE = new ResourceLocation(LOTM.MOD_ID, "textures/models/dragon_breath_charge.png");
    private static final int TEXTURE_WIDTH = 16;
    private static final int TEXTURE_HEIGHT = 512;
    private static final float BEAM_RADIUS = 0.5F;
    private boolean clearerView = false;

    private final DragonBreathModel model;

    public DragonBreathRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new DragonBreathModel(context.bakeLayer(DragonBreathModel.LAYER));
    }

    @Override
    public void render(DragonBreathEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        this.clearerView = Minecraft.getInstance().player == entity.getOwner() &&
                Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;

        float yaw = (entity.prevYaw + (entity.renderYaw - entity.prevYaw) * partialTick) * Mth.RAD_TO_DEG;
        float pitch = (entity.prevPitch + (entity.renderPitch - entity.prevPitch) * partialTick) * Mth.RAD_TO_DEG;

        Vector3f color = ParticleColors.FIRE_YELLOW;

        float age = entity.getTime() + partialTick;

        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.mulPose(Axis.ZN.rotationDegrees(pitch));

        VertexConsumer charge = buffer.getBuffer(LOTMRenderTypes.glow(CHARGE));
        this.model.setupAnim(entity, 0.0F, 0.0F, age, 0.0F, 0.0F);
        this.model.renderToBuffer(poseStack, charge, packedLight, OverlayTexture.NO_OVERLAY, color.x, color.y, color.z, 1.0F);

        poseStack.popPose();


        if (entity.getTime() >= DragonBreathEntity.CHARGE) {
            Vec3 collidePos = entity.prevCollidePos.add(entity.collidePos.subtract(entity.prevCollidePos).scale(partialTick));
            Vec3 prevPos = new Vec3(entity.xo, entity.yo, entity.zo);
            Vec3 pos = prevPos.add(entity.position().subtract(prevPos).scale(partialTick));
            float length = (float) collidePos.distanceTo(pos);
            int frame = Mth.floor((entity.animation - 1 + partialTick) * 2);

            if (frame < 0) {
                frame = entity.getFrames() * 2;
            }

            poseStack.pushPose();
            poseStack.scale(entity.getScale(), entity.getScale(), entity.getScale());
            poseStack.translate(0.0F, (entity.getBbHeight() / 2.0F) - 0.5F, 0.0F);

            VertexConsumer beam = buffer.getBuffer(LOTMRenderTypes.glow(TEXTURE));

            float brightness = 1.0F - ((float) entity.getTime() / (entity.getCharge() + entity.getDuration() + entity.getFrames()));

            this.renderBeam(length, yaw, pitch, frame, poseStack, beam,
                    brightness, packedLight);

            poseStack.popPose();
        }
    }

    private void drawCube(float length, int frame, PoseStack poseStack, VertexConsumer consumer, float brightness, int packedLight) {
        float minU = 0.0F;
        float minV = 16.0F / TEXTURE_HEIGHT * frame;
        float maxU = minU + 16.0F / TEXTURE_WIDTH;
        float maxV = minV + 16.0F / TEXTURE_HEIGHT;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        float offset = this.clearerView ? -1.0F : 0.0F;

        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, offset, BEAM_RADIUS, minU, minV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, length, BEAM_RADIUS, minU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, length, BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, offset, BEAM_RADIUS, maxU, minV, brightness, packedLight);

        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, offset, -BEAM_RADIUS, minU, minV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, length, -BEAM_RADIUS, minU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, length, -BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, offset, -BEAM_RADIUS, maxU, minV, brightness, packedLight);

        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, length, -BEAM_RADIUS, minU, minV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, length, BEAM_RADIUS, minU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, length, BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, length, -BEAM_RADIUS, maxU, minV, brightness, packedLight);

        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, offset, -BEAM_RADIUS, minU, minV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, offset, BEAM_RADIUS, minU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, offset, BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, offset, -BEAM_RADIUS, maxU, minV, brightness, packedLight);

        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, length, -BEAM_RADIUS, minU, minV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, length, BEAM_RADIUS, minU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, offset, BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, offset, -BEAM_RADIUS, maxU, minV, brightness, packedLight);

        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, length, -BEAM_RADIUS, minU, minV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, length, BEAM_RADIUS, minU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, offset, BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, offset, -BEAM_RADIUS, maxU, minV, brightness, packedLight);
    }

    private void renderBeam(float length, float yaw, float pitch, int frame, PoseStack poseStack, VertexConsumer consumer, float brightness, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(yaw - 90.0F));
        poseStack.mulPose(Axis.XN.rotationDegrees(pitch));

        this.drawCube(length, frame, poseStack, consumer, brightness, packedLight);

        poseStack.popPose();
    }

    public void drawVertex(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer consumer, float x, float y, float z, float u, float v, float brightness, int packedLight) {
        consumer.vertex(matrix4f, x, y, z)
                .color(brightness, brightness, brightness, 1.0F)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DragonBreathEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }

    @Override
    protected int getBlockLightLevel(@NotNull DragonBreathEntity entity, @NotNull BlockPos pos) {
        return 15;
    }
}
