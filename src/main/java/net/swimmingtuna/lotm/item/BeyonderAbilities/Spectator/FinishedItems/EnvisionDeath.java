package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class EnvisionDeath extends SimpleAbilityItem {

    public EnvisionDeath(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 0, 2000, 2400);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
        envisionDeath(player, (int) dreamIntoReality.getValue());
        addCooldown(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    private void envisionDeath(Player player, int dir) {
        if (!player.level().isClientSide()) {
            double radius = 300;
            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius))) {
                if (entity != player) {
                    int entityHealth = (int) entity.getHealth();
                    if (entityHealth <= 100 + (20 * dir)) {
                        entity.hurt(entity.damageSources().magic(), 100);
                    }
                }
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, all living around you with less than 100 health die\n" +
                "Left Click for Envision Health\n" +
                "Spirituality Used: 2000\n" +
                "Cooldown: 2 minutes").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
