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
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
            player.displayClientMessage(Component.literal("Projectile Movement Turned " + (auraOfChaos ? "Off" : "On")).withStyle(ChatFormatting.BOLD, ChatFormatting.RED), true);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal(
                "Upon use, exude an aura of tyranny, not giving any entity permission to move, implanting fear strong enough to not allow them to use their abilities"
        ).withStyle(/*ChatFormatting.BOLD, ChatFormatting.BLUE*/));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
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
                    int randomInt = random.nextInt(100);
                    if (livingEntity != entity && entity.tickCount % 35 == 0) {
                        if (randomInt >= 95) {
                            MeteorEntity.summonMeteorAtPosition(entity, (int) livingEntity.getX(), (int) livingEntity.getY(), (int) livingEntity.getZ());
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
                        if (livingEntity instanceof Player || livingEntity instanceof PlayerMobEntity) {
                            AttributeInstance misfortune = livingEntity.getAttribute(ModAttributes.MISFORTUNE.get());
                            if (misfortune != null) {
                                misfortune.setBaseValue(misfortune.getBaseValue() + 3);
                            }
                        }
                    }
                }
            }
        }
    }
}