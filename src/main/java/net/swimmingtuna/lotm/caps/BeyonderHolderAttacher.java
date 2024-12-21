package net.swimmingtuna.lotm.caps;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.util.CapabilitySyncer.core.CapabilityAttacher;
import net.swimmingtuna.lotm.util.CapabilitySyncer.network.SimpleEntityCapabilityStatusPacket;
import org.jetbrains.annotations.NotNull;

public class BeyonderHolderAttacher extends CapabilityAttacher {
    public static final Capability<BeyonderHolder> CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(LOTM.MOD_ID, "beyonder_class");
    private static final Class<BeyonderHolder> CAPABILITY_CLASS = BeyonderHolder.class;

    @NotNull
    public static BeyonderHolder getHolderUnwrap(Player player) {
        LazyOptional<BeyonderHolder> holder = getHolder(player);
        if (holder.isPresent()) {
            return holder.resolve().get();
        } else {
            return new BeyonderHolder(player);
        }
    }

    public static LazyOptional<BeyonderHolder> getHolder(Player player) {
        return player.getCapability(CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<Entity> event, Player entity) {
        genericAttachCapability(event, new BeyonderHolder(entity), CAPABILITY, RESOURCE_LOCATION);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerPlayerAttacher(BeyonderHolderAttacher::attach, BeyonderHolderAttacher::getHolder, true);
        SimpleEntityCapabilityStatusPacket.registerRetriever(RESOURCE_LOCATION, BeyonderHolderAttacher::getHolderUnwrap);
    }

}
