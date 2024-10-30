package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class CalamityIncarnationTsunami extends SimpleAbilityItem {

    public CalamityIncarnationTsunami(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 2,1000,1000);
    }
    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        calamityIncarnationTsunami(player);
        return InteractionResult.SUCCESS;
    }

    public static void calamityIncarnationTsunami(Player player) {
        int x = player.getPersistentData().getInt("calamityIncarnationTsunami");
        if (x == 0) {
            player.getPersistentData().putInt("calamityIncarnationTsunami", 200);
        } else {
            player.getPersistentData().putInt("calamityIncarnationTsunami", 0);
            player.displayClientMessage(Component.literal("Tsunami Incarnation Cancelled").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
        }
    }
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, become a tsunami, turning into a massive sphere of water\n" +
                "Spirituality Used: 1000\n" +
                "Cooldown: 50 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
