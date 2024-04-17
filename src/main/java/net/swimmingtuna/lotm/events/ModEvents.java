package net.swimmingtuna.lotm.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
    }

    @SubscribeEvent
    public static void attributeHandler(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if(!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.START) {
            AttributeInstance nightmareAttribute = pPlayer.getAttribute(ModAttributes.NIGHTMARE.get());
            AttributeInstance armorInvisAttribute = pPlayer.getAttribute(ModAttributes.ARMORINVISIBLITY.get());
            if (!event.player.level().isClientSide && event.phase == TickEvent.Phase.START) {
                int nightmareTimer = pPlayer.getPersistentData().getInt("NightmareTimer");

                if (nightmareAttribute.getValue() >= 1) {
                    nightmareTimer++;
                    if (nightmareTimer >= 600) {
                        nightmareAttribute.setBaseValue(0);
                        nightmareTimer = 0;
                    }
                } else {
                    nightmareTimer = 0;
                }
                if (armorInvisAttribute.getValue() > 0 && !pPlayer.hasEffect(MobEffects.INVISIBILITY)) {
                    removeArmor(pPlayer);
                    armorInvisAttribute.setBaseValue(0);
                }

                // Save the updated nightmareTimer in player persistent data
                pPlayer.getPersistentData().putInt("NightmareTimer", nightmareTimer);
            }
        }
    }

    @SubscribeEvent
    public static void handleLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (!entity.level().isClientSide) {
            int mentalPlagueTimer = entity.getPersistentData().getInt("MentalPlagueTimer");
            if (entity.hasEffect(ModEffects.MENTALPLAGUE.get())) {
                mentalPlagueTimer++;

                if (mentalPlagueTimer >= 600) {
                    for (LivingEntity entity1 : entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(50))) {
                        applyEffectsAndDamage(entity1);

                    }
                    applyEffectsAndDamage(entity);
                    mentalPlagueTimer = 0;
                }
            }
            entity.getPersistentData().putInt("MentalPlagueTimer", mentalPlagueTimer);
        }
    }

    private static void applyEffectsAndDamage(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.POISON, 400, 2, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 400, 2, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400, 1, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 400, 1, false, false));
        entity.hurt(entity.damageSources().magic(), 20);
    }

    private static void removeArmor(Player pPlayer) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack armorStack = pPlayer.getItemBySlot(slot);
                if (!armorStack.isEmpty()) {
                    pPlayer.setItemSlot(slot, ItemStack.EMPTY);
                }
            }
        }
    }

    @SubscribeEvent
    public static void sailorLightningEvent(AttackEntityEvent event) {
        Player pPlayer = event.getEntity();
        CompoundTag tag = pPlayer.getPersistentData();
        boolean x = tag.getBoolean("SailorLightning");
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
                if (holder.isSailorClass() && tyrantSequence.getCurrentSequence() <= 8) {
                    LivingEntity target = (LivingEntity) event.getTarget();
                    if (x) {
                    if (target != pPlayer) {
                        double chanceOfDamage = (100.0 - (tyrantSequence.getCurrentSequence() * 12.5)); // Decrease chance by 12.5% for each level below 9
                        if (Math.random() * 100 < chanceOfDamage) {
                            LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, target.level());
                            lightningBolt.moveTo(target.getX(), target.getY(), target.getZ());
                            target.level().addFreshEntity(lightningBolt);
                        }
                    }
                    }
                }
            });
        }
    }
}