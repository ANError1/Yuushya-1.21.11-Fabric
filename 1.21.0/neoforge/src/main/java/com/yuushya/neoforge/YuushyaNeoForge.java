package com.yuushya.neoforge;

import com.yuushya.Yuushya;
import com.yuushya.item.data_component.Structure;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforgespi.language.IModInfo;

import java.util.Optional;

import static com.yuushya.registries.YuushyaRegistries.STRUCTURE;
import static com.yuushya.registries.YuushyaRegistries.TRANS_DIRECTION;

@Mod(Yuushya.MOD_ID)
public class YuushyaNeoForge {

    public YuushyaNeoForge(IEventBus modBus) {
        Yuushya.init();
        modBus.addListener(this::packSetup);
    }

    public void packSetup(AddPackFindersEvent event) {
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(Yuushya.MOD_ID, "resourcepacks/fusion_combine"),
                PackType.CLIENT_RESOURCES,
                Component.translatable("pack.yuushya_fusion_combine.name"),
                PackSource.BUILT_IN,
                false,
                Pack.Position.TOP);
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(Yuushya.MOD_ID, "resourcepacks/mcpatcher_feature"),
                PackType.CLIENT_RESOURCES,
                Component.translatable("pack.yuushya_mcpatcher_feature.name"),
                PackSource.BUILT_IN,
                false,
                Pack.Position.TOP);
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(Yuushya.MOD_ID, "resourcepacks/ctm_support"),
                PackType.CLIENT_RESOURCES,
                Component.translatable("pack.yuushya_ctm_support.name"),
                PackSource.BUILT_IN,
                false,
                Pack.Position.TOP);

    }

//    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Yuushya.MOD_ID);
//
//    private static final DeferredHolder<DataComponentType<?>, DataComponentType<Structure>> _STRUCTURE = REGISTRAR.registerComponentType(
//            "structure",
//            builder -> builder
//                    // The codec to read/write the data to disk
//                    .persistent(Structure.CODEC)
//                    // The codec to read/write the data across the network
//                    .networkSynchronized(Structure.STREAM_CODEC)
//    );
//
//    private static final DeferredHolder<DataComponentType<?>,DataComponentType<Integer>> _TRANS_DIRECTION = REGISTRAR.registerComponentType(
//            "mode", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
//
//    static {
//        STRUCTURE = (RegistrySupplier<DataComponentType<?>>) _STRUCTURE.getDelegate();
//        TRANS_DIRECTION = (RegistrySupplier<DataComponentType<?>>) _TRANS_DIRECTION.getDelegate();
//    }

}
