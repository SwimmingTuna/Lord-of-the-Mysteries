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
    public TestItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        useAbilities(player);
        return super.use(level, player, hand);
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
