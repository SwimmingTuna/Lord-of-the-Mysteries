package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.entity.EndStoneEntity;
import net.swimmingtuna.lotm.entity.NetherrackEntity;
import net.swimmingtuna.lotm.entity.StoneEntity;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.BeyonderAbilityUser;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.LightningStorm;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.MatterAccelerationBlocks;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import java.util.Comparator;
import java.util.function.Supplier;

public class MatterAccelerationBlockC2S {
    public MatterAccelerationBlockC2S() {

    }

    public MatterAccelerationBlockC2S(FriendlyByteBuf buf) {

    }

    public void toByte(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer pPlayer = context.getSender();
        context.enqueueWork(() -> {
           int x = pPlayer.getPersistentData().getInt("matterAccelerationBlockTimer");
           if (x >= 1) {
               Vec3 lookDirection = pPlayer.getLookAngle().normalize().scale(20);
               if (pPlayer.level().dimension() == Level.OVERWORLD) {
                   StoneEntity stoneEntity = pPlayer.level().getEntitiesOfClass(StoneEntity.class, pPlayer.getBoundingBox().inflate(10))
                           .stream()
                           .min(Comparator.comparingDouble(zombie -> zombie.distanceTo(pPlayer)))
                           .orElse(null);
                   if (stoneEntity != null) {
                       stoneEntity.setDeltaMovement(lookDirection);
                       stoneEntity.setSent(true);
                       stoneEntity.setShouldntDamage(false);
                       stoneEntity.setTickCount(440);
                   }
                   if (stoneEntity == null) {
                       pPlayer.getPersistentData().putInt("matterAccelerationBlockTimer", 0);
                   }
               }
               if (pPlayer.level().dimension() == Level.NETHER) {
                   NetherrackEntity netherrackEntity = pPlayer.level().getEntitiesOfClass(NetherrackEntity.class, pPlayer.getBoundingBox().inflate(10))
                           .stream()
                           .min(Comparator.comparingDouble(zombie -> zombie.distanceTo(pPlayer)))
                           .orElse(null);
                   if (netherrackEntity != null) {
                       netherrackEntity.setDeltaMovement(lookDirection);
                       netherrackEntity.setSent(true);
                       netherrackEntity.setShouldntDamage(false);
                       netherrackEntity.setTickCount(440);
                   }
                   if (netherrackEntity == null) {
                       pPlayer.getPersistentData().putInt("matterAccelerationBlockTimer", 0);
                   }
               }
               if (pPlayer.level().dimension() == Level.END) {
                   EndStoneEntity endStoneEntity = pPlayer.level().getEntitiesOfClass(EndStoneEntity.class, pPlayer.getBoundingBox().inflate(10))
                           .stream()
                           .min(Comparator.comparingDouble(zombie -> zombie.distanceTo(pPlayer)))
                           .orElse(null);
                   if (endStoneEntity != null) {
                       endStoneEntity.setDeltaMovement(lookDirection);
                       endStoneEntity.setSent(true);
                       endStoneEntity.setShouldntDamage(false);
                       endStoneEntity.setTickCount(440);
                   }
                   if (endStoneEntity == null) {
                       pPlayer.getPersistentData().putInt("matterAccelerationBlockTimer", 0);
                   }
               }
           } else {
               int activeSlot = pPlayer.getInventory().selected;
               ItemStack heldItem = pPlayer.getMainHandItem();
               if (!heldItem.isEmpty() && heldItem.getItem() instanceof MatterAccelerationBlocks) {
                   pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.MatterAccelerationSelf.get()));
                   heldItem.shrink(1);
               }
           }
        });
        return true;
    }
}
