package net.swimmingtuna.lotm.item.custom.BeyonderAbilities;


import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

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
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            if (spectatorSequence.getCurrentSequence() == 0 && spectatorSequence.useSpirituality(150)) {
                int blinkDistance = pPlayer.getPersistentData().getInt("BlinkDistance");

                Vec3 lookVector = pPlayer.getLookAngle();
                double targetX = pPlayer.getX() + blinkDistance * lookVector.x();
                double targetY = (pPlayer.getY() +1) + blinkDistance * lookVector.y();
                double targetZ = pPlayer.getZ() + blinkDistance * lookVector.z();
                BlockPos playerPos = new BlockPos((int) pPlayer.getX(), (int) pPlayer.getY(), (int) pPlayer.getZ());
                for (int x = -2; x <= 2; x++) {
                    for (int y = -2; y <= 2; y++) {
                        for (int z = -2; z <= 2; z++) {
                            BlockPos targetPos = playerPos.offset(x, y, z);
                            BlockState blockState = level.getBlockState(targetPos);
                            if (blockState.is(Blocks.DIRT) || blockState.is(Blocks.STONE) || blockState.is(Blocks.IRON_ORE) || blockState.is(Blocks.COAL_ORE) || blockState.is(Blocks.NETHERRACK)|| blockState.is(Blocks.SNOW_BLOCK)|| blockState.is(Blocks.SNOW)) {
                                level.setBlockAndUpdate(targetPos, Blocks.AIR.defaultBlockState());
                            }
                        }
                    }
                }

                pPlayer.teleportTo(targetX,targetY,targetZ);
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 20);
            }
        });
        return super.use(level, pPlayer, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, teleport in front of you\n" +
                    "Spirituality Used: 150\n" +
                    "Cooldown: 1 second"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
}
