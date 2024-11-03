package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class SirenSongWeaken extends SimpleAbilityItem {

    public SirenSongWeaken(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 5, 300, 600);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        sirenSongWeaken(player, level);
        return InteractionResult.SUCCESS;
    }

    private static void sirenSongWeaken(Player player, Level level) {
        CompoundTag tag = player.getPersistentData();
        if (tag.getInt("sirenSongWeaken") == 0) {
            tag.putInt("sirenSongWeaken", 400);
            tag.putInt("ssParticleAttributeHelper", 400);
        }
        if (tag.getInt("sirenSongWeaken") > 1 && tag.getInt("sirenSongWeaken") < 400) {
            tag.putInt("sirenSongWeaken", 0);
            tag.putInt("ssParticleAttributeHelper", 1);
        }
        if (tag.getInt("sirenSongHarm") > 1) {
            tag.putInt("sirenSongHarm", 0);
            tag.putInt("sirenSongWeaken", 400);
            tag.putInt("ssParticleAttributeHelper", 400);

        }
        if (tag.getInt("sirenSongStun") > 1) {
            tag.putInt("sirenSongStun", 0);
            tag.putInt("sirenSongWeaken", 400);
            tag.putInt("ssParticleAttributeHelper", 400);
        }
        if (tag.getInt("sirenSongStrengthen") > 1) {
            tag.putInt("sirenSongStrengthen", 0);
            tag.putInt("sirenSongWeaken", 400);
            tag.putInt("ssParticleAttributeHelper", 400);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, start singing a song that weakens all entities around you\n" +
                "Spirituality Used: 300\n" +
                "Cooldown: 50 seconds").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}