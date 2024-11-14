package net.swimmingtuna.lotm.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.worldgen.dimension.DimensionInit;
import org.jetbrains.annotations.NotNull;

public class TestItem extends Item {
    public TestItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            switchDimension(player);
        }
        if (!level.isClientSide) LOTM.LOGGER.info("USE");
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (!pContext.getLevel().isClientSide) LOTM.LOGGER.info("USE ON BLOCK");
        return InteractionResult.FAIL;
//        return super.useOn(pContext);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (!pPlayer.level().isClientSide) {
            LOTM.LOGGER.info("INTERACT LIVING ENTITY");
            pPlayer.sendSystemMessage(Component.literal("health is " + pInteractionTarget.getHealth()));
        }
        return InteractionResult.FAIL;
    }

    public static void switchDimension(Player player) {
        if (!player.level().isClientSide()) {
            if (player instanceof ServerPlayer serverPlayer) {
                MinecraftServer server = serverPlayer.getServer();
                if (server != null) {
                    // Check current dimension
                    if (player.level().dimension() == Level.OVERWORLD) {
                        ServerLevel spiritWorld = server.getLevel(DimensionInit.SPIRIT_WORLD_LEVEL_KEY);
                        if (spiritWorld != null) {
                            player.sendSystemMessage(Component.literal("Transporting to SpiritWorld..."));
                            serverPlayer.teleportTo(spiritWorld,
                                    player.getX(),
                                    player.getY(),
                                    player.getZ(),
                                    player.getYRot(),
                                    player.getXRot());
                        }
                    } else if (player.level().dimension() == DimensionInit.SPIRIT_WORLD_LEVEL_KEY) {
                        ServerLevel overworldWorld = server.getLevel(Level.OVERWORLD);
                        if (overworldWorld != null) {
                            player.sendSystemMessage(Component.literal("Transporting to Overworld..."));
                            serverPlayer.teleportTo(overworldWorld,
                                    player.getX() * 8.0, // Convert back to overworld coordinates
                                    player.getY(),
                                    player.getZ() * 8.0,
                                    player.getYRot(),
                                    player.getXRot());
                        }
                    }
                }
            }
        }
    }
}
