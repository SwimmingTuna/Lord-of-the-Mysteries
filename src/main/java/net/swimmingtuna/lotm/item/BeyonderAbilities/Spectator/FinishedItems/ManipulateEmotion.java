package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ManipulateEmotion extends SimpleAbilityItem {

    public ManipulateEmotion(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 4, 500, 0);
    }


    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        int dreamIntoReality = (int) player.getAttribute(ModAttributes.DIR.get()).getValue();
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player, this, 1200 / dreamIntoReality);
        useSpirituality(player, 500);
        manipulateEmotion(player, holder.getCurrentSequence());
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, all living entities able to be manipulated within 250 blocks fall into despair and harm themselves.\n" +
                "Left Click for Manipulate Fondness\n" +
                "Spirituality Used: 500\n" +
                "Cooldown: 1 minute").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    private static void manipulateEmotion(Player player, int sequence) {
        if (!player.level().isClientSide()) {
            float damage = 100 - (sequence * 10);
            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(250))) {
                if (entity != player && entity.hasEffect(ModEffects.MANIPULATION.get())) {
                    entity.hurt(entity.damageSources().magic(), damage);
                    entity.removeEffect(ModEffects.MANIPULATION.get());
                }
            }
        }
    }
}
