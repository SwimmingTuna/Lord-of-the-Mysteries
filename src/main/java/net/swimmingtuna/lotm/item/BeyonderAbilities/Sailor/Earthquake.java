package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.StoneEntity;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Earthquake extends Item {
    public Earthquake(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSailorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 75) {
                pPlayer.displayClientMessage(Component.literal("You need 75 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }

        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
            if (holder.isSailorClass() && tyrantSequence.getCurrentSequence() <= 4 && tyrantSequence.useSpirituality(75)) {
                useItem(pPlayer);
            }
            if (!pPlayer.getAbilities().instabuild)
                pPlayer.getCooldowns().addCooldown(this, 60);

        });
            }
        return super.use(level, pPlayer, hand);
    }

    public static void useItem(Player pPlayer) {
        pPlayer.getPersistentData().putInt("sailorEarthquake", 200);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, shoots a projectile that upon hit, pulls the target towards the user\n" +
                    "Spirituality Used: 75\n" +
                    "Cooldown: 3 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    @SubscribeEvent
    public static void earthquakeTick(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        if (event.phase == TickEvent.Phase.END && !pPlayer.level().isClientSide()) {
            int x = pPlayer.getPersistentData().getInt("sailorEarthquake");
            if (x == 200 || x == 180 || x == 160 || x == 140 || x == 120 || x == 100 || x == 80 || x == 60 || x == 40 || x == 20 || x == 1) {
                int sequence = holder.getCurrentSequence();
                int radius = 100 - (sequence * 10);
                for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate((radius)))) {
                    if (entity != pPlayer) {
                        if (entity.onGround()) {
                            entity.hurt(pPlayer.damageSources().fall(), 35 - (sequence * 5));
                        }
                    }
                }
                AABB checkArea = pPlayer.getBoundingBox().inflate(radius);
                Random random = new Random();
                for (BlockPos blockPos : BlockPos.betweenClosed(
                        new BlockPos((int) checkArea.minX, (int) checkArea.minY, (int) checkArea.minZ),
                        new BlockPos((int) checkArea.maxX, (int) checkArea.maxY, (int) checkArea.maxZ))) {

                    if (!pPlayer.level().getBlockState(blockPos).isAir() && isOnSurface(pPlayer.level(), blockPos)) {
                        if (random.nextInt(200) == 1) { // 50% chance to destroy a block
                            pPlayer.level().destroyBlock(blockPos, false);
                        } else if (random.nextInt(200) == 2) { // 10% chance to spawn a stone entity
                            StoneEntity stoneEntity = new StoneEntity(pPlayer.level(), pPlayer);
                            ScaleData scaleData = ScaleTypes.BASE.getScaleData(stoneEntity);
                            stoneEntity.teleportTo(blockPos.getX(), blockPos.getY() + 3, blockPos.getZ());
                            stoneEntity.setDeltaMovement(0, (3 + (Math.random() * (6 - 3)) ), 0);
                            stoneEntity.setStoneYRot((int) (Math.random() * 18));
                            stoneEntity.setStoneXRot((int) (Math.random() * 18));
                            scaleData.setScale((float) (1 + (Math.random()) * 2.0f));
                            pPlayer.level().addFreshEntity(stoneEntity);
                        }
                    }
                }
            }
            if (x >= 0) {
                pPlayer.sendSystemMessage(Component.literal("x is " + x));
                pPlayer.getPersistentData().putInt("sailorEarthquake", x - 1);
            }
        }
    }
    private static boolean isOnSurface(Level level, BlockPos pos) {
        return level.canSeeSky(pos.above()) || !level.getBlockState(pos.above()).isSolid();
    }
}