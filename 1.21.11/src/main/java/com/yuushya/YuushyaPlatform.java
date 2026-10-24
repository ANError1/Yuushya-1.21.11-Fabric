package com.yuushya;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Fabric 平台相关工具（原先由 Architectury 的 {@code Platform} / {@code @ExpectPlatform} 提供）。
 */
public final class YuushyaPlatform {

    private YuushyaPlatform() {
    }

    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static Path getModsDirectory() {
        return FabricLoader.getInstance().getGameDir().resolve("mods");
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static Optional<ModContainer> getMod(String modId) {
        return FabricLoader.getInstance().getModContainer(modId);
    }

    public static String getModVersion(String modId) {
        return getMod(modId)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("0.0.0");
    }

    public static List<Path> getModFilePaths(String modId) {
        return getMod(modId)
                .map(container -> container.getRootPaths().stream().toList())
                .orElseGet(List::of);
    }

    public static String getPlatformName() {
        return "fabric";
    }
}
