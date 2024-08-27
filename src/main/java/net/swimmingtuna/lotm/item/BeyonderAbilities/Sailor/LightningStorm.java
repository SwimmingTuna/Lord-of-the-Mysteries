package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.LeftClickC2S;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LightningStorm extends Item {
    public LightningStorm(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSailorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 20) {
                pPlayer.displayClientMessage(Component.literal("You need 20 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                if (holder.isSailorClass() && tyrantSequence.getCurrentSequence() <= 8 && tyrantSequence.useSpirituality(20)) {
                    useItem(pPlayer);
                }
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 200);
            });
        }
        return super.use(level, pPlayer, hand);
    }

    public static void useItem(Player pPlayer) { //add logic to add persitatent data of targetX,
        if (!pPlayer.level().isClientSide()) {
            int sailorStormVec = pPlayer.getPersistentData().getInt("sailorStormVec");
            Vec3 lookVec = pPlayer.getLookAngle();
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            int sequence = holder.getCurrentSequence();
            double targetX = pPlayer.getX() + sailorStormVec * lookVec.x();
            double targetY = pPlayer.getY() + sailorStormVec * lookVec.y();
            double targetZ = pPlayer.getZ() + sailorStormVec * lookVec.z();
            pPlayer.getPersistentData().putDouble("sailorStormVecX", targetX);
            pPlayer.getPersistentData().putDouble("sailorStormVecY", targetY);
            pPlayer.getPersistentData().putDouble("sailorStormVecZ", targetZ);
            CompoundTag persistentData = pPlayer.getPersistentData();
            persistentData.putInt("sailorLightningStorm", 500 - (sequence * 80));
            if (sequence <= 0) {
                persistentData.putInt("sailorLightningStormTyrant", 500);
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, causes the user to shoot punches powerfully all around the user, damaging everything around them\n" +
                    "Spirituality Used: 20\n" +
                    "Cooldown: 10 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    @SubscribeEvent
    public static void sailorLightningStorm(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        Style style = BeyonderUtil.getStyle(pPlayer);
        double distance = pPlayer.getPersistentData().getDouble("sailorLightningStormDistance");
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.END) {
            CompoundTag tag = pPlayer.getPersistentData();
            if (distance > 300) {
                tag.putDouble("sailorLightningStormDistance", 0);
                pPlayer.sendSystemMessage(Component.literal("Storm Radius Is 0").withStyle(style));
            }
            int tyrantVer = tag.getInt("sailorLightningStormTyrant");
            int sailorMentioned = tag.getInt("tyrantMentionedInChat");
            int sailorLightningStorm1 = tag.getInt("sailorLightningStorm1");
            int x1 =  tag.getInt("sailorStormVecX1");
            int y1 =  tag.getInt("sailorStormVecY1");
            int z1 =  tag.getInt("sailorStormVecZ1");
            if (sailorMentioned >= 1) {
                tag.putInt("tyrantMentionedInChat",sailorMentioned - 1 );
                if (sailorLightningStorm1 >= 1) {
                    LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), pPlayer.level());
                    lightningEntity.setSpeed(10.0f);
                    lightningEntity.setDeltaMovement((Math.random() * 0.4) - 0.2, -4, (Math.random() * 0.4) - 0.2);
                    lightningEntity.setMaxLength(30);
                    lightningEntity.setOwner(pPlayer);
                    lightningEntity.setOwner(pPlayer);
                    lightningEntity.setNoUp(true);
                    lightningEntity.teleportTo(x1 + ((Math.random() * 300) - (double) 300 / 2), y1 + 80, z1 + ((Math.random() * 300) - (double) 300 / 2));
                    pPlayer.level().addFreshEntity(lightningEntity);
                    pPlayer.level().addFreshEntity(lightningEntity);
                    pPlayer.level().addFreshEntity(lightningEntity);
                    pPlayer.level().addFreshEntity(lightningEntity);
                    if (tyrantVer >= 1) {
                        pPlayer.level().addFreshEntity(lightningEntity);
                        pPlayer.level().addFreshEntity(lightningEntity);
                        pPlayer.level().addFreshEntity(lightningEntity);
                        pPlayer.level().addFreshEntity(lightningEntity);
                        tag.putInt("sailorLightningStormTyrant", tyrantVer - 1);
                    }
                    tag.putInt("sailorLightningStorm1", sailorLightningStorm1 - 1);
                }
            }
            int sailorLightningStorm = tag.getInt("sailorLightningStorm");
            int stormVec = tag.getInt("sailorStormVec");
            double x = tag.getInt("sailorStormVecX");
            double y = tag.getInt("sailorStormVecY");
            double z = tag.getInt("sailorStormVecZ");
            if (sailorLightningStorm >= 1) {
                LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), pPlayer.level());
                lightningEntity.setSpeed(10.0f);
                lightningEntity.setDeltaMovement((Math.random() * 0.4) - 0.2, -4, (Math.random() * 0.4) - 0.2);
                lightningEntity.setMaxLength(30);
                lightningEntity.setOwner(pPlayer);
                lightningEntity.setOwner(pPlayer);
                lightningEntity.setNoUp(true);
                lightningEntity.teleportTo(x + ((Math.random() * distance) - (double) distance / 2), y + 80, z + ((Math.random() * distance) - (double) distance / 2));
                pPlayer.level().addFreshEntity(lightningEntity);
                pPlayer.level().addFreshEntity(lightningEntity);
                pPlayer.level().addFreshEntity(lightningEntity);
                pPlayer.level().addFreshEntity(lightningEntity);
                tag.putInt("sailorLightningStorm", sailorLightningStorm - 1);
            }
            if (holder != null) {
                if (holder.isSailorClass() && holder.getCurrentSequence() <= 3 && pPlayer.getMainHandItem().getItem() instanceof LightningStorm) {
                    if (pPlayer.isShiftKeyDown()) {
                        tag.putInt("sailorStormVec", stormVec + 10);
                        pPlayer.sendSystemMessage(Component.literal("Sailor Storm Spawn Distance is " + stormVec).withStyle(style));
                    }
                    if (stormVec > 300) {
                        tag.putInt("sailorStormVec", 0);
                        stormVec = 0;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void leftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        Style style = BeyonderUtil.getStyle(pPlayer);
        if (pPlayer.getMainHandItem().getItem() instanceof LightningStorm) {
            LOTMNetworkHandler.sendToServer(new LeftClickC2S());
        }
    }

    @SubscribeEvent
    public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        Style style = BeyonderUtil.getStyle(pPlayer);
        if (!pPlayer.level().isClientSide()) {
            if (pPlayer.getMainHandItem().getItem() instanceof LightningStorm) {
                CompoundTag tag = pPlayer.getPersistentData();
                double distance = tag.getDouble("sailorLightningStormDistance");
                tag.putDouble("sailorLightningStormDistance", (int) (distance + 30));
                pPlayer.sendSystemMessage(Component.literal("Storm Radius Is" + distance).withStyle(style));
                if (distance > 300) {
                    tag.putDouble("sailorLightningStormDistance", 0);
                }
            }
        }
    }
}