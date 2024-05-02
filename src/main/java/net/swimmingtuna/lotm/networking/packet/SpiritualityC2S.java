package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import net.swimmingtuna.lotm.util.CapabilitySyncer.network.IPacket;

public class SpiritualityC2S implements IPacket {
    public SpiritualityC2S() {

    }
    public static SpiritualityC2S read(FriendlyByteBuf packetBuf) {
        return new SpiritualityC2S();
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, SpiritualityC2S.class, SpiritualityC2S::read);
    }

    private static boolean hasSolidBlocksAround(ServerPlayer player, ServerLevel level) {
        return level.getBlockStates(player.getBoundingBox().inflate(2))
                .filter(blockState ->
                                !blockState.is(Blocks.AIR) ||
                                !blockState.is(Blocks.CAVE_AIR) ||
                                !blockState.is(Blocks.VOID_AIR)
                                || !blockState.is(Blocks.TRIPWIRE)
                                || !blockState.is(Blocks.TRIPWIRE_HOOK)
                                || !blockState.is(Blocks.LIGHTNING_ROD)).toArray().length > 0;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            ServerLevel level = (ServerLevel) player.level();
            if (hasSolidBlocksAround(player, level) && player.isCrouching()) {
            }});
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {

    }
}
