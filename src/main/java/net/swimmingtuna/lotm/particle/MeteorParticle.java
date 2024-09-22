package net.swimmingtuna.lotm.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class MeteorParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    public MeteorParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);
        this.spriteSet = spriteSet;

        this.friction = 0F;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 5;
        this.lifetime = 100;

        // Set the initial sprite
        this.setSpriteFromAge(spriteSet);

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        return this.quadSize * 10;
    }

    public static void addSizeParticle(ClientLevel level, Player entity, SpriteSet spriteSet, float size) {
        MeteorParticle particle = new MeteorParticle(level, entity.getX(), entity.getY(), entity.getZ(), spriteSet, 0, 0, 0);
        particle.scale(size);
//        level.addParticle(particle, entity.getX(), entity.getY(), entity.getZ(), 0, 0, 0);
    }


    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.spriteSet);
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements net.minecraft.client.particle.ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public MeteorParticle createParticle(@NotNull SimpleParticleType particleType, @NotNull ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            MeteorParticle particle = new MeteorParticle(level, x, y, z, this.spriteSet, dx, dy, dz);
            return particle;
        }
    }
}
