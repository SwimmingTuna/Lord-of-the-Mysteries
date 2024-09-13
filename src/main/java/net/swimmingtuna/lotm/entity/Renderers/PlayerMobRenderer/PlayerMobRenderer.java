package net.swimmingtuna.lotm.entity.Renderers.PlayerMobRenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.swimmingtuna.lotm.entity.PlayerMobEntity;
import net.swimmingtuna.lotm.util.PlayerMobs.TextureUtils;

public class PlayerMobRenderer extends HumanoidMobRenderer<PlayerMobEntity, PlayerModel<PlayerMobEntity>> {

    private final PlayerModel<PlayerMobEntity> steveModel;
    private final PlayerModel<PlayerMobEntity> alexModel;
    private final RenderLayer<PlayerMobEntity, PlayerModel<PlayerMobEntity>> steveArmorModel;
    private final RenderLayer<PlayerMobEntity, PlayerModel<PlayerMobEntity>> alexArmorModel;

    private final int armorLayerIndex;
    public PlayerMobRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        steveModel = this.model;
        alexModel = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
        steveArmorModel = new HumanoidArmorLayer<>(this, new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager());
        alexArmorModel = new HumanoidArmorLayer<>(this, new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM_INNER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM_OUTER_ARMOR)), context.getModelManager());

        var arrowLayer = new ArrowLayer<>(context, this);
        this.addLayer(arrowLayer);
        armorLayerIndex = layers.indexOf(arrowLayer);
        this.addLayer(new PlayerMobDeadmau5EarsLayer(this));
        this.addLayer(new PlayerMobCapeLayer(this));
    }

    @Override
    public void render(PlayerMobEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        boolean slim = TextureUtils.getPlayerSkinType(entity.getProfile()) == TextureUtils.SkinType.SLIM;
        model = slim ? alexModel: steveModel;
        layers.remove(steveArmorModel);
        layers.remove(alexArmorModel);
        // Make sure we add the armor layer before most other layers as you normally do
        layers.add(armorLayerIndex, slim ? alexArmorModel: steveArmorModel);

        model.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        model.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        ItemStack stack = entity.getMainHandItem();
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof CrossbowItem) {
                if (entity.isChargingCrossbow())
                    setHandPose(entity, HumanoidModel.ArmPose.CROSSBOW_CHARGE);
                else
                    setHandPose(entity, HumanoidModel.ArmPose.CROSSBOW_HOLD);
            } else if (stack.getItem() instanceof BowItem && entity.isAggressive())
                setHandPose(entity, HumanoidModel.ArmPose.BOW_AND_ARROW);
            else
                setHandPose(entity, HumanoidModel.ArmPose.ITEM);
        }

        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    private void setHandPose(PlayerMobEntity entity, HumanoidModel.ArmPose pose) {
        if (entity.getMainArm() == HumanoidArm.RIGHT) {
            model.rightArmPose = pose;
        } else {
            model.leftArmPose = pose;
        }
    }

    @Override
    protected void scale(PlayerMobEntity entity, PoseStack matrix, float partialTickTime) {
        matrix.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public ResourceLocation getTextureLocation(PlayerMobEntity entity) {
        return TextureUtils.getPlayerSkin(entity);
    }
}
