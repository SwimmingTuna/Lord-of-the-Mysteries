package net.swimmingtuna.lotm.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.ConfigValue<SpiritualityBarOverlay.Display> SPIRITUALITY_BAR_DISPLAY;
    public static final ForgeConfigSpec.ConfigValue<Integer> SPIRITUALITY_BAR_Y_OFFSET;
    public static final ForgeConfigSpec.ConfigValue<Integer> SPIRITUALITY_BAR_X_OFFSET;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SPIRITUALITY_BAR_TEXT_VISIBLE;
    public static final ForgeConfigSpec.ConfigValue<SpiritualityBarOverlay.Anchor> SPIRITUALITY_BAR_ANCHOR;
    public static final ForgeConfigSpec SPEC;

    static {
        SPIRITUALITY_BAR_DISPLAY = BUILDER.defineEnum("spiritualityBarDisplay", SpiritualityBarOverlay.Display.CONTEXTUAL);
        SPIRITUALITY_BAR_Y_OFFSET = BUILDER.define("manaBarYOffset", 0);
        SPIRITUALITY_BAR_X_OFFSET = BUILDER.define("manaBarXOffset", 0);
        SPIRITUALITY_BAR_TEXT_VISIBLE = BUILDER.define("manaBarTextVisible", true);
        SPIRITUALITY_BAR_ANCHOR = BUILDER.defineEnum("manaBarAnchor", SpiritualityBarOverlay.Anchor.HUNGER);
        SPEC = BUILDER.build();
    }
}
