package net.swimmingtuna.lotm.init;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class CustomRenderType extends RenderType {

    public CustomRenderType(String name, VertexFormat format, VertexFormat.Mode drawMode, int bufferSize, boolean useDelegate, boolean needsSorting, Runnable setupTask, Runnable clearTask) {
        super(name, format, drawMode, bufferSize, useDelegate, needsSorting, setupTask, clearTask);
    }

    public static RenderType getLightning() {
        return create("lightning_glow",
                DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256,
                true, true,
                RenderType.CompositeState.builder()
                        .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                        .setTextureState(NO_TEXTURE)
                        .setTransparencyState(LIGHTNING_TRANSPARENCY)
                        .setOutputState(TRANSLUCENT_TARGET)
                        .setWriteMaskState(COLOR_WRITE)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                        .createCompositeState(true));
    }
}

