package net.swimmingtuna.lotm.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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
import org.jetbrains.annotations.NotNull;

public class TestItem extends Item {
    public TestItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            player.level().addParticle(ParticleTypes.FLASH, player.getX(), player.getY(), player.getZ(), 0,0,0);
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
}
