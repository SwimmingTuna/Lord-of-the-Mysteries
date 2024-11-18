package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.blocks.MonsterDomainBlockEntity;
import net.swimmingtuna.lotm.item.BeyonderAbilities.BeyonderAbilityUser;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.DomainOfDecay;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.DomainOfProvidence;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.MonsterDomainTeleporation;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.Hurricane;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.LightningStorm;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import java.util.List;
import java.util.function.Supplier;

public class MonsterLeftClickC2S {
    public MonsterLeftClickC2S() {

    }

    public MonsterLeftClickC2S(FriendlyByteBuf buf) {

    }

    public void toByte(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (player == null) return;
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.getItem() instanceof MonsterDomainTeleporation) {
                List<MonsterDomainBlockEntity> ownedDomains = MonsterDomainBlockEntity.getDomainsOwnedBy(player.level(), player);
                if (ownedDomains.isEmpty()) {
                    player.displayClientMessage(Component.literal("You don't own any Domains!")
                            .withStyle(ChatFormatting.RED), true);
                    return;
                }
                CompoundTag tag = heldItem.getOrCreateTag();
                int currentIndex = tag.getInt("CurrentDomainIndex");
                currentIndex = (currentIndex + 1) % ownedDomains.size();
                tag.putInt("CurrentDomainIndex", currentIndex);
                MonsterDomainBlockEntity selectedDomain = ownedDomains.get(currentIndex);
                BlockPos pos = selectedDomain.getBlockPos();
                player.displayClientMessage(Component.literal("Selected Domain " + (currentIndex + 1) + " of " + ownedDomains.size()
                                + " at: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ())
                        .withStyle(BeyonderUtil.getStyle(player)), true);

            }
        });
        return true;
    }
}
