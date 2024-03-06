package net.swimmingtuna.lotm.item.custom.BeyonderAbilities;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class DragonBreathCurved extends Item {
    public DragonBreathCurved(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            if (spectatorSequence.getCurrentSequence() <= 7 && spectatorSequence.useSpirituality(100)) {
                Vec3 vec3 = pPlayer.getViewVector(1.0f);
                if (!pPlayer.level().isClientSide) {
                    DragonFireball dragonFireball = new DragonFireball(level, pPlayer, pPlayer.getX(), pPlayer.getEyeY(), pPlayer.getZ());
                    dragonFireball.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 3.0F, 1.0F);
                    level.addFreshEntity(dragonFireball);
                    pPlayer.sendSystemMessage(Component.literal("working"));
                }
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 10);
            }
        });
        return super.use(level, pPlayer, hand);
    }
    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, shoots a dragons breath\n" +
                    "Spirituality Used: 100\n" +
                    "Cooldown: 0.5 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
}
