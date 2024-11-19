package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.entity.MeteorEntity;
import net.swimmingtuna.lotm.entity.TornadoEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MisfortuneManipulation extends SimpleAbilityItem {
    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public MisfortuneManipulation(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 4, 350, 200, 50, 50);
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            if (!checkAll(player)) {
                return InteractionResult.FAIL;
            }
            useSpirituality(player);
            addCooldown(player);
            manipulateMisfortune(interactionTarget, player);
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

    private static void manipulateMisfortune(LivingEntity interactionTarget, Player player) {
        if (!player.level().isClientSide() && !interactionTarget.level().isClientSide()) {
            CompoundTag tag = interactionTarget.getPersistentData();
            CompoundTag playerTag = player.getPersistentData();
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            int misfortuneManipulation = playerTag.getInt("misfortuneManipulationItem");
            if (misfortuneManipulation == 1) {
                int random = (int) ((Math.random() * 40) - 20);
                if (holder.getCurrentSequence() > 2) {
                    player.sendSystemMessage(Component.literal("Meteors Summoned"));
                    summonMeteor(interactionTarget);
                }
                if (holder.getCurrentSequence() == 2 || holder.getCurrentSequence() == 1) {
                    player.sendSystemMessage(Component.literal("Meteors Summoned Angel"));
                    summonMeteor(interactionTarget);
                    summonMeteor(interactionTarget);
                }
                if (holder.getCurrentSequence() == 0) {
                    player.sendSystemMessage(Component.literal("Meteors Summoned Deity"));
                    summonMeteor(interactionTarget);
                    summonMeteor(interactionTarget);
                    summonMeteor(interactionTarget);
                    summonMeteor(interactionTarget);
                    summonMeteor(interactionTarget);

                }
            }
            if (misfortuneManipulation == 2) {
                TornadoEntity tornadoEntity = new TornadoEntity(EntityInit.TORNADO_ENTITY.get(), player.level());
                tornadoEntity.setTornadoLifecount(400 - (holder.getCurrentSequence() * 60));
                tornadoEntity.setOwner(player);
                tornadoEntity.setTornadoPickup(true);
                tornadoEntity.setTornadoRadius(50 - (holder.getCurrentSequence() * 8));
                tornadoEntity.setTornadoHeight(80 - (holder.getCurrentSequence() * 10));
                tornadoEntity.teleportTo(interactionTarget.getX(), interactionTarget.getY(), interactionTarget.getZ());
                player.level().addFreshEntity(tornadoEntity);
            }
            if (misfortuneManipulation == 3) {
                interactionTarget.getPersistentData().putInt("sailorLightningStorm1", 200 - (holder.getCurrentSequence() * 25));
                interactionTarget.getPersistentData().putInt("sailorStormVecX1", (int) interactionTarget.getX());
                interactionTarget.getPersistentData().putInt("sailorStormVecY1", (int) interactionTarget.getY());
                interactionTarget.getPersistentData().putInt("sailorStormVecZ1", (int) interactionTarget.getZ());
            }
            if (misfortuneManipulation == 4) {
                LightningEntity lightning = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), interactionTarget.level());
                lightning.setSpeed(5.0f);
                lightning.setTargetEntity(interactionTarget);
                lightning.setMaxLength(120);
                lightning.setNewStartPos(new Vec3(interactionTarget.getX(), interactionTarget.getY() + 80, interactionTarget.getZ()));
                lightning.setDeltaMovement(0, -3,0);
                lightning.setNoUp(true);
                player.level().addFreshEntity(lightning);
            }
            if (misfortuneManipulation == 5) {
                for (Mob mob : interactionTarget.level().getEntitiesOfClass(Mob.class, interactionTarget.getBoundingBox().inflate(60 - (holder.getCurrentSequence() * 10)))) {
                    if (mob.getTarget() != interactionTarget) {
                        mob.setTarget(interactionTarget);
                        mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 2, false, false));
                    }
                }
            }
            if (misfortuneManipulation == 6) {
                tag.putInt("luckDoubleDamage", tag.getInt("luckDoubleDamage") + 5 - holder.getCurrentSequence());
            }
            if (misfortuneManipulation == 7) {
                    Random random = new Random();
                    List<EquipmentSlot> armorSlots = Arrays.asList(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
                    List<EquipmentSlot> equippedArmor = armorSlots.stream()
                            .filter(slot -> !player.getItemBySlot(slot).isEmpty())
                            .toList();
                    if (!equippedArmor.isEmpty()) {
                        EquipmentSlot randomArmorSlot = equippedArmor.get(random.nextInt(equippedArmor.size()));
                        ItemStack armorPiece = player.getItemBySlot(randomArmorSlot);
                        player.spawnAtLocation(armorPiece);
                        player.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                    }
            }
            if (misfortuneManipulation == 8) {
                tag.putInt("luckIgnoreAbility", tag.getInt("luckIgnoreAbility") + 1);
            }
            if (misfortuneManipulation == 9) {
                BeyonderUtil.applyMobEffect(interactionTarget, MobEffects.POISON, 300 - (holder.getCurrentSequence() * 40), 4, true, true);
            }
        }

    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player player) {
            if (player.tickCount % 2 == 0 && !level.isClientSide()) {
                if (player.getMainHandItem().getItem() instanceof MisfortuneManipulation) {
                    player.displayClientMessage(Component.literal("Current Misfortune Manipulation is: " + misfortuneManipulationString(player)), true);
                }
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    public static String misfortuneManipulationString(Player pPlayer) {
        CompoundTag tag = pPlayer.getPersistentData();
        int luckManipulation = tag.getInt("misfortuneManipulationItem");
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
            return "Lightning Bolt";
        }
        if (luckManipulation == 5) {
            return "Attract Mobs";
        }
        if (luckManipulation == 6) {
            return "Double next damage";
        }
        if (luckManipulation == 7) {
            return "Unequip Armor";
        }
        if (luckManipulation == 8) {
            return "Next Ability Use Failed";
        }
        if (luckManipulation == 9) {
            return "Poison";
        }
        return null;
    }

    private static void summonMeteor(LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            int x = (int) entity.getX();
            int y = (int) entity.getY();
            int z = (int) entity.getZ();
            Vec3 targetPos = new Vec3(x,y,z);
            MeteorEntity meteor = new MeteorEntity(EntityInit.METEOR_ENTITY.get(), entity.level());
            meteor.teleportTo(x + (Math.random() * 100) - 50,y + 150 + (Math.random() * 100) - 50, z+ (Math.random() * 100) - 50);
            meteor.noPhysics = true;
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(meteor);
            scaleData.setScale(3);
            scaleData.markForSync(true);
            Vec3 randomizedTargetPos = targetPos.add((Math.random() * 20 - 10), (Math.random() * 20 - 10), (Math.random() * 20 - 10));
            double speed = 4.0;
            Vec3 directionToTarget = randomizedTargetPos.subtract(meteor.position()).normalize();
            meteor.setDeltaMovement(directionToTarget.scale(speed));
            entity.level().addFreshEntity(meteor);
        }
    }
}