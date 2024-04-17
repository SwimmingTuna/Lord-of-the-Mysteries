package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnvisionLocationBlink extends Item {

    public EnvisionLocationBlink(Properties pProperties) {
        super(pProperties);
    }

    @SubscribeEvent
    public static void blinkTimer(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (!event.player.level().isClientSide && event.phase == TickEvent.Phase.START) {
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                if (spectatorSequence.getCurrentSequence() == 0) {
                    int blinkDistance = pPlayer.getPersistentData().getInt("BlinkDistance");
                    if (pPlayer.isShiftKeyDown() && pPlayer.getMainHandItem().getItem() instanceof EnvisionLocationBlink) {
                        blinkDistance++;
                        blinkDistance++;
                        blinkDistance++;
                        blinkDistance++;
                        blinkDistance++;
                        pPlayer.sendSystemMessage(Component.literal("Blink Distance is" + blinkDistance));
                    }
                    if (blinkDistance >= 200) {
                        blinkDistance = 0;
                    }
                    pPlayer.getPersistentData().putInt("BlinkDistance", blinkDistance);
                }
            });
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        int blinkDistance = pPlayer.getPersistentData().getInt("BlinkDistance");
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSpectatorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < blinkDistance * 8) {
                pPlayer.displayClientMessage(Component.literal("You need "  + (blinkDistance * 8) + " spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
        }        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (holder.isSpectatorClass() && !pPlayer.level().isClientSide() && spectatorSequence.getCurrentSequence() == 0 && spectatorSequence.useSpirituality(blinkDistance * 8)) {
                Vec3 lookVector = pPlayer.getLookAngle();
                double targetX = pPlayer.getX() + blinkDistance * lookVector.x();
                double targetY = (pPlayer.getY() + 1) + blinkDistance * lookVector.y();
                double targetZ = pPlayer.getZ() + blinkDistance * lookVector.z();
                pPlayer.teleportTo(targetX,targetY ,targetZ);
                BlockPos playerPos = new BlockPos((int) pPlayer.getX(), (int) pPlayer.getY(), (int) pPlayer.getZ());
                BlockPos playerPos1 = new BlockPos((int) pPlayer.getX() +1, (int) pPlayer.getY() +1, (int) pPlayer.getZ() +1);

                for (int x = -2; x <= 2; x++) {
                    for (int y = -2; y <= 2; y++) {
                        for (int z = -2; z <= 2; z++) {
                            BlockPos targetPos = playerPos.offset(x, y, z);
                            BlockPos targetPos1 = playerPos.offset(x+1, y+1, z+1);
                            BlockState blockState = level.getBlockState(targetPos);
                            if (blockState.is(Blocks.DIRT) || blockState.is(Blocks.STONE) || blockState.is(Blocks.IRON_ORE) || blockState.is(Blocks.COAL_ORE)
                                    || blockState.is(Blocks.NETHERRACK) || blockState.is(Blocks.SNOW_BLOCK) || blockState.is(Blocks.SNOW) || blockState.is(Blocks.END_STONE) ||
                                    blockState.is(Blocks.DEEPSLATE) || blockState.is(Blocks.COPPER_ORE) || blockState.is(Blocks.SOUL_SAND) || blockState.is(Blocks.SOUL_SOIL) || blockState.is(Blocks.DEEPSLATE_COPPER_ORE) || blockState.is(Blocks.DEEPSLATE_COAL_ORE)) {
                                level.setBlockAndUpdate(targetPos, Blocks.AIR.defaultBlockState());
                                level.destroyBlock(playerPos, false);
                                level.destroyBlock(targetPos, false);
                                level.destroyBlock(targetPos1, false);
                                level.destroyBlock(playerPos1, false);
                                pPlayer.sendSystemMessage(Component.literal("Working1"));
                            }
                        }
                    }
                }
                AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
                if (!pPlayer.getAbilities().instabuild)

                    pPlayer.getCooldowns().addCooldown( this, (int) (20/ dreamIntoReality.getValue()));
            }
        });
        return super.use(level, pPlayer, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, teleport in front of you\n" +
                    "Shift to Increase Blink Distance\n" +
                    "Left Click for Envision Weather\n" +
                    "Spirituality Used: 150\n" +
                    "Cooldown: 1 second"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof EnvisionLocationBlink) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.EnvisionWeather.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof EnvisionLocationBlink) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.EnvisionWeather.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
}
