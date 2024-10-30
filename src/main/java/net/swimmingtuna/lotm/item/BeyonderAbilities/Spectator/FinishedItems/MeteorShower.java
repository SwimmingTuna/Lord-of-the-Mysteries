package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.entity.MeteorEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class MeteorShower extends SimpleAbilityItem {

    public MeteorShower(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 1, 2500, 900);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        meteorShower(player);
        return InteractionResult.SUCCESS;
    }

    public static void meteorShower(Player player) {
        MeteorEntity.summonMultipleMeteors(player);
        MeteorEntity.summonMultipleMeteors(player);
        MeteorEntity.summonMultipleMeteors(player);
        MeteorEntity.summonMultipleMeteors(player);
        MeteorEntity.summonMultipleMeteors(player);
        MeteorEntity.summonMultipleMeteors(player);
        MeteorEntity.summonMultipleMeteors(player);

    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, summons a meteor shower\n" +
                "Spirituality Used: 1500\n" +
                "Left Click for a version that deals no block destruction\n" +
                "Cooldown: 45 secondss").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
