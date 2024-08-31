package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnvisionLife extends Item {

    public EnvisionLife(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("While holding this item, type in a mob and that mob will be spawned, targeting the nearest player within 100 blocks\n" +
                    "In the case of Modded Mobs, type in the Mod ID followed by the mob name, e.g. (lotm:black_panter\n" +
                    "Spirituality Used: 1500\n" +
                    "Left Click for Envision Location\n" +
                    "Cooldown: 0 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    @SubscribeEvent
    public static void onChatMessage(ServerChatEvent event) {
        Level level = event.getPlayer().serverLevel();
        Player pPlayer = event.getPlayer();
        AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
        String message = event.getMessage().getString().toLowerCase();
        if (pPlayer.getMainHandItem().getItem() instanceof EnvisionLife && !pPlayer.level().isClientSide()) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
                if (!holder.isSpectatorClass()) {
                    pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
                }

                if (holder.getSpirituality() < 1500) {
                    pPlayer.displayClientMessage(Component.literal("You need " + ((int) 1500 / dreamIntoReality.getValue()) + " spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
                }
        }
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (holder.isSpectatorClass() && !pPlayer.level().isClientSide() && pPlayer.getMainHandItem().getItem() instanceof EnvisionLife && spectatorSequence.getCurrentSequence() == 0) {
                    spawnMob(pPlayer,message);
                spectatorSequence.useSpirituality((int) (1500/dreamIntoReality.getValue()));
                event.setCanceled(true);
            }
        });
    }

    private static void spawnMob(Player pPlayer, String mobName) {
        // Get the world level and position of the player
        Level level = pPlayer.level();
        double x = pPlayer.getX();
        double y = pPlayer.getY();
        double z = pPlayer.getZ();

        // Find the EntityType based on the mobName (assuming it's a valid EntityType)
        EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(mobName));

        int waitMakeLifeCounter = pPlayer.getPersistentData().getInt("waitMakeLifeTimer");
        if (waitMakeLifeCounter == 0) {
            if (entityType != null) {
                Entity entity = entityType.create(level);
                if (entity != null) {
                    Mob mob = (Mob) entity;
                    entity.setPos(x, y, z);
                    Player nearestPlayer = findNearestPlayer(level, x, y, z, 100, pPlayer);
                    if (nearestPlayer != null) {
                        mob.setLastHurtByPlayer(nearestPlayer);
                    }
                    BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
                    if (holder.getCurrentSequence() >= ((Mob) entity).getMaxHealth() * 3) {
                        holder.useSpirituality((int) (((Mob) entity).getMaxHealth() * 3));
                        level.addFreshEntity(entity);
                    } else {
                        pPlayer.sendSystemMessage(Component.literal("You need " + (((Mob) entity).getMaxHealth() * 3 - holder.getSpirituality()) + " more spirituality in order to envision" + entity.getName().getString()));
                    }
                }
            }
        }
        if (waitMakeLifeCounter != 0) {
            pPlayer.sendSystemMessage(Component.literal("Ability on Cooldown for " + (int) ((400 - waitMakeLifeCounter)/20) + " seconds"));
        }
        else {
            pPlayer.sendSystemMessage(Component.literal("Mob not valid"));
        }
    }
    private static Player findNearestPlayer(Level world, double x, double y, double z, double range, Player excludedPlayer) {
        List<Player> players = world.getEntitiesOfClass(Player.class, excludedPlayer.getBoundingBox().inflate(range, range, range), player -> player != excludedPlayer);
        if (players.isEmpty()) {
            return null;
        }

        players.sort((p1, p2) -> {
            double d1 = p1.distanceToSqr(x, y, z);
            double d2 = p2.distanceToSqr(x, y, z);
            return Double.compare(d1, d2);
        });

        return players.get(0);
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof EnvisionLife) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.EnvisionKingdom.get()));
            heldItem.shrink(1);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof EnvisionLife) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.EnvisionKingdom.get()));
            heldItem.shrink(1);
        }
    }
}