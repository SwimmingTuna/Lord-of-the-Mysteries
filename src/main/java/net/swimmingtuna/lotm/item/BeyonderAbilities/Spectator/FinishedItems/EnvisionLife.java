package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class EnvisionLife extends SimpleAbilityItem {

    public EnvisionLife(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 0, 0, 0);
    }
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("While holding this item, type in a mob and that mob will be spawned, targeting the nearest player within 100 blocks\n" +
                "In the case of Modded Mobs, type in the Mod ID followed by the mob name, e.g. (lotm:black_panther\n" +
                "Spirituality Used: 1500\n" +
                "Left Click for Envision Location\n" +
                "Cooldown: 0 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }


    public static void spawnMob(Player player, String mobName) {
        Level level = player.level();
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        // Create resource location and check if it exists in registry
        ResourceLocation resourceLocation = new ResourceLocation(mobName);
        if (!ForgeRegistries.ENTITY_TYPES.containsKey(resourceLocation)) {
            player.sendSystemMessage(Component.literal("Invalid mob name: " + mobName));
            return;
        }

        EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(resourceLocation);

        int waitMakeLifeCounter = player.getPersistentData().getInt("waitMakeLifeTimer");
        if (waitMakeLifeCounter == 0) {
            Entity entity = entityType.create(level);
            if (entity != null && entity instanceof Mob mob) {
                entity.setPos(x, y, z);
                Player nearestPlayer = findNearestPlayer(level, x, y, z, 100, player);
                if (nearestPlayer != null) {
                    mob.setLastHurtByPlayer(nearestPlayer);
                }

                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                if (holder.getSpirituality() >= mob.getMaxHealth() * 3) {
                    holder.useSpirituality((int) (mob.getMaxHealth() * 3));
                    level.addFreshEntity(entity);

                    // Get the translated name of the entity
                    String entityName = entity.getType().getDescription().getString();
                    player.displayClientMessage(Component.literal("Envisioned a " + entityName + " into the world"), true);
                } else {
                    // Get the translated name for the error message too
                    String entityName = entity.getType().getDescription().getString();
                    player.sendSystemMessage(Component.literal("You need " + (mob.getMaxHealth() * 3 - holder.getSpirituality()) + " more spirituality in order to envision " + entityName));
                }
            }
        } else {
            player.sendSystemMessage(Component.literal("Ability on Cooldown for " + (400 - waitMakeLifeCounter) / 20 + " seconds"));
        }
    }

    @Nullable
    private static Player findNearestPlayer(Level world, double x, double y, double z, double range, Player excludedPlayer) {
        List<Player> players = world.getEntitiesOfClass(Player.class, excludedPlayer.getBoundingBox().inflate(range), player -> player != excludedPlayer);
        if (players.isEmpty()) {
            return null;
        }


        players.sort(Comparator.comparing(player -> player.distanceToSqr(x, y, z)));

        return players.get(0);
    }
}