package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.world.worldgen.dimension.DimensionInit;

import java.util.function.Supplier;

public class SpiritWorldTraversalC2S {
    public SpiritWorldTraversalC2S() {

    }

    public SpiritWorldTraversalC2S(FriendlyByteBuf buf) {

    }

    public void toByte(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer pPlayer = context.getSender();
        context.enqueueWork(() -> {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(pPlayer);
            MinecraftServer server = pPlayer.getServer();
            if (server != null) {
                if (pPlayer.level().dimension() == Level.OVERWORLD) {
                    ServerLevel spiritWorld = server.getLevel(DimensionInit.SPIRIT_WORLD_LEVEL_KEY);
                    if ((holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && holder.getCurrentSequence() <= 4) || (holder.currentClassMatches(BeyonderClassInit.SAILOR) && holder.getCurrentSequence() <= 3) || (holder.currentClassMatches(BeyonderClassInit.MONSTER) && holder.getCurrentSequence() <= 5)) {
                        if (spiritWorld != null) {
                            pPlayer.sendSystemMessage(Component.literal("Transporting to Spirit World...").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));
                            pPlayer.teleportTo(spiritWorld,
                                    pPlayer.getX(),
                                    pPlayer.getY(),
                                    pPlayer.getZ(),
                                    pPlayer.getYRot(),
                                    pPlayer.getXRot());
                        }
                    } else if (pPlayer.level().dimension() == DimensionInit.SPIRIT_WORLD_LEVEL_KEY) {
                        ServerLevel overworldWorld = server.getLevel(Level.OVERWORLD);
                        if (overworldWorld != null) {
                            pPlayer.sendSystemMessage(Component.literal("Transporting to Overworld...").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GREEN));
                            pPlayer.teleportTo(overworldWorld,
                                    pPlayer.getX(), // Convert back to overworld coordinates
                                    pPlayer.getY(),
                                    pPlayer.getZ(),
                                    pPlayer.getYRot(),
                                    pPlayer.getXRot());
                        }
                    }
                }
            }
        });
        return true;
    }
}
