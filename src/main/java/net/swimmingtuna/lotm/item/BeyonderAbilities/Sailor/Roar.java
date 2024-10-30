package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.RoarEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Roar extends SimpleAbilityItem {

    public Roar(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 4, 500, 100);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        roar(player);
        addCooldown(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    public static void roar(Player player) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        RoarEntity roarEntity = new RoarEntity(EntityInit.ROAR_ENTITY.get(), player.level());
        roarEntity.teleportTo(player.getX(), player.getY(), player.getZ());
        Vec3 lookVec = player.getLookAngle();
        roarEntity.setDeltaMovement(lookVec.scale(10 - holder.getCurrentSequence()).x,lookVec.scale(10 - holder.getCurrentSequence()).y,lookVec.scale(10 - holder.getCurrentSequence()).z);
        roarEntity.hurtMarked = true;
        player.level().addFreshEntity(roarEntity);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, lets out a devastating roar\n" +
                "Spirituality Used: 500\n" +
                "Cooldown: 5 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
