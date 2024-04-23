package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProphesizeDemise extends Item implements ReachChangeUUIDs {
    private final LazyOptional<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = LazyOptional.of(() -> createAttributeMap());

    public ProphesizeDemise(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pSlot) {
        if (pSlot == EquipmentSlot.MAINHAND) {
            return lazyAttributeMap.orElseGet(() -> createAttributeMap());
        }
        return super.getDefaultAttributeModifiers(pSlot);
    }

    private Multimap<Attribute, AttributeModifier> createAttributeMap() {

        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder = ImmutableMultimap.builder();
        attributeBuilder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(BeyonderEntityReach, "Reach modifier", 400, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(BeyonderBlockReach, "Reach modifier", 400, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use on a living entity, prophesizes their demise, making it so they have to stay still for 10 seconds out of 30 seconds, otherwise they die\n" +
                    "Left Click for Prophesize Teleport Block" +
                    "Spirituality Used: 70\n" +
                    "Cooldown: 2 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player pPlayer = event.getEntity();
        if (!pPlayer.level().isClientSide() && pPlayer.getMainHandItem().getItem() instanceof ProphesizeDemise) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSpectatorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 1000) {
                pPlayer.displayClientMessage(Component.literal("You need 1000 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
        }        ItemStack itemStack = pPlayer.getItemInHand(event.getHand());
        Entity targetEntity = event.getTarget();
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (holder.isSpectatorClass() && !pPlayer.level().isClientSide && !targetEntity.level().isClientSide && itemStack.getItem() instanceof ProphesizeDemise && targetEntity instanceof LivingEntity && spectatorSequence.getCurrentSequence() <= 1 && spectatorSequence.useSpirituality(1000)) {
                ((LivingEntity) targetEntity).addEffect(new MobEffectInstance(ModEffects.SPECTATORDEMISE.get(), 600, 1, false, false));
                if (!pPlayer.getAbilities().instabuild) {
                    AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
                    pPlayer.getCooldowns().addCooldown(itemStack.getItem(), (int) (3000 / dreamIntoReality.getValue()));
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        });
    }


    @SubscribeEvent
    public static void EntityDemise(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            CompoundTag persistentTag = entity.getPersistentData();
            double prevX = persistentTag.getDouble("prevX");
            double prevY = persistentTag.getDouble("prevY");
            double prevZ = persistentTag.getDouble("prevZ");
            double currentX = persistentTag.getDouble("currentX");
            double currentY = persistentTag.getDouble("currentY");
            double currentZ = persistentTag.getDouble("currentZ");
            int tickCounter = persistentTag.getInt("tickCounter");
            boolean hasSpectatorDemise = entity.hasEffect(ModEffects.SPECTATORDEMISE.get());
            int messageCounter = persistentTag.getInt("MessageCounter");
            if (!hasSpectatorDemise) {
                int demise = persistentTag.getInt("EntityDemise");
                demise = 0;
                persistentTag.putInt("EntityDemise", 0);
                messageCounter = 0;
                persistentTag.putInt("MessageCounter", 0);
                int nonDemise = persistentTag.getInt("NonDemise");
                nonDemise = 0;
                persistentTag.putInt("NonDemise",0);
            }
            if (hasSpectatorDemise) {
                MobEffectInstance demiseEffect = entity.getEffect(ModEffects.SPECTATORDEMISE.get());
                if (demiseEffect != null) {
                    int effectDuration = demiseEffect.getDuration();
                    int effectDurationSeconds;
                    if (effectDuration < 20) {
                        effectDurationSeconds = 1;
                    } else {
                        effectDurationSeconds = (effectDuration + 19) / 20;
                    }
                    if (hasSpectatorDemise) {

                        int demise = persistentTag.getInt("EntityDemise");
                        int nonDemise = persistentTag.getInt("NonDemise");

                        int nonDemiseSeconds = (nonDemise + 19) / 20;
                        if (tickCounter == 0) {
                            prevX = entity.getX();
                            persistentTag.putDouble("prevX", prevX);

                            prevY = entity.getY();
                            persistentTag.putDouble("prevY", prevY);

                            prevZ = entity.getZ();
                            persistentTag.putDouble("prevZ", prevZ);

                            persistentTag.putInt("tickCounter", 1);
                        } else if (tickCounter == 1) {
                            currentX = entity.getX();
                            persistentTag.putDouble("currentX", currentX);

                            currentY = entity.getY();
                            persistentTag.putDouble("currentY", currentY);

                            currentZ = entity.getZ();
                            persistentTag.putDouble("currentZ", currentZ);

                            persistentTag.putInt("tickCounter", 0);
                        }
                        if (Math.abs(prevX - currentX) > 0.006 || Math.abs(prevY - currentY) > 0.006 || Math.abs(prevZ - currentZ) > 0.006) {
                            demise++;
                            persistentTag.putInt("EntityDemise", demise);
                         } else { nonDemise++;
                        persistentTag.putInt("NonDemise", nonDemise);}
                        if (demise == 400) {
                            entity.kill();
                            messageCounter = 0;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            nonDemise = 0;
                            persistentTag.putInt("NonDemise", nonDemise);
                        }
                        if (nonDemise > 200) {
                            demise = 0;
                            persistentTag.putInt("EntityDemise", demise);
                            entity.removeEffect(ModEffects.SPECTATORDEMISE.get());
                            nonDemise = 0;
                            persistentTag.putInt("NonDemise", nonDemise);
                            messageCounter = 0;
                            persistentTag.putInt("MessageCounter", messageCounter);
                        }
                        if (nonDemise == 200) {
                            demise = 0;
                            persistentTag.putInt("EntityDemise", demise);
                            entity.removeEffect(ModEffects.SPECTATORDEMISE.get());
                             entity.sendSystemMessage(Component.literal("You survived your fate").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                            nonDemise = 0;
                            persistentTag.putInt("NonDemise", nonDemise);
                            messageCounter = 0;
                            persistentTag.putInt("MessageCounter", messageCounter);
                        }
                        if (demise == 20 && messageCounter == 0) {
                            messageCounter = 1;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 19 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 40 && messageCounter == 1) {
                            messageCounter = 2;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 18 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 60 && messageCounter == 2) {
                            messageCounter = 3;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 17 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 80 && messageCounter == 3) {
                            messageCounter = 4;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 16 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 100 && messageCounter == 4) {
                            messageCounter = 5;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 15 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 120 && messageCounter == 5) {
                            messageCounter = 6;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 14 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 140 && messageCounter == 6) {
                            messageCounter = 7;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 13 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 160 && messageCounter == 7) {
                            messageCounter = 8;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 12 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 180 && messageCounter == 8) {
                            messageCounter = 9;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 11 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 200 && messageCounter == 9) {
                            messageCounter = 10;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 10 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 220 && messageCounter == 10) {
                            messageCounter = 11;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 9 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 240 && messageCounter == 11) {
                            messageCounter = 12;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 8 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 260 && messageCounter == 12) {
                            messageCounter = 13;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 7 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 280 && messageCounter == 13) {
                            messageCounter = 14;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 6 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 300 && messageCounter == 14) {
                            messageCounter = 15;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 5 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 320 && messageCounter == 15) {
                            messageCounter = 16;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 4 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 340 && messageCounter == 16) {
                            messageCounter = 17;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 3 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 360 && messageCounter == 17) {
                            messageCounter = 18;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 2 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (demise == 380 && messageCounter == 18) {
                            messageCounter = 19;
                            persistentTag.putInt("MessageCounter", messageCounter);
                            entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 1 second, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 20) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 9 more seconds").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 40) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 8 more seconds").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 60) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 7 more seconds").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 80) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 6 more seconds").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 100) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 5 more seconds").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 120) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 4 more seconds").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 140) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 3 more seconds").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 160) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 2 more seconds").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                        if (nonDemise == 180) {
                            entity.sendSystemMessage(Component.literal("You need to stand still for 1 more second").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                        }
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public static void handlePlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            CompoundTag persistentData = player.getPersistentData();

            if (persistentData.contains("DemiseCounter")) {
                int demiseCounter = persistentData.getInt("DemiseCounter");

                if (!persistentData.contains("EntityDemise") || persistentData.getInt("EntityDemise") == 0) {
                    player.getPersistentData().putInt("EntityDemise", demiseCounter);
                }
            } else {
                if (!persistentData.contains("EntityDemise") || persistentData.getInt("EntityDemise") == 0) {

                    player.getPersistentData().putInt("EntityDemise", 0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void handlePlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            CompoundTag persistentData = player.getPersistentData();

            // Check if the persistent data contains the "DemiseCounter" key
            if (persistentData.contains("DemiseCounter")) {
                // Retrieve the demise counter value from persistent data
                int demiseCounter = persistentData.getInt("DemiseCounter");

                // Check if the "EntityDemise" key doesn't exist or its value is 0
                if (!persistentData.contains("EntityDemise") || persistentData.getInt("EntityDemise") == 0) {
                    // Update the demise counter for the player
                    player.getPersistentData().putInt("EntityDemise", demiseCounter);
                }
            } else {
                // If the persistent data doesn't contain the "DemiseCounter" key,
                // check if the "EntityDemise" key doesn't exist or its value is 0
                if (!persistentData.contains("EntityDemise") || persistentData.getInt("EntityDemise") == 0) {
                    // Initialize the demise counter to 0
                    player.getPersistentData().putInt("EntityDemise", 0);
                }
            }
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof ProphesizeDemise) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.ProphesizeTeleportBlock.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof ProphesizeDemise) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.ProphesizeTeleportBlock.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
}