package com.yuushya.fabric;

import com.yuushya.Yuushya;
import com.yuushya.YuushyaClient;
import com.yuushya.entity.ChairEntity;
import com.yuushya.entity.ChairEntityRender;
import com.yuushya.particle.YuushyaParticleFactory;
import com.yuushya.registries.YuushyaRegistries;
import com.yuushya.registries.YuushyaRegistryConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

public class YuushyaClientFabric implements ClientModInitializer {
    @Override
    @SuppressWarnings("unchecked")
    public void onInitializeClient() {
        FabricLoader.getInstance().getModContainer(Yuushya.MOD_ID).ifPresent(container -> {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    Identifier.fromNamespaceAndPath(Yuushya.MOD_ID, "fusion_combine"),
                    container, Component.translatable("pack.yuushya_fusion_combine.name"), ResourcePackActivationType.NORMAL);
            ResourceManagerHelper.registerBuiltinResourcePack(
                    Identifier.fromNamespaceAndPath(Yuushya.MOD_ID, "mcpatcher_feature"),
                    container, Component.translatable("pack.yuushya_mcpatcher_feature.name"), ResourcePackActivationType.NORMAL);
            ResourceManagerHelper.registerBuiltinResourcePack(
                    Identifier.fromNamespaceAndPath(Yuushya.MOD_ID, "ctm_support"),
                    container, Component.translatable("pack.yuushya_ctm_support.name"), ResourcePackActivationType.NORMAL);
        });

        YuushyaClient.onInitializeClient();

        EntityRendererRegistry.register((EntityType<ChairEntity>) YuushyaRegistries.CHAIR_ENTITY.get(), ChairEntityRender::new);

        YuushyaRegistryConfig.YuushyaRawParticleMap.values().forEach(particle ->
                ParticleFactoryRegistry.getInstance().register(
                        (ParticleType<SimpleParticleType>) YuushyaRegistries.PARTICLE_TYPES.get(particle.name).get(),
                        spriteProvider -> YuushyaParticleFactory.create(particle, spriteProvider)));
    }
}
