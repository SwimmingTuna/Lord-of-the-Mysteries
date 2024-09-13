package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
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
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnvisionKingdom extends Item {

    public EnvisionKingdom(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
            if (!player.level().isClientSide()) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                if (!holder.currentClassMatches(BeyonderClassInit.SPECTATOR)) {
                    player.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
                }
                if (holder.getSpirituality() < (int) 3500 / dreamIntoReality.getValue()) {
                    player.displayClientMessage(Component.literal("You need " + ((int) 3500 / dreamIntoReality.getValue()) + " spirituality in order to use this").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA), true);
                }

                BlockPos playerPos = player.getOnPos();
                if (holder.currentClassMatches(BeyonderClassInit.SPECTATOR) && holder.getCurrentSequence() <= 0 && holder.useSpirituality((int) (3500 / dreamIntoReality.getValue()))) {
                    generateCathedral(player);
                    if (!player.getAbilities().instabuild) {
                        player.getCooldowns().addCooldown(this, 900);
                    }
                }
            }
        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, summons your Divine Kingdom, the Corpse Cathedral\n" +
                "Spirituality Used: 3500\n" +
                "Cooldown: 45 seconds ").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    private void generateCathedral(Player player) {
        if (!player.level().isClientSide) {
            int x = (int) player.getX();
            int y = (int) player.getY();
            int z = (int) player.getZ();
            CompoundTag compoundTag = player.getPersistentData();
            compoundTag.putInt("mindscapeAbilities", 50);
            compoundTag.putInt("inMindscape", 1);
            compoundTag.putInt("mindscapePlayerLocationX", x); //check if this works
            compoundTag.putInt("mindscapePlayerLocationY", y);
            compoundTag.putInt("mindscapePlayerLocationZ", z);
        }
    }
}