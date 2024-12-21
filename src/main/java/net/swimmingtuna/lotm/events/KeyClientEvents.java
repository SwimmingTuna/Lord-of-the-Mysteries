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
import net.swimmingtuna.lotm.networking.packet.SpiritWorldTraversalC2S;
import net.swimmingtuna.lotm.util.ClientSequenceData;
import net.swimmingtuna.lotm.util.KeyBinding;
import net.swimmingtuna.lotm.world.worldgen.dimension.DimensionInit;

public class KeyClientEvents {
    @Mod.EventBusSubscriber(modid = LOTM.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
           if (KeyBinding.SPIRIT_VISION.consumeClick()) {
               LOTMNetworkHandler.sendToServer(new SpiritVisionC2S());
           }
           if (KeyBinding.SPIRIT_WORLD_TRAVERSAL.consumeClick()) {
               System.out.println("Worked");
               LOTMNetworkHandler.sendToServer(new SpiritWorldTraversalC2S());
           }
        }

        @SubscribeEvent
        public static void onFogDensityEvent(ViewportEvent.RenderFog event) {
            Player player = Minecraft.getInstance().player;
            if (player.level().dimension().equals(DimensionInit.SPIRIT_WORLD_LEVEL_KEY)) {
                event.setFogShape(FogShape.SPHERE);
                int currentSequence = ClientSequenceData.getCurrentSequence();
                if (currentSequence == 0) {
                    event.setFarPlaneDistance(999);
                    event.setNearPlaneDistance(999);
                } else if (currentSequence != -1) {
                    int far = 100 - Math.max(10, currentSequence * 10);
                    int near = 97 - Math.max(7, currentSequence * 10);
                    event.setFarPlaneDistance(far);
                    event.setNearPlaneDistance(near);
                } else {
                    event.setFarPlaneDistance(6);
                    event.setNearPlaneDistance(4);
                }
                event.setCanceled(true);
            }
        }
    }
    @Mod.EventBusSubscriber(modid = LOTM.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBinding.SPIRIT_VISION);
            event.register(KeyBinding.SPIRIT_WORLD_TRAVERSAL);
        }
    }
}
