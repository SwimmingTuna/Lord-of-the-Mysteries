package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ProphesizeTeleportBlock extends SimpleAbilityItem {

    public ProphesizeTeleportBlock(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 1, 600, 300,200,200);
    }

    @Override
    public InteractionResult useAbilityOnBlock(UseOnContext pContext) {
        Player player = pContext.getPlayer();
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        prophesizeTeleportBlock(pContext);
        return InteractionResult.SUCCESS;
    }

    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 200, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 200, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    public void prophesizeTeleportBlock(UseOnContext context) {
        Player player = context.getPlayer();
        if (!player.level().isClientSide()) {
            Level level = player.level();
            AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
            BlockPos positionClicked = context.getClickedPos();
            if (!context.getLevel().isClientSide) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && holder.getCurrentSequence() <= 1 && BeyonderHolderAttacher.getHolderUnwrap(player).useSpirituality(600)) {
                    teleportEntities(player, level, positionClicked, holder.getCurrentSequence(), (int) dreamIntoReality.getValue());
                }
            }
        }
    }


    private void teleportEntities(Player player, Level level, BlockPos targetPos, int sequence, int dir) {
        double radius = (500 - sequence * 100) * dir;
        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius))) {
            if (entity != player && !entity.level().isClientSide()) {
                entity.getPersistentData().putInt("prophesizeTeleportationCounter", (int) (300 * Math.random()));
                entity.getPersistentData().putInt("prophesizeTeleportX", (int) targetPos.getX());
                entity.getPersistentData().putInt("prophesizeTeleportY", (int) targetPos.getY() + 1);
                entity.getPersistentData().putInt("prophesizeTeleportZ", (int) targetPos.getZ());
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, makes all living entities around the user teleport to a clicked block\n" +
                "Spirituality Used: 600\n" +
                "Cooldown: 15 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
