package net.swimmingtuna.lotm.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.entity.PlayerMobEntity;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.SoundInit;

public class TestItem extends Item {
    public TestItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        useAbilities(pPlayer);
        return super.use(level, pPlayer, hand);
    }
    public static void useAbilities(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            PlayerMobEntity playerMobEntity = new PlayerMobEntity(EntityInit.PLAYER_MOB_ENTITY.get(), pPlayer.level());
            playerMobEntity.setUsername("Darkere");
            playerMobEntity.setPathway("spectator");
            playerMobEntity.setSequence(0);
            playerMobEntity.teleportTo(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
            pPlayer.level().addFreshEntity(playerMobEntity);
        }
    }
    public static void useAbilities2(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundInit.SIREN_SONG_HARM_1.get(), SoundSource.NEUTRAL, 1f, 1f);

        }
    }
}
