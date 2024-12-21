package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;


import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.swimmingtuna.lotm.blocks.MonsterDomainBlockEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.BlockInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.world.worlddata.CalamityEnhancementData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class DomainOfProvidence extends SimpleAbilityItem {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public DomainOfProvidence(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 4, 400, 600);
    }
    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        makeDomainOfProvidence(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    private void makeDomainOfProvidence(Player player) {
        if (!player.level().isClientSide()) {
            Level level = player.level();
            BlockPos pos = player.getOnPos();
            level.setBlock(pos, BlockInit.MONSTER_DOMAIN_BLOCK.get().defaultBlockState().setValue(LIT, true), 3);
            if (level.getBlockEntity(pos) instanceof MonsterDomainBlockEntity domainEntity) {
                domainEntity.setOwner(player);
                int enhancement = CalamityEnhancementData.getInstance((ServerLevel) player.level()).getCalamityEnhancement();
                int radius = player.getPersistentData().getInt("monsterDomainRadius");
                domainEntity.setRadius(radius);
                System.out.println(radius);
                domainEntity.setBad(false);
                domainEntity.setChanged();
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, put down a domain of providence, which will cause everything in the radius of it to encounter overwhelmingly positive effects, the strength of them being stronger the smaller area. Examples include entities getting regeneration, stone turning to ores, crops growing faster, repair tools, and more"));
        tooltipComponents.add(Component.literal("Left Click to increase radius"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("400").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("30 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
