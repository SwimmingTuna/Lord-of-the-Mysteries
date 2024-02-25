package net.swimmingtuna.lotm.item.custom.BeyonderAbilities;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class BattleHypnotism extends Item {

    public BattleHypnotism(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Player pPlayer = pContext.getPlayer();
        Level level = pPlayer.level();
        BlockPos positionClicked = pContext.getClickedPos();
        if (!pContext.getLevel().isClientSide) {
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                if (spectatorSequence.getCurrentSequence() <= 6 &&  BeyonderHolderAttacher.getHolderUnwrap(pPlayer).useSpirituality(150)) {
                    makesEntitiesAttackEachOther(pPlayer, level, positionClicked, spectatorSequence.getCurrentSequence());
                    if (!pPlayer.getAbilities().instabuild) {
                        pPlayer.getCooldowns().addCooldown(this, 300);
                    }
                }
            });
        }
        return InteractionResult.SUCCESS;
    }
    private void makesEntitiesAttackEachOther(Player pPlayer, Level level, BlockPos targetPos, int sequence) {
        double radius = 20.0 - sequence;
        float damage = 10 - sequence;
        int duration = 400 - (sequence * 10);
        AABB boundingBox = new AABB(targetPos).inflate(radius);
        level.getEntitiesOfClass(LivingEntity.class, boundingBox, entity -> entity.isAlive()).forEach(livingEntity -> {
            livingEntity.hurt(livingEntity.damageSources().magic(), damage);
            if (livingEntity instanceof Player) {
                livingEntity.addEffect(new MobEffectInstance(ModEffects.BATTLEHYPNOTISM.get(),duration, (int) radius, false, false));
            }
            else {
                (livingEntity).addEffect((new MobEffectInstance(ModEffects.BATTLEHYPNOTISM.get(), duration, 0, false, false)));
            }});
    }
    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, makes all living entities around the user freeze in place\n" +
                    "Spirituality Used: 100\n" +
                    "Cooldown: 15 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

}
