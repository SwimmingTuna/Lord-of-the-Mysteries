package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;


import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import net.swimmingtuna.lotm.world.worlddata.CalamityEnhancementData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class MisfortuneImplosion extends SimpleAbilityItem {

    public MisfortuneImplosion(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 2, 1000, 200);
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
                    CompoundTag tag = entity.getPersistentData();
                    double misfortune = tag.getDouble("misfortune");
                    Random random = new Random();
                    int randomInt = random.nextInt(3);
                    if (randomInt == 0) {
                        float explosionRadius = (float) (Math.max(3, misfortune / 8) + (enhancement * 3));
                        float damage = (float) ((2 * misfortune) + (enhancement * 10));
                        entity.hurt(BeyonderUtil.explosionSource(player), damage);
                        entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), explosionRadius, false, Level.ExplosionInteraction.TNT);
                        tag.putDouble("misfortune", 0);
                    }
                    if (randomInt == 1) {
                        float duration = (float) (100 + (misfortune * 5) * enhancement);
                        entity.addEffect(new MobEffectInstance(MobEffects.WITHER, (int) duration, 4, false, false));
                        entity.addEffect(new MobEffectInstance(ModEffects.NOREGENERATION.get(), (int) (duration * 0.75), 1, false, false));
                        entity.hurt(BeyonderUtil.genericSource(player), (float) misfortune / 2);
                    }
                    if (randomInt == 2) {
                        int duration = (int) ((5) + (misfortune / 10) + (enhancement * 3));
                        entity.getPersistentData().putInt("monsterImplosionLightning", duration);
                        tag.putDouble("misfortune", 0);
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
                    CompoundTag tag = entity.getPersistentData();
                    double misfortune = tag.getDouble("misfortune");
                    float duration = (float) (100 + (misfortune * 5) * enhancement);
                    entity.addEffect(new MobEffectInstance(MobEffects.WITHER, (int) duration, 4, false, false));
                    entity.hurt(BeyonderUtil.genericSource(player), (float) misfortune / 2);
                    tag.putDouble("misfortune", 0);
                }
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, causes all entities misfortune around you to implode, causing them to deal with either an explosion which scales off their misfortune, wither and the inability to regenerate for a time dependent on their misfortunem, or lightning to be attracted to them for a time dependent on their misfortune"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("1000").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("10 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
