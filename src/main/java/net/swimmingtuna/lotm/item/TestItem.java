package net.swimmingtuna.lotm.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.MeteorEntity;
import net.swimmingtuna.lotm.entity.StoneEntity;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.Awe;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

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
            StoneEntity meteorEntity = new StoneEntity(pPlayer.level(), pPlayer);
            meteorEntity.setDeltaMovement(pPlayer.getLookAngle().normalize().scale(7.0f));
            meteorEntity.setPos(pPlayer.getOnPos().getCenter());
            meteorEntity.setOwner(pPlayer);
            meteorEntity.teleportTo(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
            pPlayer.level().addFreshEntity(meteorEntity);
        }
    }
}
