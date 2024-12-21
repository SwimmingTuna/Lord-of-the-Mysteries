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
import net.swimmingtuna.lotm.entity.StormSealEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import javax.annotation.Nullable;
import java.util.List;

public class StormSeal extends SimpleAbilityItem {

    public StormSeal(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 0, 5000, 2400);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        stormSeal(player);
        return InteractionResult.SUCCESS;
    }

    public static void stormSeal(Player player) {
        if (!player.level().isClientSide()) {
            StormSealEntity stormSealEntity = new StormSealEntity(EntityInit.STORM_SEAL_ENTITY.get(), player.level());
            Vec3 lookVec = player.getLookAngle().normalize().scale(3.0f);
            stormSealEntity.setOwner(player);
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(stormSealEntity);
            scaleData.setTargetScale(3.0f);
            stormSealEntity.teleportTo(player.getX(), player.getY(), player.getZ());
            stormSealEntity.setDeltaMovement(lookVec.x, lookVec.y, lookVec.z);
            player.level().addFreshEntity(stormSealEntity);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, shoots out a compressed storm, which damages entities around and anything hit directly will be sealed for 3 minutes"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("5000").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("2 Minutes").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
