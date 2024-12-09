package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.PlayerMobEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CycleOfFate extends SimpleAbilityItem {
    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public CycleOfFate(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 9, 100, 10, 35, 35);
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            if (!checkAll(player)) {
                return InteractionResult.FAIL;
            }
            useSpirituality(player);
            addCooldown(player);
            cycleOfFate(interactionTarget, player);
        }
        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.lazyAttributeMap.get();
        }
        return super.getDefaultAttributeModifiers(slot);
    }

    private Multimap<Attribute, AttributeModifier> createAttributeMap() {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder = ImmutableMultimap.builder();
        attributeBuilder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 20, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 20, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use on a living entity, gives you the ability to manipulate them for 30 seconds\n" +
                "Left Click for Manipulate Emotion\n" +
                "Spirituality Used: 50\n" +
                "Cooldown: None").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }


    private static void cycleOfFate(LivingEntity interactionTarget, Player player) {
        if (!player.level().isClientSide() && !interactionTarget.level().isClientSide()) {
            int spirituality = 0;
            int sequence = -1;

            player.getPersistentData().putInt("monsterCycleOfFateUser", 70);
            savePotionEffectsToTag(player, player.getPersistentData());

            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            player.getPersistentData().putInt("monsterCycleOfFateUserX", (int) player.getX());
            player.getPersistentData().putInt("monsterCycleOfFateUserY", (int) player.getY());
            player.getPersistentData().putInt("monsterCycleOfFateUserZ", (int) player.getZ());
            player.getPersistentData().putInt("monsterCycleOfFateUserHealth", (int) player.getHealth());
            player.getPersistentData().putInt("monsterCycleOfFateUserSequence", holder.getCurrentSequence());
            if (interactionTarget instanceof Player pPlayer) {
                spirituality = (int) BeyonderHolderAttacher.getHolderUnwrap(pPlayer).getSpirituality();
                sequence = BeyonderHolderAttacher.getHolderUnwrap(pPlayer).getCurrentSequence();
            }
            if (interactionTarget instanceof PlayerMobEntity playerMobEntity) {
                sequence = playerMobEntity.getCurrentSequence();
                spirituality = playerMobEntity.getSpirituality();
            }
            if (interactionTarget.getPersistentData().getInt("monsterCycleOfFate") == 0) {
                interactionTarget.getPersistentData().putInt("monsterCycleOfFate", 60);
                savePotionEffectsToTag(interactionTarget, interactionTarget.getPersistentData());
                interactionTarget.getPersistentData().putInt("monsterCycleOfFateX", (int) interactionTarget.getX());
                interactionTarget.getPersistentData().putInt("monsterCycleOfFateY", (int) interactionTarget.getY());
                interactionTarget.getPersistentData().putInt("monsterCycleOfFateZ", (int) interactionTarget.getZ());
                interactionTarget.getPersistentData().putUUID("monsterCycleOfFateHolder", player.getUUID());
                interactionTarget.getPersistentData().putInt("monsterCycleOfFateHealth", (int) interactionTarget.getHealth());
                interactionTarget.getPersistentData().putInt("monsterCycleOfFateSpirituality", spirituality);
                interactionTarget.getPersistentData().putInt("monsterCycleOfFateSequence", sequence);
                for (LivingEntity entity : interactionTarget.level().getEntitiesOfClass(LivingEntity.class, interactionTarget.getBoundingBox().inflate(500))) {
                    CompoundTag tag = entity.getPersistentData();
                    if (entity != interactionTarget && entity != player) {
                        int pSpirituality;
                        int pSequence;
                        savePotionEffectsToTag(entity, tag);
                        tag.putUUID("monsterCycleOfFateHolder", player.getUUID());
                        tag.putInt("monsterCycleOfFateEntity", 70);
                        tag.putInt("monsterCycleOfFateEntityX", (int) entity.getX());
                        tag.putInt("monsterCycleOfFateEntityY", (int) entity.getY());
                        tag.putInt("monsterCycleOfFateEntityZ", (int) entity.getZ());
                        tag.putInt("monsterCycleOfFateEntityHealth", (int) entity.getHealth());
                        if (entity instanceof Player pPlayer) {
                            pSpirituality = (int) BeyonderHolderAttacher.getHolderUnwrap(pPlayer).getSpirituality();
                            pSequence = BeyonderHolderAttacher.getHolderUnwrap(pPlayer).getCurrentSequence();
                            tag.putInt("monsterCycleOfFateEntitySpirituality", (int) pSpirituality);
                            tag.putInt("monsterCycleOfFateEntitySequence", (int) pSequence);
                        }
                        if (entity instanceof PlayerMobEntity playerMobEntity) {
                            pSequence = playerMobEntity.getCurrentSequence();
                            pSpirituality = playerMobEntity.getSpirituality();
                            tag.putInt("monsterCycleOfFateEntitySpirituality", (int) pSpirituality);
                            tag.putInt("monsterCycleOfFateEntitySequence", (int) pSequence);
                        }
                    }
                }
            } else {
                interactionTarget.getPersistentData().putInt("monsterCycleOfFate", 1);
                player.getPersistentData().putInt("monsterCycleOfFateUser", 1);
            }
        }
    }

    public static void cycleOfFateDeath(LivingDeathEvent event) { //might need to utilize how I can save world states or smth
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            CompoundTag tag = entity.getPersistentData();
            int cycleCounter = tag.getInt("monsterCycleOfFateEntity");
            int cycleX = tag.getInt("monsterCycleOfFateEntityX");
            int cycleY = tag.getInt("monsterCycleOfFateEntityY");
            int cycleZ = tag.getInt("monsterCycleOfFateEntityZ");
            int cycleSpirituality = tag.getInt("monsterCycleOfFateEntitySpirituality");
            int cycleSequence = tag.getInt("monsterCycleOfFateEntitySequence");
            int cycleHealth = tag.getInt("monsterCycleOfFateEntityHealth");
            if (tag.contains("monsterCycleOfFateHolder")) {
                Player player = entity.level().getPlayerByUUID(tag.getUUID("monsterCycleOfFateHolder"));
                if (cycleCounter >= 1 && tag.getInt("monsterCycleOfFate") == 0 && tag.getInt("monsterCycleOfFateUser") == 0) { //for other entities caught in cycle
                    if (player != null) {
                        tag.putBoolean("monsterCycleOfFateIsDead", true);
                        entity.setHealth(cycleHealth);
                        event.setCanceled(true);
                        entity.teleportTo(cycleX, cycleY + 400, cycleZ);
                        entity.setHealth(cycleHealth);
                        if (entity instanceof Player player1) {
                            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player1);
                            holder.setCurrentSequence(cycleSequence);
                            holder.setSpirituality(cycleSpirituality);
                        }
                        if (entity instanceof PlayerMobEntity playerMobEntity) {
                            playerMobEntity.setSequence(cycleSequence);
                            playerMobEntity.setSpirituality(cycleSpirituality);
                        }
                    } else {
                        tag.putInt("monsterCycleOfFateEntity", 0);
                        tag.putInt("monsterCycleOfFateEntityX", 0);
                        tag.putInt("monsterCycleOfFateEntityY", 0);
                        tag.putInt("monsterCycleOfFateEntityZ", 0);
                        tag.putInt("monsterCycleOfFateEntitySpirituality", 0);
                        tag.putInt("monsterCycleOfFateEntitySequence", 0);
                        tag.putInt("monsterCycleOfFateEntityHealth", 0);
                        tag.putInt("monsterCycleOfFateHolder", 0);
                    }
                }
            }

            if (tag.getInt("monsterCycleOfFateUser") >= 1 && entity instanceof Player pPlayer && BeyonderHolderAttacher.getHolderUnwrap(pPlayer).getSpirituality() >= 1) {
                int userX = tag.getInt("monsterCycleOfFateUserX");
                int userY = tag.getInt("monsterCycleOfFateUserY");
                int userZ = tag.getInt("monsterCycleOfFateUserZ");
                int userHealth = tag.getInt("monsterCycleOfFateUserHealth");
                int userSequence = tag.getInt("monsterCycleOfFateUserSequence");
                for (LivingEntity living : entity.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(800))) {
                    CompoundTag livingTag = living.getPersistentData();
                    int livingCounter = livingTag.getInt("monsterCycleOfFate");
                    if (livingCounter >= 1) {
                        int livingX = livingTag.getInt("monsterCycleOfFateX");
                        int livingY = livingTag.getInt("monsterCycleOfFateY");
                        int livingZ = livingTag.getInt("monsterCycleOfFateZ");
                        int livingHealth = livingTag.getInt("monsterCycleOfFateHealth");
                        int livingSequence = livingTag.getInt("monsterCycleOfFateSequence");
                        int livingSpirituaity = livingTag.getInt("monsterCycleOfFateSpirituality");
                        if (living instanceof Player player1) {
                            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player1);
                            holder.setCurrentSequence(livingSequence);
                            holder.setSpirituality(livingSpirituaity);
                        }
                        if (living instanceof PlayerMobEntity playerMobEntity) {
                            playerMobEntity.setSequence(livingSequence);
                            playerMobEntity.setSpirituality(livingSpirituaity);
                        }
                        restorePotionEffectsFromTag(living, livingTag);
                        living.teleportTo(livingX, livingY, livingZ);
                        living.setHealth(livingHealth);
                        livingTag.putInt("monsterCycleOfFate", 60);
                        tag.putInt("monsterCycleOfFateUser", 70);
                        pPlayer.teleportTo(userX, userY, userZ);
                        restorePotionEffectsFromTag(pPlayer, tag);
                        pPlayer.setHealth(userHealth);
                        event.setCanceled(true);
                        BeyonderHolderAttacher.getHolderUnwrap(pPlayer).setCurrentSequence(userSequence);
                        for (LivingEntity pEntity : living.level().getEntitiesOfClass(LivingEntity.class, living.getBoundingBox().inflate(800))) {
                            if (pEntity != pPlayer && pEntity != living) {
                                CompoundTag pTag = pEntity.getPersistentData();
                                int entityCounter = pTag.getInt("monsterCycleOfFateEntity");
                                if (entityCounter >= 1) {
                                    tag.putBoolean("monsterCycleOfFateIsDead", false);
                                    restorePotionEffectsFromTag(pEntity, pTag);
                                    int entityX = pTag.getInt("monsterCycleOfFateEntityX");
                                    int entityY = pTag.getInt("monsterCycleOfFateEntityY");
                                    int entityZ = pTag.getInt("monsterCycleOfFateEntityZ");
                                    int entityHealth = pTag.getInt("monsterCycleOfFateEntityHealth");
                                    int entitySequence = pTag.getInt("monsterCycleOfFateEntitySequence");
                                    int entitySpirituality = pTag.getInt("monsterCycleOfFateEntitySpirituality");
                                    pEntity.teleportTo(entityX, entityY, entityZ);
                                    pEntity.setHealth(entityHealth);
                                    if (pEntity instanceof Player player1) {
                                        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player1);
                                        holder.setCurrentSequence(entitySequence);
                                        holder.setSpirituality(entitySpirituality);
                                    }
                                    if (pEntity instanceof PlayerMobEntity playerMobEntity) {
                                        playerMobEntity.setSequence(entitySequence);
                                        playerMobEntity.setSpirituality(entitySpirituality);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void tickEvent(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            CompoundTag tag = entity.getPersistentData();
            int userTag = tag.getInt("monsterCycleOfFateUser");
            int targetTag = tag.getInt("monsterCycleOfFate");
            int entityTag = tag.getInt("monsterCycleOfFateEntity");
            int cycleX = tag.getInt("monsterCycleOfFateEntityX");
            int cycleY = tag.getInt("monsterCycleOfFateEntityY");
            int cycleZ = tag.getInt("monsterCycleOfFateEntityZ");
            boolean isDead = tag.getBoolean("monsterCycleOfFateIsDead");
            if (isDead) {
                entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20,1,false,false));
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20,1,false,false));
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20,5,false,false));
                entity.teleportTo(cycleX, cycleY + 400, cycleZ);
            }
            if (entity.tickCount % 20 == 0) {
                if (userTag == 0 || targetTag == 0 || entityTag == 0) {
                    int effectCount = tag.getInt("monsterCyclePotionEffectsCount");
                    for (int i = 0; i < effectCount; i++) {
                        tag.remove("monsterCyclePotionEffect_" + i);
                    }
                    tag.remove("monsterCyclePotionEffectsCount");
                }

                if (userTag >= 1) {
                    tag.putInt("monsterCycleOfFateUser", userTag - 1);
                }
                if (targetTag >= 1) {
                    tag.putInt("monsterCycleOfFate", targetTag - 1);
                }
                if (entityTag >= 1) {
                    tag.putInt("monsterCycleOfFateEntity", entityTag - 1);
                }

                if (userTag == 1) {
                    tag.putInt("monsterCycleOfFateUserX", 0);
                    tag.putInt("monsterCycleOfFateUserY", 0);
                    tag.putInt("monsterCycleOfFateUserZ", 0);
                    tag.putInt("monsterCycleOfFateUserHealth", 0);
                    tag.putInt("monsterCycleOfFateUserSequence", 0);
                    tag.putBoolean("monsterCycleOfFateIsDead", false);
                }
                if (targetTag == 1) {
                    tag.putInt("monsterCycleOfFateX", 0);
                    tag.putInt("monsterCycleOfFateY", 0);
                    tag.putInt("monsterCycleOfFateZ", 0);
                    tag.putInt("monsterCycleOfFateHolder", 0);
                    tag.putInt("monsterCycleOfFateHealth", 0);
                    tag.putInt("monsterCycleOfFateSpirituality", 0);
                    tag.putInt("monsterCycleOfFateSequence", 0);
                    tag.putBoolean("monsterCycleOfFateIsDead", false);
                }
                if (targetTag == 1) {
                    tag.putInt("monsterCycleOfFateEntityX", 0);
                    tag.putInt("monsterCycleOfFateEntityY", 0);
                    tag.putInt("monsterCycleOfFateEntityZ", 0);
                    tag.putInt("monsterCycleOfFateEntityHealth", 0);
                    tag.putInt("monsterCycleOfFateEntitySpirituality", 0);
                    tag.putInt("monsterCycleOfFateEntitySequence", 0);
                    tag.putBoolean("monsterCycleOfFateIsDead", false);

                }
            }
        }
    }
    private static void savePotionEffectsToTag(LivingEntity entity, CompoundTag tag) {
        Collection<MobEffectInstance> activeEffects = entity.getActiveEffects();
        tag.putInt("monsterCyclePotionEffectsCount", activeEffects.size());

        int i = 0;
        for (MobEffectInstance effect : activeEffects) {
            CompoundTag effectTag = new CompoundTag();
            effect.save(effectTag);
            tag.put("monsterCyclePotionEffect_" + i, effectTag);
            i++;
        }
    }

    private static void restorePotionEffectsFromTag(LivingEntity entity, CompoundTag tag) {
        // Clear existing effects
        for (MobEffectInstance activeEffect : new ArrayList<>(entity.getActiveEffects())) {
            entity.removeEffect(activeEffect.getEffect());
        }

        // Restore saved effects
        int effectCount = tag.getInt("monsterCyclePotionEffectsCount");
        for (int i = 0; i < effectCount; i++) {
            CompoundTag effectTag = tag.getCompound("monsterCyclePotionEffect_" + i);
            MobEffectInstance effect = MobEffectInstance.load(effectTag);
            if (effect != null) {
                entity.addEffect(effect);
            }
        }
    }
}