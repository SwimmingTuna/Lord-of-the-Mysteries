package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class AquaticLifeManipulation extends SimpleAbilityItem {

    public AquaticLifeManipulation(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 3, 125, 200);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        aquaticLifeManipulation(player);
        addCooldown(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    public static void aquaticLifeManipulation(Player player) {
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            int sequence = holder.getCurrentSequence();
            if (player.level().isClientSide()) {
                return;
            }
            List<LivingEntity> aquaticEntities = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(50), entity -> entity instanceof WaterAnimal);
            if (aquaticEntities.isEmpty()) {
                return;
            }
            LivingEntity nearestAquaticEntity = aquaticEntities.stream().min(Comparator.comparingDouble(player::distanceTo)).orElse(null);
            List<Player> nearbyPlayers = nearestAquaticEntity.level().getEntitiesOfClass(Player.class, nearestAquaticEntity.getBoundingBox().inflate(200 - (sequence * 20)));
            Player nearestPlayer = nearbyPlayers.stream().filter(nearbyPlayer -> nearbyPlayer != player).min(Comparator.comparingDouble(nearestAquaticEntity::distanceTo)).orElse(null);
            if (nearestPlayer == null) {
                return;
            }
            if (sequence >= 2) {
                player.sendSystemMessage(Component.literal("Nearest Player is " + nearestPlayer.getName().getString() + ". Pathway is " + holder.getCurrentClass()).withStyle(BeyonderUtil.getStyle(player)));
            } else {
                player.sendSystemMessage(Component.literal("Nearest Player is " + nearestPlayer.getName().getString() + ". Pathway is " + holder.getCurrentClass().sequenceNames().get(holder.getCurrentSequence()) + " sequence " + holder.getCurrentSequence()).withStyle(BeyonderUtil.getStyle(player)));
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, communicates with all aquatic life near you to get information about the nearest player and their pathway."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("125").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("10 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
