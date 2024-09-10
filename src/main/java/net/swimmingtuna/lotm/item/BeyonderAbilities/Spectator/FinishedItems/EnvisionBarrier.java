package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.BlockInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnvisionBarrier extends Item {

    private final Map<BlockPos, BlockState> replacedBlocks = new HashMap<>();
    private final List<BlockPos> replacedAirBlocks = new ArrayList<>();
    private BlockPos domeCenter = null;

    public EnvisionBarrier(Properties properties) {
        super(properties);
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        int activeSlot = player.getInventory().selected;
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof EnvisionBarrier) {
            player.getInventory().setItem(activeSlot, new ItemStack(ItemInit.ENVISION_DEATH.get()));
            heldItem.shrink(1);
        }
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        int activeSlot = player.getInventory().selected;
        if (!player.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof EnvisionBarrier) {
            player.getInventory().setItem(activeSlot, new ItemStack(ItemInit.ENVISION_DEATH.get()));
            heldItem.shrink(1);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < (int) (800 / dreamIntoReality.getValue())) {
                player.displayClientMessage(Component.literal("You need " + (800 / dreamIntoReality.getValue()) + " spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            }
        }
        BlockPos playerPos = player.getOnPos();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && !player.level().isClientSide() && holder.getCurrentSequence() <= 0 && holder.useSpirituality((int) (800 / dreamIntoReality.getValue()))) {
            generateBarrier(player, level, playerPos);
            if (!player.getAbilities().instabuild)
                player.getCooldowns().addCooldown(this, 100);
        }
        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, makes a barrier around the user\n" +
                "Hold Shift to Increase Barrier Radius\n" +
                "Left Click for Envision Death\n" +
                "Spirituality Used: 800\n" +
                "Cooldown: 5 seconds ").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    private void generateBarrier(Player player, Level level, BlockPos playerPos) {
        if (!player.level().isClientSide()) {
            int radius = player.getPersistentData().getInt("BarrierRadius");
            int thickness = 1; // Adjust the thickness of the glass dome

            if (domeCenter != null) {
                // Remove the existing glass dome
                for (Map.Entry<BlockPos, BlockState> entry : replacedBlocks.entrySet()) {
                    BlockPos worldPos = domeCenter.offset(entry.getKey());
                    level.setBlockAndUpdate(worldPos, entry.getValue());
                }
                for (BlockPos airPos : replacedAirBlocks) {
                    BlockPos worldPos = domeCenter.offset(airPos);
                    level.setBlockAndUpdate(worldPos, Blocks.AIR.defaultBlockState());
                }
                replacedBlocks.clear();
                replacedAirBlocks.clear();
                domeCenter = null;
                return;
            }

            domeCenter = playerPos;
            replacedAirBlocks.clear();
            BlockState barrierState = Blocks.OBSIDIAN.defaultBlockState();
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        double distanceFromCenter = pos.distSqr(BlockPos.ZERO);
                        if (distanceFromCenter <= (radius - thickness) * (radius - thickness)) {
                            // Skip placing glass blocks inside the dome
                            continue;
                        } else if (distanceFromCenter <= radius * radius) {
                            BlockPos worldPos = domeCenter.offset(pos);

                            BlockState currentState = level.getBlockState(worldPos);
                            if (currentState.isAir()) {
                                level.setBlockAndUpdate(worldPos, BlockInit.VISIONARY_BARRIER_BLOCK.get().defaultBlockState());
                                replacedAirBlocks.add(pos);
                            } else {
                                replacedBlocks.put(pos, currentState);
                                level.setBlockAndUpdate(worldPos, BlockInit.VISIONARY_BARRIER_BLOCK.get().defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
    }
}