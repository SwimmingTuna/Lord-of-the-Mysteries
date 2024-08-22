package net.swimmingtuna.lotm.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.CircleEntity;
import net.swimmingtuna.lotm.entity.LightningBallEntity;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.entity.RoarEntity;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.util.ExplosionUtil;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class TestItem extends Item {
    public TestItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        summonThing(pPlayer);
        return super.use(level, pPlayer, hand);
    }

    private void summonThing(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            LightningBallEntity lightningBall = new LightningBallEntity(EntityInit.LIGHTNING_BALL.get(), pPlayer.level(), true);
            lightningBall.setSummoned(true);
            lightningBall.setBallXRot(10.0f);
            lightningBall.setBallYRot(10.0f);
            lightningBall.setPos(pPlayer.getX(), pPlayer.getY() + 1.5, pPlayer.getZ());
            lightningBall.setOwner(pPlayer);
            lightningBall.setAbsorbed(true);
            pPlayer.level().addFreshEntity(lightningBall);
        }
    }
    public static void sonicBoom(Player pPlayer) {
        pPlayer.level().addParticle(ParticleTypes.EXPLOSION, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), 0, 0, 0);
    }
}
