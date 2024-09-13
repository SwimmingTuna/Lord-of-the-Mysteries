package net.swimmingtuna.lotm.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.snapshots.ForgeSnapshotsMod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.PlayerMobEntity;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.SoundInit;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TestItem extends Item {
    public TestItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
//        useAbilities(player);
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
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
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
            PlayerMobEntity playerMobEntity = new PlayerMobEntity(EntityInit.PLAYER_MOB_ENTITY.get(), player.level());
            playerMobEntity.setUsername("Darkere");
            playerMobEntity.setPathway("spectator");
            playerMobEntity.setSequence(0);
            playerMobEntity.teleportTo(player.getX(), player.getY(), player.getZ());
            player.level().addFreshEntity(playerMobEntity);
        }
    }

    public static void useAbilities2(Player player) {
        if (!player.level().isClientSide()) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundInit.SIREN_SONG_HARM_1.get(), SoundSource.NEUTRAL, 1f, 1f);

        }
    }
}
