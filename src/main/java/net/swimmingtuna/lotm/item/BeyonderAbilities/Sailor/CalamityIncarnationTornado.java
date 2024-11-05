package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class CalamityIncarnationTornado extends SimpleAbilityItem {


    public CalamityIncarnationTornado(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 2,800,800);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        calamityIncarnationTornado(player);
        addCooldown(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }


    public static void calamityIncarnationTornado(Player player) {
        if (!player.level().isClientSide()) {
            player.getPersistentData().putInt("calamityIncarnationTornado", 300);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, turns into a tornado, picking up both blocks and entities\n" +
                "Spirituality Used: 300\n" +
                "Cooldown: 40 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
