package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
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

public class MonsterProjectileControl extends SimpleAbilityItem {
    public MonsterProjectileControl(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 8, 0, 20);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        enableOrDisableProjectileControl(player);
        return InteractionResult.SUCCESS;
    }

    public static void enableOrDisableProjectileControl(Player player) {
        if (!player.level().isClientSide()) {
            CompoundTag tag = player.getPersistentData();
            boolean monsterProjectileControl = tag.getBoolean("monsterProjectileControl");
            tag.putBoolean("monsterProjectileControl", !monsterProjectileControl);
            player.displayClientMessage(Component.literal("Projectile Movement Turned " + (monsterProjectileControl ? "Off" : "On")).withStyle(ChatFormatting.BOLD, ChatFormatting.WHITE), true);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, enables or disables your mastery with the bow. If enabled, all arrows will change trajectory to the nearest target after a few seconds"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("None").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("1 Second").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}