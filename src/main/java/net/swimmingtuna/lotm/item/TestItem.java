package net.swimmingtuna.lotm.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.entity.CircleEntity;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.init.EntityInit;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class TestItem extends Item {
    public TestItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            summonThing2(pPlayer);
        }
        return super.use(level, pPlayer, hand);
    }

    public static void summonThing(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            pPlayer.getPersistentData().putInt("sailorSeal", 1200);
            pPlayer.getPersistentData().putInt("sailorSealX", (int) pPlayer.getX());
            pPlayer.getPersistentData().putInt("sailorSeaY", (int) pPlayer.getY());
            pPlayer.getPersistentData().putInt("sailorSealZ", (int) pPlayer.getZ());
        }
    }

    private void summonThing2(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            pPlayer.setTicksFrozen(60);

        }
    }
}
