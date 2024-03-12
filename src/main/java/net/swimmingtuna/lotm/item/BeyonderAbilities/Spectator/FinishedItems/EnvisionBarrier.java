package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BlockInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnvisionBarrier extends Item {

    public EnvisionBarrier(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        BlockPos playerPos = pPlayer.getOnPos();
        AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            if (spectatorSequence.getCurrentSequence() <= 7 && spectatorSequence.useSpirituality((int) (800 / dreamIntoReality.getValue()))) {
                generateBarrier(pPlayer, level, playerPos);
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 100);
            }
        });
        return super.use(level, pPlayer, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, makes a barrier around the user\n" +
                    "Spirituality Used: 800\n" +
                    "Cooldown: 5 seconds "));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    private void generateBarrier(Player pPlayer, Level level, BlockPos playerPos) {
        if (!pPlayer.level().isClientSide()) {
            int radius = pPlayer.getPersistentData().getInt("BarrierRadius");
            int thickness = 1; // Adjust the thickness of the glass dome

            if (domeCenter != null) {
                // Remove the existing glass dome
                for (Map.Entry<BlockPos, BlockState> entry : replacedBlocks.entrySet()) {
                    BlockPos  worldPos = domeCenter.offset(entry.getKey());
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

    private Map<BlockPos, BlockState> replacedBlocks = new HashMap<>();
    private List<BlockPos> replacedAirBlocks = new ArrayList<>();
    private BlockPos domeCenter = null;

    @SubscribeEvent
    public static void barrierRadius(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (!event.player.level().isClientSide && event.phase == TickEvent.Phase.START) {
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                if (spectatorSequence.getCurrentSequence() == 0) {
                    int barrierRadius = pPlayer.getPersistentData().getInt("BarrierRadius");
                    if (pPlayer.isShiftKeyDown() && pPlayer.getMainHandItem().getItem() instanceof EnvisionBarrier) {
                        barrierRadius++;
                        pPlayer.sendSystemMessage(Component.literal("Barrier Radius " + barrierRadius));
                    }
                    if (barrierRadius > 100) {
                        barrierRadius = 0;
                    }
                    pPlayer.getPersistentData().putInt("BarrierRadius", barrierRadius);
                }
            });
        }
    }
}