package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class MatterAccelerationEntities extends SimpleAbilityItem {

    public MatterAccelerationEntities(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 0, 800, 900);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        matterAccelerationEntities(player);
        addCooldown(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, increases the speed of all entities, if their speed passes a certain point, they deal damage to all entities around them and destroy all blocks around them\n" +
                "Spirituality Used: 800\n" +
                "Cooldown: 45 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    public static void matterAccelerationEntities(Player player) {
        if (!player.level().isClientSide()) {
            AABB searchBox = player.getBoundingBox().inflate(300);
            for (Entity entity : player.level().getEntitiesOfClass(Entity.class, searchBox)) {
                if (entity != player && (entity instanceof LivingEntity || entity instanceof Projectile)) {
                    Vec3 currentMovement = entity.getDeltaMovement();
                    double speed = currentMovement.length();
                    Vec3 normalizedMovement = currentMovement.normalize();
                    Vec3 newMovement = normalizedMovement.scale(speed * 10);
                    entity.setDeltaMovement(newMovement);
                    entity.hurtMarked = true;
                    entity.getPersistentData().putInt("matterAccelerationEntities", 10);
                }
            }
        }
    }
}
