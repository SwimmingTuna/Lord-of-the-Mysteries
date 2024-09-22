package net.swimmingtuna.lotm.networking;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.networking.packet.*;
import net.swimmingtuna.lotm.util.CapabilitySyncer.network.SimpleEntityCapabilityStatusPacket;

import java.util.List;
import java.util.function.BiConsumer;

public class LOTMNetworkHandler {
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(LOTM.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int nextId = 0;
    private static int id() {
        return nextId++;
    }

    public static void register() {
        List<BiConsumer<SimpleChannel, Integer>> packets = ImmutableList.<BiConsumer<SimpleChannel, Integer>>builder()
                .add(SimpleEntityCapabilityStatusPacket::register)
                .add(SpiritualityC2S::register)
                .build();
        packets.forEach(consumer -> consumer.accept(INSTANCE, id()));

        INSTANCE.messageBuilder(LeftClickC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(LeftClickC2S::new)
                .encoder(LeftClickC2S::toByte)
                .consumerMainThread(LeftClickC2S::handle)
                .add();
        INSTANCE.messageBuilder(MatterAccelerationBlockC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(MatterAccelerationBlockC2S::new)
                .encoder(MatterAccelerationBlockC2S::toByte)
                .consumerMainThread(MatterAccelerationBlockC2S::handle)
                .add();
        INSTANCE.messageBuilder(SpiritVisionC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SpiritVisionC2S::new)
                .encoder(SpiritVisionC2S::toByte)
                .consumerMainThread(SpiritVisionC2S::handle)
                .add();
        INSTANCE.messageBuilder(NonVisibleS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(NonVisibleS2C::decode)
                .encoder(NonVisibleS2C::encode)
                .consumerMainThread(NonVisibleS2C::handle)
                .add();

    }


    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message); //also got no clue lmao
    }
}

