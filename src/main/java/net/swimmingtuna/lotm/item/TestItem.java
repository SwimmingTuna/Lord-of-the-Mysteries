package net.swimmingtuna.lotm.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.CircleEntity;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class TestItem extends Item {
    public TestItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            summonThing2(pPlayer);
        }
        return super.use(level, pPlayer, hand);
    }

    public static void summonThing(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            pPlayer.getPersistentData().putInt("sailorSeal", 1200);
            pPlayer.getPersistentData().putInt("sailorSealX", (int) pPlayer.getX());
            pPlayer.getPersistentData().putInt("sailorSeaY", (int) pPlayer.getY());
            pPlayer.getPersistentData().putInt("sailorSealZ", (int) pPlayer.getZ());
        }
    }

    private void summonThing2(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            pPlayer.setTicksFrozen(60);

        }
    }
    public static void sonicBoom(Player pPlayer, int sequence) {
        if (!pPlayer.level().isClientSide()) {
            pPlayer.getPersistentData().putInt("sailorSonicBoom", 60);
            pPlayer.level().explode(pPlayer, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), 60 - (sequence * 8), Level.ExplosionInteraction.TNT);
            for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(40 - (sequence * 8)))) {
                if (entity != pPlayer) {
                    entity.getPersistentData().putInt("sailorSonicBoom", 5);
                    int duration = 100 - (sequence * 20);
                    int damage = 25 - (sequence * 5);
                    if (!(entity instanceof Player)) {
                        entity.addEffect((new MobEffectInstance(ModEffects.AWE.get(), duration, 1, false, false)));
                        entity.hurt(entity.damageSources().magic(), damage);
                        pPlayer.sendSystemMessage(Component.literal("damage is " + damage));
                    } else if ((entity instanceof Player player)) {
                        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(player).orElse(null);
                        int pSequence = holder.getCurrentSequence();
                        int pDuration = duration - (50 - (pSequence * 5));
                        int pDamage = (int) (damage - (8 - (pSequence * 0.5)));
                        entity.addEffect((new MobEffectInstance(ModEffects.AWE.get(), pDuration, 1, false, false)));
                        entity.hurt(entity.damageSources().magic(), pDamage);
                    }
                }
            }
        }
    }
}
