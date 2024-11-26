package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;


import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.swimmingtuna.lotm.blocks.MonsterDomainBlock;
import net.swimmingtuna.lotm.blocks.MonsterDomainBlockEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.BlockInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class DomainOfProvidence extends SimpleAbilityItem {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public DomainOfProvidence(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 9, 0, 20);
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
        tooltipComponents.add(Component.literal(
                "Upon use, exude an aura of tyranny, not giving any entity permission to move, implanting fear strong enough to not allow them to use their abilities"
        ).withStyle(/*ChatFormatting.BOLD, ChatFormatting.BLUE*/));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
