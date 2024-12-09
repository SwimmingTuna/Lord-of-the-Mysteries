package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;


import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.WhisperOfCorruptionEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class WhisperOfCorruption extends SimpleAbilityItem {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public WhisperOfCorruption(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 2, 800, 300);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        whisper(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    private void whisper(Player player) {
        if (!player.level().isClientSide()) {
            WhisperOfCorruptionEntity.summonWhispersInLookVec(player, BeyonderHolderAttacher.getHolderUnwrap(player).getCurrentSequence());
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal(
                "Upon use, exude an aura of tyranny, not giving any entity permission to move, implanting fear strong enough to not allow them to use their abilities"
        ).withStyle(/*ChatFormatting.BOLD, ChatFormatting.BLUE*/));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
