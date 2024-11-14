package net.swimmingtuna.lotm.events;

import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.SpiritVisionC2S;
import net.swimmingtuna.lotm.util.KeyBinding;
import net.swimmingtuna.lotm.worldgen.dimension.DimensionInit;

public class KeyClientEvents {
    @Mod.EventBusSubscriber(modid = LOTM.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
           if (KeyBinding.SPIRIT_VISION.consumeClick()) {
               LOTMNetworkHandler.sendToServer(new SpiritVisionC2S());
           }
        }

        @SubscribeEvent
        public static void onFogDensityEvent(ViewportEvent.RenderFog event) {
            Player player = Minecraft.getInstance().player;
            if (player.level().dimension().equals(DimensionInit.SPIRIT_WORLD_LEVEL_KEY)) {
                event.setFogShape(FogShape.SPHERE);
                event.setFarPlaneDistance(10.0f);
                event.setNearPlaneDistance(7.0f);
                event.setCanceled(true);
            }
        }
    }
    @Mod.EventBusSubscriber(modid = LOTM.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBinding.SPIRIT_VISION);
        }
    }
}
