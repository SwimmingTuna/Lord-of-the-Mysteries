package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DreamWeaving extends Item {
    private static final List<EntityType<? extends Mob>> MOB_TYPES = List.of(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.CREEPER,
            EntityType.ENDERMAN,
            EntityType.RAVAGER,
            EntityType.VEX,
            EntityType.ENDERMITE,
            EntityType.SPIDER,
            EntityType.WITHER,
            EntityType.PHANTOM
    );

    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public DreamWeaving(Properties properties) {
        super(properties);
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 300, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 300, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use on a living entity, brings their nightmares into reality, giving them darkness temporarily and summoning a random array of mobs around the target\n" +
                "Spirituality Used: 250\n" +
                "Cooldown: 8 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
            player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            return InteractionResult.FAIL;
        }
        if (!holder.useSpirituality(250)) {
            player.displayClientMessage(Component.literal("You need 250 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
            return InteractionResult.FAIL;
        }
        Level level = player.level();
        ItemStack itemInHand = player.getItemInHand(usedHand);
        double x = interactionTarget.getX();
        double y = interactionTarget.getY();
        double z = interactionTarget.getZ();
        if (holder.getCurrentSequence() > 3) {
            return InteractionResult.FAIL;
        }
        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
        interactionTarget.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 150, 1, false, false));
        RandomSource random = player.getRandom();
        int times = 20 - (holder.getCurrentSequence() * 3);
        for (int i = 0; i < times; i++) {
            int randomNumber = random.nextInt(10);
            EntityType<? extends Mob> entityType = MOB_TYPES.get(randomNumber);
            spawnMobsAroundTarget(entityType, interactionTarget, level, x, y, z, dreamIntoReality.getValue() == 2 ? 2 : 1);

            if (!player.getAbilities().instabuild) {
                player.getCooldowns().addCooldown(itemInHand.getItem(), (int) (160 / dreamIntoReality.getValue()));
            }
        }
        return InteractionResult.PASS;
    }

    private static void spawnMobsAroundTarget(EntityType<? extends Mob> mobEntityType, LivingEntity entity, Level level, double x, double y, double z, int numberOfMobs) {
        for (int i = 0; i < numberOfMobs; i++) {
            Mob mob = mobEntityType.create(level);
            AttributeInstance maxHp = mob.getAttribute(Attributes.MAX_HEALTH);
            spawnEntityInRadius(mob, level, x, y, z);
            maxHp.setBaseValue(551);
            mob.setTarget(entity);
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