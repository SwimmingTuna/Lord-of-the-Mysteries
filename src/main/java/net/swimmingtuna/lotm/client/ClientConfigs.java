package net.swimmingtuna.lotm.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.swimmingtuna.lotm.util.PlayerMobs.ItemManager;
import net.swimmingtuna.lotm.util.PlayerMobs.NameManager;
import net.swimmingtuna.lotm.util.PlayerMobs.ThreadUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.ConfigValue<SpiritualityBarOverlay.Display> SPIRITUALITY_BAR_DISPLAY;
    public static final ForgeConfigSpec.ConfigValue<Integer> SPIRITUALITY_BAR_Y_OFFSET;
    public static final ForgeConfigSpec.ConfigValue<Integer> SPIRITUALITY_BAR_X_OFFSET;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SPIRITUALITY_BAR_TEXT_VISIBLE;
    public static final ForgeConfigSpec.ConfigValue<SpiritualityBarOverlay.Anchor> SPIRITUALITY_BAR_ANCHOR;
    public static final ForgeConfigSpec SPEC;

    static {
        SPIRITUALITY_BAR_DISPLAY = BUILDER.defineEnum("spiritualityBarDisplay", SpiritualityBarOverlay.Display.Contextual);
        SPIRITUALITY_BAR_Y_OFFSET = BUILDER.define("manaBarYOffset", 0);
        SPIRITUALITY_BAR_X_OFFSET = BUILDER.define("manaBarXOffset", 0);
        SPIRITUALITY_BAR_TEXT_VISIBLE = BUILDER.define("manaBarTextVisible", true);
        SPIRITUALITY_BAR_ANCHOR = BUILDER.defineEnum("manaBarAnchor", SpiritualityBarOverlay.Anchor.Hunger);
        SPEC = BUILDER.build();
    }
}
