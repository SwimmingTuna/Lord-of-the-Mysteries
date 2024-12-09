package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class LuckFutureTelling extends SimpleAbilityItem {
    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public LuckFutureTelling(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 5, 50, 60,80,80);
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 80, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 80, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use on a living entity, plants a plague seed in the target, sprouting after 30 seconds and dealing massive damage to it and all entities around it, be careful as this can effect the user\n" +
                "Spirituality Used: 200\n" +
                "Cooldown: 5 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            if (!checkAll(player)) {
                return InteractionResult.FAIL;
            }
            addCooldown(player);
            useSpirituality(player);
            luckFutureTell(player, interactionTarget);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            if (!checkAll(player)) {
                return InteractionResult.FAIL;
            }
            useSpirituality(player);
            luckFutureTellPlayer(player);
            addCooldown(player);
        }
        return InteractionResult.SUCCESS;
    }

    public static void luckFutureTell(Player pPlayer, LivingEntity interactionTarget) {
        CompoundTag tag = interactionTarget.getPersistentData();
        int meteor = tag.getInt("luckMeteor");
        int doubleDamage = tag.getInt("luckDoubleDamage");
        int ignoreDamage = tag.getInt("luckIgnoreDamage");
        int halveDamage = tag.getInt("luckHalveDamage");
        int lotmLightning = tag.getInt("luckLightningLOTM");
        int paralysis = tag.getInt("luckParalysis");
        int unequipArmor = tag.getInt("luckUnequipArmor");
        int wardenSpawn = tag.getInt("luckWarden");
        int mcLightning = tag.getInt("luckLightningMC");
        int poison = tag.getInt("luckPoison");
        int attackerPoisoned = tag.getInt("luckAttackerPoisoned");
        int tornadoInt = tag.getInt("luckTornado");
        int stone = tag.getInt("luckStone");
        int luckIgnoreMobs = tag.getInt("luckIgnoreMobs");
        int regeneration = tag.getInt("luckRegeneration");
        int diamondsDropped = tag.getInt("luckDiamonds");
        int windMovingProjectiles = tag.getInt("windMovingProjectilesCounter");
        if (meteor == 1) {
            pPlayer.sendSystemMessage(Component.literal("A meteor will drop on their head in one second").withStyle(ChatFormatting.DARK_RED));
        }
        if (meteor >= 2) {
            pPlayer.sendSystemMessage(Component.literal("A meteor will drop on their head in " + meteor + " seconds").withStyle(ChatFormatting.DARK_RED));
        }
        if (doubleDamage == 1) {
            pPlayer.sendSystemMessage(Component.literal("Their next damage will be doubled").withStyle(ChatFormatting.RED));
        }
        if (doubleDamage >= 2) {
            pPlayer.sendSystemMessage(Component.literal("Their next " + doubleDamage + "damage instances will be doubled").withStyle(ChatFormatting.RED));
        }
        if (ignoreDamage == 1) {
            pPlayer.sendSystemMessage(Component.literal("They will ignore their next damage").withStyle(ChatFormatting.GREEN));
        }
        if (ignoreDamage >= 2) {
            pPlayer.sendSystemMessage(Component.literal("They will ignore their next " + ignoreDamage + " damage instances").withStyle(ChatFormatting.GREEN));
        }
        if (halveDamage == 1) {
            pPlayer.sendSystemMessage(Component.literal("They will halve their next damage").withStyle(ChatFormatting.GREEN));
        }
        if (halveDamage >= 2) {
            pPlayer.sendSystemMessage(Component.literal("They will halve their next " + halveDamage + " damage instances").withStyle(ChatFormatting.GREEN));
        }
        if (lotmLightning == 1) {
            pPlayer.sendSystemMessage(Component.literal("A strenghtened lightning will strike them in one second").withStyle(ChatFormatting.RED));
        }
        if (lotmLightning >= 2) {
            pPlayer.sendSystemMessage(Component.literal("A strengthened lightning will strike them in " + lotmLightning + " seconds").withStyle(ChatFormatting.RED));
        }
        if (paralysis == 1) {
            pPlayer.sendSystemMessage(Component.literal("They will be paralyzed temporarily in one second").withStyle(ChatFormatting.RED));
        }
        if (paralysis >= 2) {
            pPlayer.sendSystemMessage(Component.literal("They will be be paralyzed temporarily in " + paralysis + " seconds").withStyle(ChatFormatting.RED));
        }
        if (unequipArmor == 1) {
            pPlayer.sendSystemMessage(Component.literal("Their armor will be unequipped in one second").withStyle(ChatFormatting.RED));
        }
        if (unequipArmor >= 2) {
            pPlayer.sendSystemMessage(Component.literal("Their armor will be unequipped in " + unequipArmor + "seconds").withStyle(ChatFormatting.RED));
        }
        if (wardenSpawn == 1) {
            pPlayer.sendSystemMessage(Component.literal("A warden will spawn at their location in one seconds").withStyle(ChatFormatting.RED));
        }
        if (wardenSpawn >= 2) {
            pPlayer.sendSystemMessage(Component.literal("A warden will spawn at their location in " + wardenSpawn + " seconds").withStyle(ChatFormatting.RED));
        }
        if (mcLightning == 1) {
            pPlayer.sendSystemMessage(Component.literal("A lightning bolt will strike them the next time they're at low health").withStyle(ChatFormatting.RED));
        }
        if (mcLightning >= 2) {
            pPlayer.sendSystemMessage(Component.literal("A lightning bolt will strike them the next time they're low " + mcLightning + " second's onward").withStyle(ChatFormatting.RED));
        }
        if (poison == 1) {
            pPlayer.sendSystemMessage(Component.literal("They will be poisoned in one second").withStyle(ChatFormatting.RED));
        }
        if (poison >= 2) {
            pPlayer.sendSystemMessage(Component.literal("They will be poisoned in " + poison + "seconds").withStyle(ChatFormatting.RED));
        }
        if (attackerPoisoned == 1) {
            pPlayer.sendSystemMessage(Component.literal("Their next attacker will be poisoned").withStyle(ChatFormatting.GREEN));
        }
        if (attackerPoisoned >= 2) {
            pPlayer.sendSystemMessage(Component.literal("Their next " + attackerPoisoned + " attackers will be poisoned").withStyle(ChatFormatting.GREEN));
        }
        if (tornadoInt == 1) {
            pPlayer.sendSystemMessage(Component.literal("A tornado will spawn at their location in one second").withStyle(ChatFormatting.DARK_RED));
        }
        if (tornadoInt >= 2) {
            pPlayer.sendSystemMessage(Component.literal("A tornado will spawn at their location in " + tornadoInt + " seconds").withStyle(ChatFormatting.DARK_RED));
        }
        if (stone == 1) {
            pPlayer.sendSystemMessage(Component.literal("A stone block will drop on their head in one second").withStyle(ChatFormatting.RED));
        }
        if (stone >= 2) {
            pPlayer.sendSystemMessage(Component.literal("A stone block will drop on their head in " + stone + "seconds").withStyle(ChatFormatting.RED));
        }
        if (luckIgnoreMobs == 1) {
            pPlayer.sendSystemMessage(Component.literal("The next time they're targeted by a mob, the mob will ignore them").withStyle(ChatFormatting.GREEN));
        }
        if (luckIgnoreMobs >= 2) {
            pPlayer.sendSystemMessage(Component.literal("The next " + luckIgnoreMobs + " times they're targeted by a mob, the mob will ignore them").withStyle(ChatFormatting.GREEN));
        }
        if (regeneration == 1) {
            pPlayer.sendSystemMessage(Component.literal("The next time they're at low health, they'll gain rapid regeneration").withStyle(ChatFormatting.GREEN));
        }
        if (diamondsDropped == 1) {
            pPlayer.sendSystemMessage(Component.literal("Diamonds will drop at their feet in one second").withStyle(ChatFormatting.GREEN));
        }
        if (diamondsDropped >= 2) {
            pPlayer.sendSystemMessage(Component.literal("Diamonds will drop at their feet in " + diamondsDropped + " seconds").withStyle(ChatFormatting.GREEN));
        }
        if (windMovingProjectiles == 1) {
            pPlayer.sendSystemMessage(Component.literal("The next time a projectile is heading towards them, the wind will move it").withStyle(ChatFormatting.GREEN));
        }
        if (windMovingProjectiles >= 2) {
            pPlayer.sendSystemMessage(Component.literal("The next " + windMovingProjectiles + " times a projectile is heading towards them, the wind will move it ").withStyle(ChatFormatting.GREEN));
        }
    }

    public static void luckFutureTellPlayer(Player pPlayer) {
        CompoundTag tag = pPlayer.getPersistentData();
        int meteor = tag.getInt("luckMeteor");
        int doubleDamage = tag.getInt("luckDoubleDamage");
        int ignoreDamage = tag.getInt("luckIgnoreDamage");
        int halveDamage = tag.getInt("luckHalveDamage");
        int lotmLightning = tag.getInt("luckLightningLOTM");
        int paralysis = tag.getInt("luckParalysis");
        int unequipArmor = tag.getInt("luckUnequipArmor");
        int wardenSpawn = tag.getInt("luckWarden");
        int mcLightning = tag.getInt("luckLightningMC");
        int poison = tag.getInt("luckPoison");
        int attackerPoisoned = tag.getInt("luckAttackerPoisoned");
        int tornadoInt = tag.getInt("luckTornado");
        int stone = tag.getInt("luckStone");
        int luckIgnoreMobs = tag.getInt("luckIgnoreMobs");
        int regeneration = tag.getInt("luckRegeneration");
        int diamondsDropped = tag.getInt("luckDiamonds");
        int windMovingProjectiles = tag.getInt("windMovingProjectilesCounter");
        if (meteor == 1) {
            pPlayer.sendSystemMessage(Component.literal("A meteor will drop on your head in one second").withStyle(ChatFormatting.DARK_RED));
        }
        if (meteor >= 2) {
            pPlayer.sendSystemMessage(Component.literal("A meteor will drop on your head in " + meteor + " seconds").withStyle(ChatFormatting.DARK_RED));
        }
        if (doubleDamage == 1) {
            pPlayer.sendSystemMessage(Component.literal("Your next damage will be doubled").withStyle(ChatFormatting.RED));
        }
        if (doubleDamage >= 2) {
            pPlayer.sendSystemMessage(Component.literal("Your next " + doubleDamage + "damage instances will be doubled").withStyle(ChatFormatting.RED));
        }
        if (ignoreDamage == 1) {
            pPlayer.sendSystemMessage(Component.literal("You will ignore your next damage").withStyle(ChatFormatting.GREEN));
        }
        if (ignoreDamage >= 2) {
            pPlayer.sendSystemMessage(Component.literal("You will ignore your next " + ignoreDamage + " damage instances").withStyle(ChatFormatting.GREEN));
        }
        if (halveDamage == 1) {
            pPlayer.sendSystemMessage(Component.literal("You will halve your next damage").withStyle(ChatFormatting.GREEN));
        }
        if (halveDamage >= 2) {
            pPlayer.sendSystemMessage(Component.literal("You will halve your next " + halveDamage + " damage instances").withStyle(ChatFormatting.GREEN));
        }
        if (lotmLightning == 1) {
            pPlayer.sendSystemMessage(Component.literal("A strenghtened lightning will strike you in one second").withStyle(ChatFormatting.RED));
        }
        if (lotmLightning >= 2) {
            pPlayer.sendSystemMessage(Component.literal("A strengthened lightning will strike you in " + lotmLightning + " seconds").withStyle(ChatFormatting.RED));
        }
        if (paralysis == 1) {
            pPlayer.sendSystemMessage(Component.literal("You will be paralyzed temporarily in one second").withStyle(ChatFormatting.RED));
        }
        if (paralysis >= 2) {
            pPlayer.sendSystemMessage(Component.literal("You will be be paralyzed temporarily in " + paralysis + " seconds").withStyle(ChatFormatting.RED));
        }
        if (unequipArmor == 1) {
            pPlayer.sendSystemMessage(Component.literal("Your armor will be unequipped in one second").withStyle(ChatFormatting.RED));
        }
        if (unequipArmor >= 2) {
            pPlayer.sendSystemMessage(Component.literal("Your armor will be unequipped in " + unequipArmor + "seconds").withStyle(ChatFormatting.RED));
        }
        if (wardenSpawn == 1) {
            pPlayer.sendSystemMessage(Component.literal("A warden will spawn at your location in one seconds").withStyle(ChatFormatting.RED));
        }
        if (wardenSpawn >= 2) {
            pPlayer.sendSystemMessage(Component.literal("A warden will spawn at your location in " + wardenSpawn + " seconds").withStyle(ChatFormatting.RED));
        }
        if (mcLightning == 1) {
            pPlayer.sendSystemMessage(Component.literal("A lightning bolt will strike you the next time you're at low health").withStyle(ChatFormatting.RED));
        }
        if (mcLightning >= 2) {
            pPlayer.sendSystemMessage(Component.literal("A lightning bolt will strike you the next time you're at low health " + mcLightning + " second's onward").withStyle(ChatFormatting.RED));
        }
        if (poison == 1) {
            pPlayer.sendSystemMessage(Component.literal("You will be poisoned in one second").withStyle(ChatFormatting.RED));
        }
        if (poison >= 2) {
            pPlayer.sendSystemMessage(Component.literal("You will be poisoned in " + poison + "seconds").withStyle(ChatFormatting.RED));
        }
        if (attackerPoisoned == 1) {
            pPlayer.sendSystemMessage(Component.literal("Your next attacker will be poisoned").withStyle(ChatFormatting.GREEN));
        }
        if (attackerPoisoned >= 2) {
            pPlayer.sendSystemMessage(Component.literal("Your next " + attackerPoisoned + " attackers will be poisoned").withStyle(ChatFormatting.GREEN));
        }
        if (tornadoInt == 1) {
            pPlayer.sendSystemMessage(Component.literal("A tornado will spawn at your location in one second").withStyle(ChatFormatting.DARK_RED));
        }
        if (tornadoInt >= 2) {
            pPlayer.sendSystemMessage(Component.literal("A tornado will spawn at your location in " + tornadoInt + " seconds").withStyle(ChatFormatting.DARK_RED));
        }
        if (stone == 1) {
            pPlayer.sendSystemMessage(Component.literal("A stone block will drop on your head in one second").withStyle(ChatFormatting.RED));
        }
        if (stone >= 2) {
            pPlayer.sendSystemMessage(Component.literal("A stone block will drop on your head in " + stone + "seconds").withStyle(ChatFormatting.RED));
        }
        if (luckIgnoreMobs == 1) {
            pPlayer.sendSystemMessage(Component.literal("The next time you're targeted by a mob, the mob will ignore you").withStyle(ChatFormatting.GREEN));
        }
        if (luckIgnoreMobs >= 2) {
            pPlayer.sendSystemMessage(Component.literal("The next " + luckIgnoreMobs + " times  you're targeted by a mob, the mob will ignore you").withStyle(ChatFormatting.GREEN));
        }
        if (regeneration == 1) {
            pPlayer.sendSystemMessage(Component.literal("The next time you're at low health, you'll gain rapid regeneration").withStyle(ChatFormatting.GREEN));
        }
        if (diamondsDropped == 1) {
            pPlayer.sendSystemMessage(Component.literal("Diamonds will drop at your feet in one second").withStyle(ChatFormatting.GREEN));
        }
        if (diamondsDropped >= 2) {
            pPlayer.sendSystemMessage(Component.literal("Diamonds will drop at your feet in " + diamondsDropped + " seconds").withStyle(ChatFormatting.GREEN));
        }
        if (windMovingProjectiles == 1) {
            pPlayer.sendSystemMessage(Component.literal("The next time a projectile is heading towards you, the wind will move it").withStyle(ChatFormatting.GREEN));
        }
        if (windMovingProjectiles >= 2) {
            pPlayer.sendSystemMessage(Component.literal("The next " + windMovingProjectiles + " times a projectile is heading towards you, the wind will move it ").withStyle(ChatFormatting.GREEN));
        }
    }
}
