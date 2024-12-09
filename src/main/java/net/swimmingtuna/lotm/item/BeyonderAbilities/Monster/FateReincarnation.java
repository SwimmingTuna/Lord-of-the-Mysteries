package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import javax.annotation.Nullable;
import java.util.List;

public class FateReincarnation extends SimpleAbilityItem {

    public FateReincarnation(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 1, 0, 10000);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        fateReincarnation(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    private void fateReincarnation(Player player) { //add functionality for being able to control a player mob
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            int x = (int) (player.getX() + (Math.random() * 5000) - 2500);
            int z = (int) (player.getZ() + (Math.random() * 5000) - 2500);
            int surfaceY = player.level().getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + 1;
            player.teleportTo(x, surfaceY, z);
            player.getPersistentData().putInt("monsterReincarnationCounter", 7200);
            if (holder.getCurrentSequence() == 0) {
                player.getPersistentData().putBoolean("monsterReincarnation", true);
            } else {
                player.getPersistentData().putBoolean("monsterReincarnation", false);
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal(
                "Upon use, exude an aura of tyranny, not giving any entity permission to move, implanting fear strong enough to not allow them to use their abilities"
        ).withStyle(/*ChatFormatting.BOLD, ChatFormatting.BLUE*/));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    public static void monsterReincarnationChecker(Player player) {
        if (!player.level().isClientSide() && player.tickCount % 20 == 0) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            CompoundTag tag = player.getPersistentData();
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(player);

            boolean y = player.getPersistentData().getBoolean("monsterReincarnation");
            int x = tag.getInt("monsterReincarnationCounter");
            if (!y) {
                if (x >= 1) {
                    tag.putInt("monsterReincarnationCounter", x - 1);
                }
                if (x >= 7100) {
                    scaleData.setScale(0.2f);
                    holder.removeClass();
                }
                if (x >= 6900 && x <= 7099) {
                    scaleData.setScale(0.25f);
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 9);
                }
                if (x >= 6600 && x <= 6899) {
                    scaleData.setScale(0.35f);
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 8);
                }
                if (x >= 6050 && x <= 6599) {
                    scaleData.setScale(0.5f);
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 7);
                }
                if (x >= 5300 && x <= 6049) {
                    scaleData.setScale(0.6f);
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 6);
                }
                if (x >= 4200 && x <= 5299) {
                    scaleData.setScale(0.7f);
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 5);
                }
                if (x >= 3200 && x <= 4199) {
                    scaleData.setScale(0.85f);
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 4);
                }
                if (x >= 1800 && x <= 3199) {
                    scaleData.setScale(0.9f);
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 3);
                }
                if (x >= 2 && x <= 1799) {
                    scaleData.setScale(1.0f);
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 2);
                }
                if (x == 1) {
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 1);
                }
            } else {
                if (x >= 1) {
                    tag.putInt("monsterReincarnationCounter", x - 1);
                }
                if (x >= 7140) {
                    scaleData.setScale(0.2f);
                    holder.removeClass();
                }
                if (x >= 7010 && x <= 7139) {
                    scaleData.setScale(0.25f);
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 9);
                }
                if (x >= 6800 && x <= 7009) {
                    scaleData.setScale(0.35f);
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 8);
                }
                if (x >= 6400 && x <= 6799) {
                    scaleData.setScale(0.5f);
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 7);
                }
                if (x >= 5850 && x <= 6399) {
                    scaleData.setScale(0.6f);
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 6);
                }
                if (x >= 5050 && x <= 5849) {
                    scaleData.setScale(0.7f);
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 5);
                }
                if (x >= 4150 && x <= 5049) {
                    scaleData.setScale(0.85f);
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 4);
                }
                if (x >= 2949 && x <= 4149) {
                    scaleData.setScale(0.9f);
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 3);
                }
                if (x >= 1650 && x <= 2950) {
                    scaleData.setScale(1.0f);
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 2);
                }
                if (x >= 2 && x <= 1649) {
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 1);
                }
                if (x == 1) {
                    holder.setClassAndSequence(BeyonderClassInit.MONSTER.get(), 0);

                }
            }
        }
    }
}
