package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.entity.PlayerMobEntity;
import net.swimmingtuna.lotm.entity.StoneEntity;
import net.swimmingtuna.lotm.entity.TornadoEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import net.swimmingtuna.lotm.world.worlddata.CalamityEnhancementData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MisfortuneRedirection extends SimpleAbilityItem {
    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public MisfortuneRedirection(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 3, 300, 200, 100, 100);
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            if (!checkAll(player)) {
                return InteractionResult.FAIL;
            }
            useSpirituality(player);
            addCooldown(player);
            misfortuneRedirection(interactionTarget, player);
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 100, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 100, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, takes all misfortunate values from all entities nearby and causes them to instantly occur in the target"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("300").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("10 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }


    private static void misfortuneRedirection(LivingEntity interactionTarget, Player player) {
        if (!player.level().isClientSide() && !interactionTarget.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            for (LivingEntity livingEntity : interactionTarget.level().getEntitiesOfClass(LivingEntity.class, interactionTarget.getBoundingBox().inflate(300 - (holder.getCurrentSequence() * 50)))) {
                CompoundTag tag = livingEntity.getPersistentData();
                int enhancement = CalamityEnhancementData.getInstance((ServerLevel) player.level()).getCalamityEnhancement();
                int paralysisDuration = 0;
                int lotmLightningCount = 0;
                int mcLightningCount = 0;
                int poisonDuration = 0;
                int calamityGroundTremorCounter = 0;
                int calamityGazeCounter = 0;
                int calamityBreezeCounter = 0;
                int calamityWaveCounter = 0;
                int calamityExplosionCounter = 0;
                int cantUseAbilityCount = 0;
                int doubleDamageCount = 0;
                int meteor = tag.getInt("luckMeteor");
                int lotmLightning = tag.getInt("luckLightningLOTM");
                int paralysis = tag.getInt("luckParalysis");
                int unequipArmor = tag.getInt("luckUnequipArmor");
                int wardenSpawn = tag.getInt("luckWarden");
                int mcLightning = tag.getInt("luckLightningMC");
                int poison = tag.getInt("luckPoison");
                int tornadoInt = tag.getInt("luckTornado");
                int stone = tag.getInt("luckStone");
                int doubleDamage = tag.getInt("luckDoubleDamage");
                int cantUseAbility = tag.getInt("cantUseAbility");
                int calamityMeteor = tag.getInt("calamityMeteor");
                int calamityLightningStorm = tag.getInt("calamityLightningStorm");
                int calamityLightningBolt = tag.getInt("calamityLightningBolt");
                int calamityGroundTremor = tag.getInt("calamityGroundTremor");
                int calamityGaze = tag.getInt("calamityGaze");
                int calamityUndeadArmy = tag.getInt("calamityUndeadArmy");
                int calamityBabyZombie = tag.getInt("calamityBabyZombie");
                int calamityWindArmorRemoval = tag.getInt("calamityWindArmorRemoval");
                int calamityBreeze = tag.getInt("calamityBreeze");
                int calamityWave = tag.getInt("calamityWave");
                int calamityExplosion = tag.getInt("calamityExplosion");
                int calamityTornado = tag.getInt("calamityTornado");
                if (meteor >= 1) {
                    MisfortuneManipulation.summonMeteor(interactionTarget);
                }
                if (lotmLightning >= 1) {
                    lotmLightningCount = lotmLightningCount + enhancement;
                    interactionTarget.getPersistentData().putInt("misfortuneRedirectionLightning", interactionTarget.getPersistentData().getInt("misfortuneRedirectionLightning") + lotmLightningCount);
                }
                if (paralysis >= 1) {
                    paralysisDuration = paralysisDuration + enhancement;
                    interactionTarget.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), paralysisDuration * 10, 1, false, false));
                }
                if (unequipArmor >= 1) {
                    if (interactionTarget instanceof Player pPlayer) {
                        Random random = new Random();
                        List<EquipmentSlot> armorSlots = Arrays.asList(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
                        List<EquipmentSlot> equippedArmor = armorSlots.stream()
                                .filter(slot -> !pPlayer.getItemBySlot(slot).isEmpty())
                                .toList();
                        if (!equippedArmor.isEmpty()) {
                            EquipmentSlot randomArmorSlot = equippedArmor.get(random.nextInt(equippedArmor.size()));
                            ItemStack armorPiece = pPlayer.getItemBySlot(randomArmorSlot);
                            pPlayer.spawnAtLocation(armorPiece);
                            pPlayer.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                        }
                    }
                }
                if (wardenSpawn >= 1) {
                    for (int i = 0; i < enhancement; i++) {
                        WitherBoss witherBoss = new WitherBoss(EntityType.WITHER, interactionTarget.level());
                        witherBoss.setTarget(interactionTarget);
                        witherBoss.teleportTo(interactionTarget.getX(), interactionTarget.getY(), interactionTarget.getZ());
                        AttributeInstance maxHp = witherBoss.getAttribute(Attributes.MAX_HEALTH);
                        maxHp.setBaseValue(551);
                        witherBoss.getPersistentData().putInt("DeathTimer", 0);
                    }
                }
                if (mcLightning >= 1) {
                    mcLightningCount = mcLightningCount + enhancement;
                    interactionTarget.getPersistentData().putInt("misfortuneRedirectionMCLightning", mcLightningCount);
                }
                if (poison >= 1) {
                    poisonDuration = poisonDuration + enhancement;
                    interactionTarget.addEffect(new MobEffectInstance(MobEffects.POISON, poisonDuration * 15, 1, false, false));
                }
                if (tornadoInt >= 1) {
                    for (int i = 0; i < enhancement; i++) {
                        TornadoEntity.summonTornadoRandom(interactionTarget);
                    }
                }
                if (stone >= 1) {
                    for (int i = 0; i < enhancement; i++) {
                        StoneEntity.summonStoneRandom(interactionTarget);
                    }
                }
                if (doubleDamage >= 1) {
                    doubleDamageCount = doubleDamageCount + enhancement;
                    interactionTarget.getPersistentData().putInt("luckDoubleDamage", doubleDamage + doubleDamageCount);
                }
                if (cantUseAbility >= 1) {
                    cantUseAbilityCount = cantUseAbilityCount + enhancement;
                    interactionTarget.getPersistentData().putInt("cantUseAbility", cantUseAbility + cantUseAbilityCount);
                }
                if (calamityMeteor >= 1) {
                    for (int i = 0; i < enhancement; i++) {
                        MisfortuneManipulation.summonMeteor(interactionTarget);
                    }
                }
                if (calamityLightningStorm >= 1) {
                    interactionTarget.getPersistentData().putInt("sailorLightningStorm1", interactionTarget.getPersistentData().getInt("sailorLightningStorm1") + (10 * enhancement));
                    interactionTarget.getPersistentData().putInt("sailorStormVecX1", (int) interactionTarget.getX());
                    interactionTarget.getPersistentData().putInt("sailorStormVecY1", (int) interactionTarget.getY());
                    interactionTarget.getPersistentData().putInt("sailorStormVecZ1", (int) interactionTarget.getZ());
                }
                if (calamityLightningBolt >= 1) {
                    interactionTarget.getPersistentData().putInt("misfortuneRedirectionLightning", interactionTarget.getPersistentData().getInt("misfortuneRedirectionLightning") + lotmLightningCount);
                }
                if (calamityGroundTremor >= 1) {
                    calamityGroundTremorCounter++;
                    for (LivingEntity living : interactionTarget.level().getEntitiesOfClass(LivingEntity.class, interactionTarget.getBoundingBox().inflate(calamityGroundTremorCounter * 5))) {
                        if (livingEntity != player) {
                            living.hurt(BeyonderUtil.genericSource(interactionTarget), 5 * enhancement);
                        }
                    }
                }
                if (calamityGaze >= 1) {
                    calamityGazeCounter++;
                    for (LivingEntity living : interactionTarget.level().getEntitiesOfClass(LivingEntity.class, interactionTarget.getBoundingBox().inflate(calamityGazeCounter * 5))) {
                        if (living != player) {
                            if (living instanceof Player || living instanceof PlayerMobEntity) {
                                AttributeInstance corruption = living.getAttribute(ModAttributes.CORRUPTION.get());
                                if (corruption != null) {
                                    corruption.setBaseValue(corruption.getBaseValue() + (4 * enhancement));
                                }
                            }
                        }
                    }
                }
                if (calamityUndeadArmy >= 1) {
                    undeadArmy(interactionTarget);
                }
                if (calamityBabyZombie >= 1) {
                    Zombie zombie = new Zombie(EntityType.ZOMBIE, player.level());
                    zombie.setBaby(true);
                    zombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100000, 3, true, true));
                    zombie.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100000, 3, false, false));
                    zombie.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100000, 2, false, false));
                    zombie.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
                    zombie.teleportTo(interactionTarget.getX(), interactionTarget.getY(), interactionTarget.getZ());
                    zombie.setTarget(interactionTarget);
                    for (int i = 0; i < enhancement; i++) {
                    interactionTarget.level().addFreshEntity(zombie);
                    }
                }
                if (calamityWindArmorRemoval >= 1) {
                    if (interactionTarget instanceof Player pPlayer) {
                        Random random = new Random();
                        List<EquipmentSlot> armorSlots = Arrays.asList(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
                        List<EquipmentSlot> equippedArmor = armorSlots.stream()
                                .filter(slot -> !pPlayer.getItemBySlot(slot).isEmpty())
                                .toList();
                        if (!equippedArmor.isEmpty()) {
                            EquipmentSlot randomArmorSlot = equippedArmor.get(random.nextInt(equippedArmor.size()));
                            ItemStack armorPiece = pPlayer.getItemBySlot(randomArmorSlot);
                            pPlayer.spawnAtLocation(armorPiece);
                            pPlayer.setItemSlot(randomArmorSlot, ItemStack.EMPTY);
                        }
                    }
                }
                if (calamityBreeze >= 1) {
                    calamityBreezeCounter++;
                    for (LivingEntity living : interactionTarget.level().getEntitiesOfClass(LivingEntity.class, interactionTarget.getBoundingBox().inflate((calamityBreezeCounter * 5) + (enhancement * 5)))) {
                        if (livingEntity != player) {
                            living.addEffect(new MobEffectInstance(ModEffects.STUN.get(), calamityBreezeCounter * 10));
                            living.hurt(BeyonderUtil.genericSource(interactionTarget), 4);
                            living.setTicksFrozen(calamityBreezeCounter * 10);
                        }
                    }
                }
                if (calamityWave >= 1) {
                    calamityWaveCounter++;
                    for (LivingEntity living : interactionTarget.level().getEntitiesOfClass(LivingEntity.class, interactionTarget.getBoundingBox().inflate((calamityBreezeCounter * 5) + (enhancement * 5)))) {
                        if (livingEntity != player) {
                            living.setSecondsOnFire(calamityWaveCounter * 2);
                            living.hurt(living.damageSources().lava(), 5 * enhancement);
                        }
                    }
                }
                if (calamityExplosion >= 1) {
                    calamityExplosionCounter++;
                    BlockPos hitPos = interactionTarget.getOnPos();
                    float radius = calamityExplosionCounter * 4 + (enhancement);
                    interactionTarget.level().playSound(null, interactionTarget.getOnPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 30.0f, 1.0f);
                    for (BlockPos pos : BlockPos.betweenClosed(
                            hitPos.offset((int) -radius, (int) -radius, (int) -radius),
                            hitPos.offset((int) radius, (int) radius, (int) radius))) {
                        if (pos.distSqr(hitPos) <= radius * radius) {
                            if (interactionTarget.level().getBlockState(pos).getDestroySpeed(interactionTarget.level(), pos) >= 0) {
                                interactionTarget.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                            }
                        }
                    }
                    List<Entity> entities = interactionTarget.level().getEntities(interactionTarget,
                            new AABB(hitPos.offset((int) -radius, (int) -radius, (int) -radius),
                                    hitPos.offset((int) radius, (int) radius, (int) radius)));
                    for (Entity entity : entities) {
                        if (entity instanceof LivingEntity livingEntity1) {
                            livingEntity1.hurt(BeyonderUtil.genericSource(interactionTarget), 4 * radius); // problem w/ damage sources
                        }
                    }
                }

                if (calamityTornado >= 1) {
                    for (int i = 0; i < enhancement; i++) {
                        TornadoEntity.summonTornadoRandom(interactionTarget);
                    }
                }
            }
        }
    }

    public static void misfortuneLivingTickEvent(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!livingEntity.level().isClientSide() && livingEntity.tickCount % 20 == 0) {
            int enhancement = CalamityEnhancementData.getInstance((ServerLevel) livingEntity.level()).getCalamityEnhancement();
            if (livingEntity.getPersistentData().getInt("misfortuneRedirectionLightning") >= 1) {
                LightningEntity lightning = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), livingEntity.level());
                lightning.setSpeed(5.0f);
                lightning.setTargetEntity(livingEntity);
                lightning.setMaxLength(120);
                lightning.setNewStartPos(new Vec3(livingEntity.getX(), livingEntity.getY() + 80, livingEntity.getZ()));
                lightning.setDeltaMovement(0, -3, 0);
                lightning.setNoUp(true);
                for (int i = 0; i < enhancement; i++) {
                    livingEntity.level().addFreshEntity(lightning);
                }
                livingEntity.getPersistentData().putInt("misfortuneRedirectionLightning", livingEntity.getPersistentData().getInt("misfortuneRedirectionLightning") - 1);
            }
            if (livingEntity.getPersistentData().getInt("misfortuneRedirectionMCLightning") >= 1) {
                livingEntity.getPersistentData().putInt("misfortuneRedirectionMCLightning", livingEntity.getPersistentData().getInt("misfortuneRedirectionMCLightning") - 1);
                LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, livingEntity.level());
                lightningBolt.teleportTo(lightningBolt.getX(), lightningBolt.getY(), lightningBolt.getZ());
                lightningBolt.setDamage(12);
                livingEntity.level().addFreshEntity(lightningBolt);
            }
        }
    }

    public static void undeadArmy(LivingEntity livingEntity) {
        int x = (int) (livingEntity.getX() + (Math.random() * 40) - 20);
        int z = (int) (livingEntity.getX() + (Math.random() * 40) - 20);
        int surfaceY = livingEntity.level().getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + 1;
        Random random = new Random();
        int enhancement = CalamityEnhancementData.getInstance((ServerLevel) livingEntity.level()).getCalamityEnhancement();
        ItemStack leatherHelmet = new ItemStack(Items.LEATHER_HELMET);
        ItemStack leatherChestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
        ItemStack leatherLeggings = new ItemStack(Items.LEATHER_LEGGINGS);
        ItemStack leatherBoots = new ItemStack(Items.LEATHER_BOOTS);
        ItemStack ironHelmet = new ItemStack(Items.IRON_HELMET);
        ItemStack ironChestplate = new ItemStack(Items.IRON_CHESTPLATE);
        ItemStack ironLeggings = new ItemStack(Items.IRON_LEGGINGS);
        ItemStack ironBoots = new ItemStack(Items.IRON_BOOTS);
        ItemStack diamondHelmet = new ItemStack(Items.DIAMOND_HELMET);
        ItemStack diamondChestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
        ItemStack diamondLeggings = new ItemStack(Items.DIAMOND_LEGGINGS);
        ItemStack diamondBoots = new ItemStack(Items.DIAMOND_BOOTS);
        ItemStack netheriteHelmet = new ItemStack(Items.NETHERITE_HELMET);
        ItemStack netheriteChestplate = new ItemStack(Items.NETHERITE_CHESTPLATE);
        ItemStack netheriteLeggings = new ItemStack(Items.NETHERITE_LEGGINGS);
        ItemStack netheriteBoots = new ItemStack(Items.NETHERITE_BOOTS);
        ItemStack enchantedBow = new ItemStack(Items.BOW);
        ItemStack woodSword = new ItemStack(Items.WOODEN_SWORD);
        ItemStack ironSword = new ItemStack(Items.IRON_SWORD);
        ItemStack diamondSword = new ItemStack(Items.DIAMOND_SWORD);
        ItemStack netheriteSword = new ItemStack(Items.NETHERITE_SWORD);
        Zombie zombie = new Zombie(EntityType.ZOMBIE, livingEntity.level());
        Skeleton skeleton = new Skeleton(EntityType.SKELETON, livingEntity.level());
        for (int i = 0; i < enhancement; i++) {
            int randomPos = (int) ((Math.random() * 24) - 12);
            if (random.nextInt(10) == 10) {
                zombie.setTarget(livingEntity);
                livingEntity.level().addFreshEntity(zombie);
            }
            if (random.nextInt(10) == 9) {
                zombie.setPos(x + randomPos, surfaceY, z + randomPos);
                zombie.setItemSlot(EquipmentSlot.HEAD, leatherHelmet);
                zombie.setItemSlot(EquipmentSlot.CHEST, leatherChestplate);
                zombie.setItemSlot(EquipmentSlot.LEGS, leatherLeggings);
                zombie.setItemSlot(EquipmentSlot.FEET, leatherBoots);
                zombie.setItemSlot(EquipmentSlot.MAINHAND, woodSword);
                zombie.setTarget(livingEntity);
                livingEntity.level().addFreshEntity(zombie);
            }
            if (random.nextInt(10) == 8) {
                zombie.setPos(x + randomPos, surfaceY, z + randomPos);
                zombie.setItemSlot(EquipmentSlot.HEAD, ironHelmet);
                zombie.setItemSlot(EquipmentSlot.CHEST, ironChestplate);
                zombie.setItemSlot(EquipmentSlot.LEGS, ironLeggings);
                zombie.setItemSlot(EquipmentSlot.FEET, ironBoots);
                zombie.setItemSlot(EquipmentSlot.MAINHAND, ironSword);
                zombie.setTarget(livingEntity);
                livingEntity.level().addFreshEntity(zombie);
            }
            if (random.nextInt(10) == 7) {
                zombie.setPos(x + randomPos, surfaceY, z + randomPos);
                zombie.setItemSlot(EquipmentSlot.HEAD, diamondHelmet);
                zombie.setItemSlot(EquipmentSlot.CHEST, diamondChestplate);
                zombie.setItemSlot(EquipmentSlot.LEGS, diamondLeggings);
                zombie.setItemSlot(EquipmentSlot.FEET, diamondBoots);
                zombie.setItemSlot(EquipmentSlot.MAINHAND, diamondSword);
                zombie.setTarget(livingEntity);
                livingEntity.level().addFreshEntity(zombie);
            }
            if (random.nextInt(10) == 6) {
                zombie.setPos(x + randomPos, surfaceY, z + randomPos);
                zombie.setItemSlot(EquipmentSlot.HEAD, netheriteHelmet);
                zombie.setItemSlot(EquipmentSlot.CHEST, netheriteChestplate);
                zombie.setItemSlot(EquipmentSlot.LEGS, netheriteLeggings);
                zombie.setItemSlot(EquipmentSlot.FEET, netheriteBoots);
                zombie.setItemSlot(EquipmentSlot.MAINHAND, netheriteSword);
                zombie.setTarget(livingEntity);
                livingEntity.level().addFreshEntity(zombie);
            }
            if (random.nextInt(20) == 5) {
                skeleton.setPos(x + randomPos, surfaceY, z + randomPos);
                skeleton.setItemSlot(EquipmentSlot.MAINHAND, enchantedBow);
                zombie.setTarget(livingEntity);
                livingEntity.level().addFreshEntity(skeleton);
            }
            if (random.nextInt(20) == 4) {
                skeleton.setPos(x + randomPos, surfaceY, z + randomPos);
                skeleton.setItemSlot(EquipmentSlot.HEAD, leatherHelmet);
                skeleton.setItemSlot(EquipmentSlot.CHEST, leatherChestplate);
                skeleton.setItemSlot(EquipmentSlot.LEGS, leatherLeggings);
                skeleton.setItemSlot(EquipmentSlot.FEET, leatherBoots);
                enchantedBow.enchant(Enchantments.POWER_ARROWS, 1);
                skeleton.setItemSlot(EquipmentSlot.MAINHAND, enchantedBow);
                zombie.setTarget(livingEntity);
                livingEntity.level().addFreshEntity(skeleton);
            }
            if (random.nextInt(20) == 3) {
                skeleton.setPos(x + randomPos, surfaceY, z + randomPos);
                skeleton.setItemSlot(EquipmentSlot.HEAD, ironHelmet);
                skeleton.setItemSlot(EquipmentSlot.CHEST, ironChestplate);
                skeleton.setItemSlot(EquipmentSlot.LEGS, ironLeggings);
                skeleton.setItemSlot(EquipmentSlot.FEET, ironBoots);
                enchantedBow.enchant(Enchantments.POWER_ARROWS, 2);
                skeleton.setItemSlot(EquipmentSlot.MAINHAND, enchantedBow);
                zombie.setTarget(livingEntity);
                livingEntity.level().addFreshEntity(skeleton);
            }
            if (random.nextInt(20) == 2) {
                skeleton.setPos(x + randomPos, surfaceY, z + randomPos);
                skeleton.setItemSlot(EquipmentSlot.HEAD, diamondHelmet);
                skeleton.setItemSlot(EquipmentSlot.CHEST, diamondChestplate);
                skeleton.setItemSlot(EquipmentSlot.LEGS, diamondLeggings);
                skeleton.setItemSlot(EquipmentSlot.FEET, diamondBoots);
                enchantedBow.enchant(Enchantments.POWER_ARROWS, 3);
                skeleton.setItemSlot(EquipmentSlot.MAINHAND, enchantedBow);
                zombie.setTarget(livingEntity);
                livingEntity.level().addFreshEntity(skeleton);
            }
            if (random.nextInt(20) == 1) {
                skeleton.setPos(x + randomPos, surfaceY, z + randomPos);
                skeleton.setItemSlot(EquipmentSlot.HEAD, netheriteHelmet);
                skeleton.setItemSlot(EquipmentSlot.CHEST, netheriteChestplate);
                skeleton.setItemSlot(EquipmentSlot.LEGS, netheriteLeggings);
                skeleton.setItemSlot(EquipmentSlot.FEET, netheriteBoots);
                enchantedBow.enchant(Enchantments.POWER_ARROWS, 4);
                skeleton.setItemSlot(EquipmentSlot.MAINHAND, enchantedBow);
                zombie.setTarget(livingEntity);
                livingEntity.level().addFreshEntity(skeleton);
            }
            zombie.setDropChance(EquipmentSlot.HEAD, 0.0F);
            zombie.setDropChance(EquipmentSlot.CHEST, 0.0F);
            zombie.setDropChance(EquipmentSlot.LEGS, 0.0F);
            zombie.setDropChance(EquipmentSlot.FEET, 0.0F);
            skeleton.setDropChance(EquipmentSlot.HEAD, 0.0F);
            skeleton.setDropChance(EquipmentSlot.CHEST, 0.0F);
            skeleton.setDropChance(EquipmentSlot.LEGS, 0.0F);
            skeleton.setDropChance(EquipmentSlot.FEET, 0.0F);
        }
    }
}