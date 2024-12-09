package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.entity.MeteorNoLevelEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class MeteorNoLevelShower extends SimpleAbilityItem {

    public MeteorNoLevelShower(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 1, 2500, 900);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        meteorNoLevelShower(player);
        return InteractionResult.SUCCESS;
    }

    public static void meteorNoLevelShower(Player player) {
        if (!player.level().isClientSide()) {
            MeteorNoLevelEntity.summonMultipleMeteors(player);
            MeteorNoLevelEntity.summonMultipleMeteors(player);
            MeteorNoLevelEntity.summonMultipleMeteors(player);
            MeteorNoLevelEntity.summonMultipleMeteors(player);
            MeteorNoLevelEntity.summonMultipleMeteors(player);
            MeteorNoLevelEntity.summonMultipleMeteors(player);
            MeteorNoLevelEntity.summonMultipleMeteors(player);
            MeteorNoLevelEntity.summonMultipleMeteors(player);
            MeteorNoLevelEntity.summonMultipleMeteors(player);
        }
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, summons a meteor shower in the direction you're looking which won't destroy blocks"));
        tooltipComponents.add(Component.literal("Left Click for Meteor Shower (Block Destruction)"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("2500").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("45 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
