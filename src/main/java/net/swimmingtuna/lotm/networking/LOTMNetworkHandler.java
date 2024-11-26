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

        INSTANCE.messageBuilder(LuckManipulationLeftClickC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(LuckManipulationLeftClickC2S::new)
                .encoder(LuckManipulationLeftClickC2S::toByte)
                .consumerMainThread(LuckManipulationLeftClickC2S::handle)
                .add();
        INSTANCE.messageBuilder(MonsterLeftClickC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(MonsterLeftClickC2S::new)
                .encoder(MonsterLeftClickC2S::toByte)
                .consumerMainThread(MonsterLeftClickC2S::handle)
                .add();
        INSTANCE.messageBuilder(AddItemInInventoryC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AddItemInInventoryC2S::new)
                .encoder(AddItemInInventoryC2S::toByte)
                .consumerMainThread(AddItemInInventoryC2S::handle)
                .add();
        INSTANCE.messageBuilder(LeftClickC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(LeftClickC2S::new)
                .encoder(LeftClickC2S::toByte)
                .consumerMainThread(LeftClickC2S::handle)
                .add();
        INSTANCE.messageBuilder(UpdateItemInHandC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(UpdateItemInHandC2S::new)
                .encoder(UpdateItemInHandC2S::toByte)
                .consumerMainThread(UpdateItemInHandC2S::handle)
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
        INSTANCE.messageBuilder(SpiritWorldTraversalC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SpiritWorldTraversalC2S::new)
                .encoder(SpiritWorldTraversalC2S::toByte)
                .consumerMainThread(SpiritWorldTraversalC2S::handle)
                .add();
        INSTANCE.messageBuilder(MisfortuneManipulationLeftClickC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(MisfortuneManipulationLeftClickC2S::new)
                .encoder(MisfortuneManipulationLeftClickC2S::toByte)
                .consumerMainThread(MisfortuneManipulationLeftClickC2S::handle)
                .add();
        INSTANCE.messageBuilder(MonsterCalamityIncarnationLeftClickC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(MonsterCalamityIncarnationLeftClickC2S::new)
                .encoder(MonsterCalamityIncarnationLeftClickC2S::toByte)
                .consumerMainThread(MonsterCalamityIncarnationLeftClickC2S::handle)
                .add();
        INSTANCE.messageBuilder(FalseProphecyLeftClickC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(FalseProphecyLeftClickC2S::new)
                .encoder(FalseProphecyLeftClickC2S::toByte)
                .consumerMainThread(FalseProphecyLeftClickC2S::handle)
                .add();
        INSTANCE.messageBuilder(NonVisibleS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(NonVisibleS2C::decode)
                .encoder(NonVisibleS2C::encode)
                .consumerMainThread(NonVisibleS2C::handle)
                .add();
        INSTANCE.messageBuilder(SyncSequencePacketS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncSequencePacketS2C::new)
                .encoder(SyncSequencePacketS2C::encode)
                .consumerMainThread(SyncSequencePacketS2C::handle)
                .add();
        INSTANCE.messageBuilder(SendParticleS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SendParticleS2C::new)
                .encoder(SendParticleS2C::encode)
                .consumerMainThread(SendParticleS2C::handle)
                .add();
    }


    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message); //also got no clue lmao
    }
}

