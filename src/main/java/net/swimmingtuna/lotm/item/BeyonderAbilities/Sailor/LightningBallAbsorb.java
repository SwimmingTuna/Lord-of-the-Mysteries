package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.LightningBallEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import javax.annotation.Nullable;
import java.util.List;

public class LightningBallAbsorb extends SimpleAbilityItem {

    public LightningBallAbsorb(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 1, 1500, 900);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        lightningBallAbsorb(player);
        addCooldown(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, briefly charge up a ball of condensed lightning which will absorb all nearby lightning, growing in size and destructive capability before being shot out"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("1500").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("45 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
    public static void lightningBallAbsorb(Player player) {
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            LightningBallEntity lightningBall = new LightningBallEntity(EntityInit.LIGHTNING_BALL.get(), player.level(), true);
            lightningBall.setSummoned(true);
            lightningBall.setBallXRot((float) ((Math.random() * 20) - 10));
            lightningBall.setBallYRot((float) ((Math.random() * 20) - 10));
            lightningBall.setPos(player.getX(), player.getY() + 1.5, player.getZ());
            lightningBall.setOwner(player);
            lightningBall.setAbsorbed(true);
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(lightningBall);
            scaleData.setScale(10 + (10 - holder.getCurrentSequence() * 3));
            scaleData.markForSync(true);
            player.level().addFreshEntity(lightningBall);
        }
    }
}
