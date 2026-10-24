package com.yuushya.particle;

import com.yuushya.registries.YuushyaRegistryData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

public class YuushyaParticleFactory {

    public static <T extends ParticleOptions> ParticleProvider<T> create(YuushyaRegistryData.Particle particle, SpriteSet spriteSet) {
        return new ParticleProvider<T>() {
            @Nullable
            @Override
            public Particle createParticle(T particleOptions, ClientLevel clientLevel,
                                           double x, double y, double z,
                                           double velocityX, double velocityY, double velocityZ,
                                           RandomSource random) {
                return switch (particle.classType) {
                    case "LeafParticle" -> new LeafParticle(clientLevel, x, y, z, velocityX, velocityY, velocityZ,
                            spriteSet, spriteSet.get(random));
                    default -> new LeafParticle(clientLevel, x, y, z, velocityX, velocityY, velocityZ,
                            spriteSet, spriteSet.get(random));
                };
            }
        };
    }
}
