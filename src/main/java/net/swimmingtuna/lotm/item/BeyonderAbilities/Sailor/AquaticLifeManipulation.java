package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AquaticLifeManipulation extends Item {
    public AquaticLifeManipulation(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (!holder.currentClassMatches(BeyonderClassInit.SAILOR)) {
                player.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            } else if (holder.getSpirituality() < 125) {
                player.displayClientMessage(Component.literal("You need 125 spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
            } else if (holder.currentClassMatches(BeyonderClassInit.SAILOR) && holder.getCurrentSequence() <= 3 && holder.useSpirituality(125)) {
                useItem(player);
                if (!player.getAbilities().instabuild) {
                    player.getCooldowns().addCooldown(this, 200);
                }
            }
        }
        return super.use(level, player, hand);
    }

    public static void useItem(Player player) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
        int sequence = holder.getCurrentSequence();
        if (player.level().isClientSide()) {
            return;
        }
        List<LivingEntity> aquaticEntities = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(50), entity -> entity instanceof WaterAnimal);
        if (aquaticEntities.isEmpty()) {
            return;
        }
        LivingEntity nearestAquaticEntity = aquaticEntities.stream().min(Comparator.comparingDouble(player::distanceTo)).orElse(null);
        List<Player> nearbyPlayers = nearestAquaticEntity.level().getEntitiesOfClass(Player.class, nearestAquaticEntity.getBoundingBox().inflate(200 - (sequence * 20)));
        Player nearestPlayer = nearbyPlayers.stream().filter(nearbyPlayer -> nearbyPlayer != player).min(Comparator.comparingDouble(nearestAquaticEntity::distanceTo)).orElse(null);
        if (nearestPlayer == null) {
            return;
        }
        if (sequence >= 2) {
            player.sendSystemMessage(Component.literal("Nearest Player is " + nearestPlayer.getName().getString() + ". Pathway is " + holder.getCurrentClass()));
        } else {
            player.sendSystemMessage(Component.literal("Nearest Player is " + nearestPlayer.getName().getString() + ". Pathway is " + holder.getCurrentClass() + " sequence " + holder.getCurrentSequence()));
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, communicates with any aquatic life around the player, if there is any, they communicate back with the information of any player within a range of the spoken to aquatic animal\n" +
                "Spirituality Used: 100\n" +
                "Cooldown: 2 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
