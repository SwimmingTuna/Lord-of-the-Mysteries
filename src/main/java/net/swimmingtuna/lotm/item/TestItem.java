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
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.entity.StoneEntity;
import net.swimmingtuna.lotm.entity.TornadoEntity;
import net.swimmingtuna.lotm.init.EntityInit;
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

    public static void summonThing(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            LightningEntity lightningEntity = new LightningEntity(EntityInit.LINE_ENTITY.get(), pPlayer.level());
            lightningEntity.setFallDown(true);
            lightningEntity.setNewStartPos(new Vec3(pPlayer.getX(), pPlayer.getY() + 50, pPlayer.getZ()));
            lightningEntity.setSpeed(10);
            lightningEntity.setTargetPos(new Vec3(pPlayer.getX(), pPlayer.getY() - 30, pPlayer.getZ()));
            pPlayer.level().addFreshEntity(lightningEntity);
        }
    }
    private void summonThing2(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            StoneEntity stoneEntity = new StoneEntity(pPlayer.level(), pPlayer);
            Vec3 lookVec = pPlayer.getLookAngle();
            stoneEntity.setDeltaMovement(lookVec.x, lookVec.y, lookVec.z);
            stoneEntity.setStoneXRot(5);
            stoneEntity.setStoneYRot(5);
            stoneEntity.teleportTo(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
            pPlayer.level().addFreshEntity(stoneEntity);
        }
    }
}
