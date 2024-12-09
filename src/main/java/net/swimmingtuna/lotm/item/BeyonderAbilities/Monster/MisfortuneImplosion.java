package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import net.swimmingtuna.lotm.world.worlddata.CalamityEnhancementData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class MisfortuneImplosion extends SimpleAbilityItem {

    public MisfortuneImplosion(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 2, 75, 240);
    }

    @Override
    public InteractionResult useAbility(Level level, Player player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        useSpirituality(player);
        addCooldown(player);
        misfortuneImplosion(player);
        return InteractionResult.SUCCESS;
    }

    public static void misfortuneImplosion(Player player) {
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            int sequence = holder.getCurrentSequence();
            int enhancement = CalamityEnhancementData.getInstance((ServerLevel) player.level()).getCalamityEnhancement();
            double radius = (250 - (sequence * 100) + (enhancement * 50));
            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius))) {
                if (entity != player) {
                    if (BeyonderUtil.isBeyonderCapable(entity)) {
                        entity.getAttribute(ModAttributes.MISFORTUNE.get()).setBaseValue(entity.getAttribute(ModAttributes.MISFORTUNE.get()).getBaseValue() + 10);
                    }
                    Random random = new Random();
                    int randomInt = random.nextInt(3);
                    if (randomInt == 0) {
                        if (BeyonderUtil.isBeyonderCapable(entity)) {
                            AttributeInstance misfortune = player.getAttribute(ModAttributes.MISFORTUNE.get());
                            float explosionRadius = (float) (misfortune.getBaseValue() / 15) + (enhancement * 3);
                            float damage = (float) ((2 * misfortune.getBaseValue()) + (enhancement * 10));
                            entity.hurt(BeyonderUtil.explosionSource(player), damage);
                            entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), explosionRadius, false, Level.ExplosionInteraction.TNT);
                        } else {
                            entity.hurt(BeyonderUtil.explosionSource(player), 40 - (sequence * 10));
                            entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), 5, false, Level.ExplosionInteraction.TNT);
                        }
                    }
                    if (randomInt == 1) {
                        if (BeyonderUtil.isBeyonderCapable(entity)) {
                            AttributeInstance misfortune = player.getAttribute(ModAttributes.MISFORTUNE.get());
                            float duration = (float) (100 + (misfortune.getBaseValue() * 5) * enhancement);
                            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, (int) duration,4, false,false));
                            entity.addEffect(new MobEffectInstance(ModEffects.NOREGENERATION.get(), (int) (duration * 0.75), 1, false,false));
                            entity.hurt(BeyonderUtil.genericSource(player), (float) misfortune.getBaseValue() / 2);
                            misfortune.setBaseValue(0);
                        } else {
                            entity.hurt(BeyonderUtil.genericSource(player), 40);
                            entity.addEffect(new MobEffectInstance(ModEffects.NOREGENERATION.get(), (int) 200, 1, false,false));
                            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, (int) 200,4, false,false));
                        }
                    }
                    if (randomInt == 2) {
                        if (BeyonderUtil.isBeyonderCapable(entity)) {
                            AttributeInstance misfortune = player.getAttribute(ModAttributes.MISFORTUNE.get());
                            int duration = (int) ((5) + (misfortune.getBaseValue() / 10) + (enhancement * 3));
                            entity.getPersistentData().putInt("monsterImplosionLightning", duration);
                        } else {
                            entity.getPersistentData().putInt("monsterImplosionLightning", 10);
                        }
                    }
                }
            }
        }
    }
    public static void misfortuneCurse(Player player) {
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            int sequence = holder.getCurrentSequence();
            int enhancement = CalamityEnhancementData.getInstance((ServerLevel) player.level()).getCalamityEnhancement();
            double radius = (250 - (sequence * 100) + (enhancement * 50));
            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius))) {
                if (entity != player) {
                    if (BeyonderUtil.isBeyonderCapable(entity)) {
                        AttributeInstance misfortune = player.getAttribute(ModAttributes.MISFORTUNE.get());
                        float duration = (float) (100 + (misfortune.getBaseValue() * 5) * enhancement);
                        entity.addEffect(new MobEffectInstance(MobEffects.WITHER, (int) duration,4, false,false));
                        entity.hurt(BeyonderUtil.genericSource(player), (float) misfortune.getBaseValue() / 2);
                        misfortune.setBaseValue(0);
                    } else {
                        entity.hurt(BeyonderUtil.genericSource(player), 40);
                        entity.addEffect(new MobEffectInstance(MobEffects.WITHER, (int) 200,4, false,false));
                    }
                }
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, makes all living entities around the user freeze in place and take damage\n" +
                "Spirituality Used: 75\n" +
                "Cooldown: 12 seconds").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
