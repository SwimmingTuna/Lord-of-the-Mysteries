package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
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

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, summons a meteor shower\n" +
                "Spirituality Used: 1500\n" +
                "Left Click for a version that deals block destruction\n" +
                "Cooldown: 45 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
