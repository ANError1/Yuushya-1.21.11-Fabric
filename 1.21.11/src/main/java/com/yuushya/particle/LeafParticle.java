package com.yuushya.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

/**
 * 落叶粒子。1.21.9 之后 TextureSheetParticle 被 SingleQuadParticle 取代，
 * 贴图改为在构造时直接传入 TextureAtlasSprite，渲染层由 getLayer() 决定。
 */
public class LeafParticle extends SingleQuadParticle {
    private final SpriteSet spriteSet;

    public LeafParticle(ClientLevel clientLevel, double x, double y, double z,
                        double velocityX, double velocityY, double velocityZ,
                        SpriteSet spriteSet, TextureAtlasSprite sprite) {
        super(clientLevel, x, y, z, velocityX, velocityY, velocityZ, sprite);
        this.lifetime = 300;
        this.gravity = 0.03F + random.nextFloat() * 0.03F;

        this.xd = 0.0F;
        this.yd = 0.0F;
        this.zd = 0.0F;
        this.scale(1.5F);
        this.hasPhysics = true;
        this.spriteSet = spriteSet;
    }

    @Override
    public void tick() {
        super.tick();
        this.xo = this.x;//o means pre
        this.yo = this.y;
        this.zo = this.z;
        this.oRoll = this.roll;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.spriteSet);
            this.yd -= 0.04D * this.gravity;
            this.move(this.xd, this.yd, this.zd);

            this.yd *= 0.9599999785423279D;
            this.xd = 0.012 * Math.sin(this.y * 0.2);
            this.zd = 0.012 * Math.cos(this.y * 0.2);

            if (this.y == this.yo) {
                this.xd *= 1.1D;
                this.zd *= 1.1D;
            }

            this.roll = (float) Math.sin((double) this.age * 0.03) * 0.75F;

            if (this.onGround) {
                this.xd *= 0.699999988079071D;
                this.zd *= 0.699999988079071D;
            }
        }
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType particleOptions, ClientLevel clientLevel,
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ,
                                       RandomSource random) {
            return new LeafParticle(clientLevel, x, y, z, velocityX, velocityY, velocityZ,
                    this.spriteSet, this.spriteSet.get(random));
        }
    }
}
