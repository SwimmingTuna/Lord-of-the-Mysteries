package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.xml.stream.Location;
import java.util.List;

import static net.minecraft.commands.arguments.EntityArgument.getPlayer;

public class DragonBreath extends Item {
    public DragonBreath(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSpectatorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 75) {
                pPlayer.displayClientMessage(Component.literal("You need " + (100 /dreamIntoReality.getValue()) + " spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
        }        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            if (spectatorSequence.getCurrentSequence() <= 4 && spectatorSequence.useSpirituality((int) (100 / dreamIntoReality.getValue()))) {
                shootFireball(pPlayer);
                    pPlayer.sendSystemMessage(Component.literal("working"));
                }
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 10);
            });
        return super.use(level, pPlayer, hand);
    }

    public static void shootFireball(Player pPlayer) {
        Vec3 eyePosition = pPlayer.getEyePosition(1.0f);
        Vec3 direction = pPlayer.getViewVector(1.0f);
        Vec3 fireballPosition = eyePosition.add(direction.scale(1.2));

        SmallFireball fireball = new SmallFireball(EntityType.SMALL_FIREBALL, pPlayer.level());
        Vec3 initialVelocity = direction.scale(2.0); // Adjust the scale factor as needed
        fireball.setDeltaMovement(initialVelocity);
        fireball.setPos(fireballPosition);
        fireball.setOwner(pPlayer);

        pPlayer.level().addFreshEntity(fireball);
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
