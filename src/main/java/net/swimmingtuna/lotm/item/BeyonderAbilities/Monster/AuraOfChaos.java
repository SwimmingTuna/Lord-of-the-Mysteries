package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;


import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.MeteorEntity;
import net.swimmingtuna.lotm.entity.PlayerMobEntity;
import net.swimmingtuna.lotm.entity.TornadoEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import net.swimmingtuna.lotm.world.worlddata.CalamityEnhancementData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class AuraOfChaos extends SimpleAbilityItem {

    public AuraOfChaos(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 3, 0, 20);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        activateAuraOfChaos(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    private void activateAuraOfChaos(Player player) {
        if (!player.level().isClientSide()) {
            CompoundTag tag = player.getPersistentData();
            boolean auraOfChaos = tag.getBoolean("monsterAuraOfChaos");
            tag.putBoolean("monsterAuraOfChaos", !auraOfChaos);
            player.displayClientMessage(Component.literal("Aura of Chaos Turned " + (auraOfChaos ? "Off" : "On")).withStyle(ChatFormatting.BOLD, ChatFormatting.RED), true);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, enables or disables an aura of chaos around you. While enabled, it causes all entities around you to suffer a misfortunate event or calamity every two seconds. "));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("150 per second").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("None").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
    public static void auraOfChaos(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();
        CompoundTag tag = entity.getPersistentData();
        if (tag.getBoolean("monsterAuraOfChaos")) {
            if (entity instanceof Player player) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                int sequence = holder.getCurrentSequence();
                if (player.tickCount % 20 == 0) {
                    holder.useSpirituality(150);
                }
                int enhancement = CalamityEnhancementData.getInstance((ServerLevel) entity.level()).getCalamityEnhancement();
                for (LivingEntity livingEntity : entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(200 - (sequence * 50) + (enhancement * 50)))) {
                    CompoundTag persistentData = livingEntity.getPersistentData();
                    Random random = new Random();
                    int randomInt = random.nextInt(350);
                    if (livingEntity != entity && entity.tickCount % 200 == 0) {
                        if (randomInt >= 95 && randomInt <= 100) {
                            int random1 = (int) ((Math.random() * 200) - 100);
                            MeteorEntity.summonMeteorAtPositionWithScale(entity,  livingEntity.getX() + random1, livingEntity.getY() - 25,livingEntity.getZ() + random1, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 6 );
                        } else if (randomInt >= 89 && randomInt <= 94) {
                            TornadoEntity tornadoEntity = new TornadoEntity(EntityInit.TORNADO_ENTITY.get(), level);
                            tornadoEntity.teleportTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                            tornadoEntity.setTornadoPickup(true);
                            tornadoEntity.setTornadoLifecount(150);
                            tornadoEntity.setOwner(entity);
                            tornadoEntity.setTornadoRadius(25);
                            tornadoEntity.setTornadoHeight(50);
                            entity.level().addFreshEntity(tornadoEntity);
                        } else if (randomInt >= 80 && randomInt <= 88) {
                            persistentData.putInt("luckDoubleDamage", persistentData.getInt("luckDoubleDamage") + 1);
                        } else if (randomInt >= 75 && randomInt <= 87) {
                            BeyonderUtil.applyMobEffect(livingEntity, MobEffects.POISON, 100, 3, false, false);
                        } else if (randomInt >= 60 && randomInt <= 74) {
                            Zombie zombie = new Zombie(EntityType.ZOMBIE, player.level());
                            ItemStack netheriteHelmet = new ItemStack(Items.NETHERITE_HELMET);
                            ItemStack netheriteChestplate = new ItemStack(Items.NETHERITE_BOOTS);
                            ItemStack netheriteLeggings = new ItemStack(Items.NETHERITE_LEGGINGS);
                            ItemStack netheriteBoots = new ItemStack(Items.NETHERITE_BOOTS);
                            ItemStack netheriteSword = new ItemStack(Items.NETHERITE_SWORD);
                            zombie.setDropChance(EquipmentSlot.MAINHAND, 0);
                            zombie.setDropChance(EquipmentSlot.CHEST, 0);
                            zombie.setDropChance(EquipmentSlot.LEGS, 0);
                            zombie.setDropChance(EquipmentSlot.FEET, 0);
                            zombie.setDropChance(EquipmentSlot.HEAD, 0);
                            netheriteHelmet.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 5);
                            netheriteChestplate.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 5);
                            netheriteLeggings.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 5);
                            netheriteBoots.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 5);
                            netheriteSword.enchant(Enchantments.FIRE_ASPECT,2);
                            netheriteSword.enchant(Enchantments.SHARPNESS,2);
                            zombie.setItemSlot(EquipmentSlot.HEAD, netheriteHelmet);
                            zombie.setItemSlot(EquipmentSlot.CHEST, netheriteChestplate);
                            zombie.setItemSlot(EquipmentSlot.LEGS, netheriteLeggings);
                            zombie.setItemSlot(EquipmentSlot.FEET, netheriteBoots);
                            zombie.setItemSlot(EquipmentSlot.MAINHAND, netheriteSword);
                            zombie.setBaby(true);
                            zombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100000, 3, true, true));
                            zombie.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100000, 3, false, false));
                            zombie.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100000, 2, false, false));
                            zombie.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
                            zombie.teleportTo(entity.getX(), entity.getY(), entity.getZ());
                            zombie.setTarget(livingEntity);
                            entity.level().addFreshEntity(zombie);
                        } else if (randomInt >= 40 && randomInt <= 59) {
                            livingEntity.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), 40, 1, false, false));
                        } else if (randomInt >= 20 && randomInt <= 39) {
                            LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, player.level());
                            lightningBolt.teleportTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                            lightningBolt.setDamage(15.0f);
                            entity.level().addFreshEntity(lightningBolt);
                        } else {
                            if (livingEntity instanceof Player ppPlayer) {
                                ppPlayer.getPersistentData().putInt("cantUseAbility", ppPlayer.getPersistentData().getInt("cantUseAbility") + 1);
                            } else {
                                livingEntity.hurt(livingEntity.damageSources().magic(), 15);
                            }
                        }
                        livingEntity.getPersistentData().putDouble("misfortune", livingEntity.getPersistentData().getDouble("misfortune") + 3);

                    }
                }
            }
        }
    }
}