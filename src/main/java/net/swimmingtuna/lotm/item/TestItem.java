package net.swimmingtuna.lotm.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TestItem extends Item {
    public TestItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        useAbilities2(player);
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
        if (!pPlayer.level().isClientSide) LOTM.LOGGER.info("INTERACT LIVING ENTITY");
        return InteractionResult.FAIL;
//        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!event.getLevel().isClientSide) LOTM.LOGGER.info("ENTITY INTERACT EVENT");
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.FAIL);
    }

    public static void useAbilities(Player player) {
        if (!player.level().isClientSide()) {
            LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, player.level());
            lightningBolt.setDamage(15);
            lightningBolt.teleportTo(player.getX(),player.getY(),player.getZ());
            player.level().addFreshEntity(lightningBolt);
            player.getPersistentData().putInt("luckMCLightningImmunity", 15);
        }
    }

    public static void useAbilities2(Player player) {
        if (!player.level().isClientSide()) {
            player.getPersistentData().putInt("calamityExplosion", 12);
        }
    }

}
