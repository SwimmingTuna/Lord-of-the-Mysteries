package net.swimmingtuna.lotm.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.TornadoEntity;
import net.swimmingtuna.lotm.spirituality.ModAttributes;

public class TestItem extends Item {
    public TestItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            summonThing(pPlayer);
        }
        return super.use(level, pPlayer, hand);
    }

    private void summonThing(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            TornadoEntity tornadoEntity = new TornadoEntity(pPlayer.level(), pPlayer);
            Vec3 lookVec = pPlayer.getLookAngle();
            tornadoEntity.setDeltaMovement(lookVec.x,lookVec.y, lookVec.z);
            tornadoEntity.setTornadoRadius(20);
            tornadoEntity.setTornadoHeight(100);
            pPlayer.level().addFreshEntity(tornadoEntity);
        }
    }
}
