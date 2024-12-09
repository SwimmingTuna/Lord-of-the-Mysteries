package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ManipulateFondness extends SimpleAbilityItem {

    public ManipulateFondness(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 4, 100, 100);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        manipulateFondness(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, all entities, up to a good distance away, that are being manipulated have mobs nearby target them and gain speed."));
        tooltipComponents.add(Component.literal("Left Click for Apply Manipulation"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("100").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("5 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    private static void manipulateFondness(Player player) {
        if (!player.level().isClientSide()) {
            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(250))) {
                if (entity != player && entity.hasEffect(ModEffects.MANIPULATION.get())) {
                    AttributeInstance dreamIntoReality = player.getAttribute(ModAttributes.DIR.get());
                    entity.addEffect(new MobEffectInstance(ModEffects.BATTLEHYPNOTISM.get(), (int) (600 * dreamIntoReality.getValue()), 1, false, false));
                    for (Mob mob : entity.level().getEntitiesOfClass(Mob.class, entity.getBoundingBox().inflate(50))) {
                        mob.setTarget(entity);
                        mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1, false, false));
                    }
                    entity.removeEffect(ModEffects.MANIPULATION.get());
                }
            }
        }
    }
}
