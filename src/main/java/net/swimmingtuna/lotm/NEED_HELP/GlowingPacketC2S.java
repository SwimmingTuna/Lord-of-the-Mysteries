package net.swimmingtuna.lotm.NEED_HELP;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.util.effect.ModEffects;

import java.util.function.Supplier;

public class GlowingPacketC2S {

    public GlowingPacketC2S() {

    }

    public GlowingPacketC2S(FriendlyByteBuf buf) {

    }
    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer pPlayer = context.getSender();
            ServerLevel level = (ServerLevel) pPlayer.level();
            boolean canSeeGlowingEffect = hasGlowingEffectTag(pPlayer);
            if (canSeeGlowingEffect) {
                for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(20))) {
                    if (entity.hasEffect(ModEffects.LOTMGLOWING.get())) {
                        entity.setGlowingTag(true);
                    } else {
                        if (!entity.hasEffect(MobEffects.GLOWING)) {
                            entity.setGlowingTag(false);
                        }
                    }
                }
            }
        });
        return true;
    }
    private boolean hasGlowingEffectTag(ServerPlayer pPlayer) {
        CompoundTag tag = pPlayer.getPersistentData();
        return tag.getInt("windGlowing") > 1;
    }
}
