package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.entity.MeteorEntity;
import net.swimmingtuna.lotm.entity.TornadoEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import javax.annotation.Nullable;
import java.util.List;

public class MonsterCalamityIncarnation extends SimpleAbilityItem {
    public MonsterCalamityIncarnation(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 5, 150, 60);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        calamityIncarnation(player);
        return InteractionResult.SUCCESS;
    }

    public static void calamityIncarnation(Player player) {
        if (!player.level().isClientSide()) {
            CompoundTag tag = player.getPersistentData();
            int calamityIncarnation = tag.getInt("monsterCalamityIncarnationItem");
            if (calamityIncarnation == 1) {
                tag.putInt("calamityIncarnationInMeteor", 200);
            }
            if (calamityIncarnation == 2) {
                TornadoEntity.summonCalamityTornado(player);
                player.getPersistentData().putInt("calamityIncarnationTornado", 300);
            }
            if (calamityIncarnation == 3) {
                tag.putInt("calamityIncarnationInLightning", 200);
            }
            if (calamityIncarnation == 4) {
                tag.putInt("calamityIncarnationInPlague", 250);
            }
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player player) {
            if (player.tickCount % 2 == 0 && !level.isClientSide()) {
                if (player.getMainHandItem().getItem() instanceof MonsterCalamityIncarnation) {
                    player.displayClientMessage(Component.literal("Current Calamity Incarnation is: " + calamityIncarnationString(player)), true);
                }
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    public static String calamityIncarnationString(Player pPlayer) {
        CompoundTag tag = pPlayer.getPersistentData();
        int luckManipulation = tag.getInt("monsterCalamityIncarnationItem");
        if (luckManipulation == 1) {
            return "Meteor";
        }
        if (luckManipulation == 2) {
            return "Tornado";
        }
        if (luckManipulation == 3) {
            return "Lightning Storm";
        }
        if (luckManipulation == 4) {
            return "Plague";
        }
        return "None";
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal("Upon use, activates your danger sense, alerting you of players around you and where they are"));
        tooltipComponents.add(Component.literal("Activation Cost: ").append(Component.literal("None").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("40 per second").withStyle(ChatFormatting.YELLOW)));
        Component.literal("Cooldown: ").append(Component.literal("None").withStyle(ChatFormatting.YELLOW));
        tooltipComponents.add(getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    public static void calamityTickEvent(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        CompoundTag tag = entity.getPersistentData();
        Vec3 lookVec = entity.getLookAngle();
        int meteor = tag.getInt("calamityIncarnationInMeteor");
        int tornado = tag.getInt("calamityIncarnationInTornado");
        int lightning = tag.getInt("calamityIncarnationInLightning");
        int plague = tag.getInt("calamityIncarnationInPlague");
        int immunity = tag.getInt("monsterCalamityImmunity");
        BlockPos pos = entity.getOnPos().below(1);
        if (immunity >= 1) {
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 4, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20, 0, false, false));
            tag.putInt("monsterCalamityImmunity", immunity - 1);
        }
        if (meteor >= 1) {
            tag.putInt("monsterCalamityImmunity", 10);
            int x = tag.getInt("calamityIncarnationInMeteorX");
            int y = tag.getInt("calamityIncarnationInMeteorY");
            int z = tag.getInt("calamityIncarnationInMeteorZ");
            if (meteor == 199) {
                tag.putInt("calamityIncarnationInMeteorX", (int) entity.getX());
                tag.putInt("calamityIncarnationInMeteorY", (int) entity.getY());
                tag.putInt("calamityIncarnationInMeteorZ", (int) entity.getZ());
            }
            if (meteor == 198) {
                entity.teleportTo(entity.getX(), entity.getY() + 100, entity.getY());
            }
            if (meteor == 195) {
                MeteorEntity meteorEntity = new MeteorEntity(EntityInit.METEOR_ENTITY.get(), entity.level());
                ScaleData scaleData = ScaleTypes.BASE.getScaleData(meteorEntity);
                if (entity instanceof Player player) {
                    scaleData.setScale(12 - BeyonderHolderAttacher.getHolderUnwrap(player).getCurrentSequence() * 2);
                } else {
                    scaleData.setScale(8);
                }
                meteorEntity.setOwner(entity);
                scaleData.markForSync(true);
                meteorEntity.teleportTo(entity.getX(), entity.getY() + scaleData.getScale() * 1.5, entity.getZ());
                meteorEntity.noPhysics = true;
                entity.level().addFreshEntity(meteorEntity);
            }
            entity.setDeltaMovement(lookVec.scale(2.5).x, -1.75, lookVec.scale(2.5).z);
            entity.hurtMarked = true;
            tag.putInt("calamityIncarnationInMeteor", meteor - 1);
        }
        if (tornado >= 1) {
            tag.putInt("calamityIncarnationInTornado", tornado - 1);
            if (entity instanceof Player player) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                if (holder.currentClassMatches(BeyonderClassInit.MONSTER)) {
                    tag.putInt("monsterCalamityImmunity", 5);
                }
            }
        }
        if (lightning >= 1) {
            tag.putInt("calamityIncarnationInLightning", lightning - 1);
            tag.putInt("monsterCalamityImmunity", 5);
            int randomInt = (int) ((Math.random() * 50) - 25);
            Vec3 startPos = new BlockPos((int) entity.getX() + randomInt, (int) entity.getY() + 50, (int) entity.getZ() + randomInt).getCenter();
            LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), entity.level());
            lightningEntity.setSpeed(9.0f);
            lightningEntity.setMaxLength(60);
            lightningEntity.setNoUp(true);
            lightningEntity.setNewStartPos(startPos);
            lightningEntity.setDeltaMovement(0, -3, 0);
            if (entity instanceof Player player) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                int sequence = holder.getCurrentSequence();
                if (sequence >= 4) {
                    entity.level().addFreshEntity(lightningEntity);
                    entity.level().addFreshEntity(lightningEntity);
                    entity.level().addFreshEntity(lightningEntity);
                } else if (sequence >= 1) {
                    entity.level().addFreshEntity(lightningEntity);
                    entity.level().addFreshEntity(lightningEntity);
                    entity.level().addFreshEntity(lightningEntity);
                    entity.level().addFreshEntity(lightningEntity);
                    entity.level().addFreshEntity(lightningEntity);
                } else {
                    entity.level().addFreshEntity(lightningEntity);
                    entity.level().addFreshEntity(lightningEntity);
                    entity.level().addFreshEntity(lightningEntity);
                    entity.level().addFreshEntity(lightningEntity);
                    entity.level().addFreshEntity(lightningEntity);
                    entity.level().addFreshEntity(lightningEntity);
                    entity.level().addFreshEntity(lightningEntity);
                }
            } else {
                entity.level().addFreshEntity(lightningEntity);
                entity.level().addFreshEntity(lightningEntity);
                entity.level().addFreshEntity(lightningEntity);
            }
        }
        if (plague >= 1) {
            tag.putInt("calamityIncarnationInPlague", plague - 1);
            tag.putInt("monsterCalamityImmunity", 5);
            if (entity instanceof Player player) {
                int sequence = BeyonderHolderAttacher.getHolderUnwrap(player).getCurrentSequence();
                for (LivingEntity livingEntity : entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(150 - (sequence * 20)))) {
                    if (livingEntity != entity) {
                        if (sequence >= 4) {
                            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 3, false, false));
                            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1, false, false));
                            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 2, false, false));
                            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 2, false, false));
                        } else if (sequence >= 1) {
                            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 4, false, false));
                            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2, false, false));
                            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 3, false, false));
                            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 3, false, false));
                        } else {
                            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 5, false, false));
                            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 3, false, false));
                            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 4, false, false));
                            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 4, false, false));
                            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 40,1,false,false));
                        }
                    }
                }
            } else {
                for (LivingEntity livingEntity : entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(80))) {
                    if (livingEntity != entity) {
                        entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 3, false, false));
                        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1, false, false));
                        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 2, false, false));
                        entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 2, false, false));
                    }
                }
            }
        }
    }
}