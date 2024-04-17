package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DreamWeaving extends Item implements ReachChangeUUIDs {
    private final LazyOptional<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = LazyOptional.of(() -> createAttributeMap());

    public DreamWeaving(Properties pProperties) {
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(BeyonderEntityReach, "Reach modifier", 300, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(BeyonderBlockReach, "Reach modifier", 300, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use on a living entity, brings their nightmares into reality, giving them darkness temporarily and summoning a random array of mobs around the target\n" +
                    "Spirituality Used: 250\n" +
                    "Cooldown: 8 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player pPlayer = event.getEntity();
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSpectatorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 250) {
                pPlayer.displayClientMessage(Component.literal("You need 250 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
        }
        Level level = pPlayer.level();
        ItemStack itemStack = pPlayer.getItemInHand(event.getHand());
        LivingEntity targetEntity = (LivingEntity) event.getTarget();
        double x = targetEntity.getX();
        double y = targetEntity.getY();
        double z = targetEntity.getZ();
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
            if (holder.isSpectatorClass() && !pPlayer.level().isClientSide && !targetEntity.level().isClientSide && itemStack.getItem() instanceof DreamWeaving && targetEntity instanceof LivingEntity && spectatorSequence.getCurrentSequence() <= 3 && spectatorSequence.useSpirituality(250)) {
                (targetEntity).addEffect(new MobEffectInstance(MobEffects.DARKNESS, 150, 1, false, false));
                Random random = new Random();
                int times = 20 - (spectatorSequence.getCurrentSequence() * 3);
                for (int i = 0; i < times; i++) {
                    int randomNumber = random.nextInt(10);
                    if (randomNumber == 0) {
                        if (dreamIntoReality.getValue() == 2) {
                            spawnZombiesAroundTarget(targetEntity, level, x, y, z, 2);
                        } else {
                            spawnZombiesAroundTarget(targetEntity, level, x, y, z, 1);
                        }
                    }
                    if (randomNumber == 1) {
                        if (dreamIntoReality.getValue() == 2) {
                            spawnSkeletonAroundTarget(targetEntity, level, x, y, z, 2);
                        } else {
                            spawnSkeletonAroundTarget(targetEntity, level, x, y, z, 1);
                        }
                    }
                    if (randomNumber == 2) {
                        if (dreamIntoReality.getValue() == 2) {
                            spawnCreeperAroundTarget(targetEntity, level, x, y, z, 2);
                        } else {
                            spawnCreeperAroundTarget(targetEntity, level, x, y, z, 1);

                        }
                    }
                    if (randomNumber == 3) {
                        if (dreamIntoReality.getValue() == 2) {
                            spawnEndermanAroundTarget(targetEntity, level, x, y, z, 2);
                        } else {
                            spawnEndermanAroundTarget(targetEntity, level, x, y, z, 1);
                        }
                    }
                    if (randomNumber == 4) {
                        if (dreamIntoReality.getValue() == 2) {
                            spawnRavagerAroundTarget(targetEntity, level, x, y, z, 2);
                        } else {
                            spawnRavagerAroundTarget(targetEntity, level, x, y, z, 1);
                        }
                    }
                    if (randomNumber == 5) {
                        if (dreamIntoReality.getValue() == 2) {
                            spawnVexAroundTarget(targetEntity, level, x, y, z, 2);
                        } else {
                            spawnVexAroundTarget(targetEntity, level, x, y, z, 1);
                        }
                    }
                    if (randomNumber == 6) {
                        if (dreamIntoReality.getValue() == 2) {
                            spawnEndermiteAroundTarget(targetEntity, level, x, y, z, 2);
                        } else {
                            spawnEndermiteAroundTarget(targetEntity, level, x, y, z, 1);
                        }
                    }
                    if (randomNumber == 7) {
                        if (dreamIntoReality.getValue() == 2) {
                            spawnSpidersAroundTarget(targetEntity, level, x, y, z, 2);
                        } else {
                            spawnSpidersAroundTarget(targetEntity, level, x, y, z, 1);
                        }
                    }
                    if (randomNumber == 8) {
                        if (dreamIntoReality.getValue() == 2) {
                            spawnWitherAroundTarget(targetEntity, level, x, y, z, 2);
                        } else {
                            spawnWitherAroundTarget(targetEntity, level, x, y, z, 1);
                        }
                    }
                    if (randomNumber == 9) {
                        if (dreamIntoReality.getValue() == 2) {
                            spawnPhantomAroundTarget(targetEntity, level, x, y, z, 2);
                        } else {
                            spawnPhantomAroundTarget(targetEntity, level, x, y, z, 1);
                        }
                    }
                    if (!pPlayer.getAbilities().instabuild) {
                        pPlayer.getCooldowns().addCooldown(itemStack.getItem(), (int) (160 / dreamIntoReality.getValue()));
                }
                }
            }
        });
    }
    private static void spawnZombiesAroundTarget(LivingEntity entity, Level level, double x, double y, double z, int numberOfZombies) {
        for (int i = 0; i < numberOfZombies; i++) {
            Zombie zombie = new Zombie(EntityType.ZOMBIE, level);
            AttributeInstance maxHP = zombie.getAttribute(Attributes.MAX_HEALTH);
            spawnEntityInRadius(zombie, level, x, y, z);
            maxHP.setBaseValue(551);
            zombie.setTarget(entity);
        }
    }
    private static void spawnSpidersAroundTarget(LivingEntity entity, Level level, double x, double y, double z, int numberOfSpiders) {
        for (int i = 0; i < numberOfSpiders; i++) {
            CaveSpider caveSpider = new CaveSpider(EntityType.CAVE_SPIDER, level);
            AttributeInstance maxHP = caveSpider.getAttribute(Attributes.MAX_HEALTH);
            spawnEntityInRadius(caveSpider, level, x, y, z);
            maxHP.setBaseValue(551);
            caveSpider.setTarget(entity);;
        }
    }
    private static void spawnSkeletonAroundTarget(LivingEntity entity, Level level, double x, double y, double z, int numberOfSkeleton) {
        for (int i = 0; i < numberOfSkeleton; i++) {
            Skeleton skeleton = new Skeleton(EntityType.SKELETON, level);
            AttributeInstance maxHP = skeleton.getAttribute(Attributes.MAX_HEALTH);
            spawnEntityInRadius(skeleton, level, x, y, z);
            maxHP.setBaseValue(551);
            skeleton.setTarget(entity);
        }
    }
    private static void spawnWitherAroundTarget(LivingEntity entity, Level level, double x, double y, double z, int numberOfWithers) {
        for (int i = 0; i < numberOfWithers; i++) {
            WitherBoss wither = new WitherBoss(EntityType.WITHER, level);
            AttributeInstance maxHP = wither.getAttribute(Attributes.MAX_HEALTH);
            spawnEntityInRadius(wither, level, x, y, z);
            maxHP.setBaseValue(551);
            wither.setTarget(entity);
        }
    }
    private static void spawnPhantomAroundTarget(LivingEntity entity, Level level, double x, double y, double z, int numberOfPhantoms) {
        for (int i = 0; i < numberOfPhantoms; i++) {
            Phantom phantom = new Phantom(EntityType.PHANTOM, level);
            AttributeInstance maxHP = phantom.getAttribute(Attributes.MAX_HEALTH);
            spawnEntityInRadius(phantom, level, x, y, z);
            maxHP.setBaseValue(551);
            phantom.setTarget(entity);
        }
    }
    private static void spawnEndermiteAroundTarget(LivingEntity entity, Level level, double x, double y, double z, int numberOfEndermites) {
        for (int i = 0; i < numberOfEndermites; i++) {
            Endermite endermite = new Endermite(EntityType.ENDERMITE, level);
            AttributeInstance maxHP = endermite.getAttribute(Attributes.MAX_HEALTH);
            spawnEntityInRadius(endermite, level, x, y, z);
            maxHP.setBaseValue(551);
            endermite.setTarget(entity);
        }
    }
    private static void spawnVexAroundTarget(LivingEntity entity, Level level, double x, double y, double z, int numberOfVex) {
        for (int i = 0; i < numberOfVex; i++) {
            Vex vex = new Vex(EntityType.VEX, level);
            AttributeInstance maxHP = vex.getAttribute(Attributes.MAX_HEALTH);
            spawnEntityInRadius(vex, level, x, y, z);
            maxHP.setBaseValue(551);
            vex.setTarget(entity);
        }
    }
    private static void spawnCreeperAroundTarget(LivingEntity entity, Level level, double x, double y, double z, int numberOfCreepers) {
        for (int i = 0; i < numberOfCreepers; i++) {
            Creeper creeper = new Creeper(EntityType.CREEPER, level);
            AttributeInstance maxHP = creeper.getAttribute(Attributes.MAX_HEALTH);
            spawnEntityInRadius(creeper, level, x, y, z);
            maxHP.setBaseValue(551);
            creeper.setTarget(entity);
        }
    }
    private static void spawnEndermanAroundTarget(LivingEntity entity, Level level, double x, double y, double z, int numberOfEndermen) {
        for (int i = 0; i < numberOfEndermen; i++) {
            EnderMan enderman = new EnderMan(EntityType.ENDERMAN, level);
            AttributeInstance maxHP = enderman.getAttribute(Attributes.MAX_HEALTH);
            spawnEntityInRadius(enderman, level, x, y, z);
            maxHP.setBaseValue(551);
            enderman.setTarget(entity);
        }
    }
    private static void spawnRavagerAroundTarget(LivingEntity entity, Level level, double x, double y, double z, int numberOfRavagers) {
        for (int i = 0; i < numberOfRavagers; i++) {
            Ravager ravager = new Ravager(EntityType.RAVAGER, level);
            AttributeInstance maxHP = ravager.getAttribute(Attributes.MAX_HEALTH);
            spawnEntityInRadius(ravager, level, x, y, z);
            maxHP.setBaseValue(551);
            ravager.setTarget(entity);
        }
    }
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        AttributeInstance maxHP = entity.getAttribute(Attributes.MAX_HEALTH);
        if (!(entity instanceof Player) && maxHP.getBaseValue() == 551) {
            int deathTimer = entity.getPersistentData().getInt("DeathTimer");
            entity.getPersistentData().putInt("DeathTimer", deathTimer + 1);
            if (deathTimer >= 300) {
                entity.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    private static void spawnEntityInRadius(Mob entity, Level level, double x, double y, double z) {
        Random random = new Random();
        double angle = random.nextDouble() * 2 * Math.PI;
        double xOffset = 10 * Math.cos(angle);
        double zOffset = 10 * Math.sin(angle);

        entity.moveTo(x + xOffset, y +1, z + zOffset);
        level.addFreshEntity(entity);
    }
}