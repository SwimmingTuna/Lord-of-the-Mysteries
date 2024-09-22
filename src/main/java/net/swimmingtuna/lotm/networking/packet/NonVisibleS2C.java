package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class NonVisibleS2C {

    public NonVisibleS2C() {}

    // Serializer
    public static void encode(NonVisibleS2C msg, FriendlyByteBuf buf) {
        // No data to write
    }

    // Deserializer
    public static NonVisibleS2C decode(FriendlyByteBuf buf) {
        return new NonVisibleS2C();
    }

    public static void handle(NonVisibleS2C msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0)); // 10 seconds of invisibility
                System.out.println("packet worked");
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
