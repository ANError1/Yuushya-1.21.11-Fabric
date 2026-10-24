package com.yuushya.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yuushya.Yuushya;
import com.yuushya.collision.data.CollisionItem;
import com.yuushya.registries.YuushyaRegistryData;
import com.yuushya.YuushyaPlatform;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.yuushya.Yuushya.MOD_ID;
import static com.yuushya.collision.CollisionFileReader.getCollisionMap;
import static com.yuushya.collision.CollisionFileReader.getVoxelShape;
import static com.yuushya.registries.YuushyaRegistryConfig.*;
import static com.yuushya.utils.GsonTools.NormalGSON;

//最主要的是读取两种文件：data/register/xxx.json, data/collision/xxx.json
public class AddonLoader {
    private static final Predicate<Path> ADDON_FILTER = path -> !Files.isDirectory(path) && path.getFileName().toString().endsWith("."+MOD_ID+".jar");
    private static final Predicate<Identifier> JSON_FILTER = resourceLocation -> resourceLocation.getPath().endsWith(".json");
    //只读取yuushya文件夹了现在
    private static final FallbackResourceManager YUUSHYA_MANAGER = new FallbackResourceManager(PackType.SERVER_DATA,MOD_ID);

    /**
     * 收集可能包含本模组 data/ 的路径。
     * <p>
     * 注意：开发环境下 mod 的根路径是目录（build/resources/main 等），发布后才是 jar，
     * 因此这里两种形式都要接受，否则开发环境会读不到 data/yuushya/register 下的注册表。
     */
    private static List<Path> candidateRootPaths(String modId, Class<?> ...clazz){
        List<Path> list = new ArrayList<>();
        if(modId!=null && YuushyaPlatform.isModLoaded(modId)){
            List<Path> paths = YuushyaPlatform.getModFilePaths(modId);
            if(paths!=null) for(Path res:paths){
                if(res!=null && !res.toString().isEmpty() && !list.contains(res)) list.add(res);
            }
        }
        for(Class<?> cls:clazz) {
            CodeSource codeSource = cls.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                try {
                    URL jarPath = codeSource.getLocation();
                    Path path = Paths.get(jarPath.toURI());
                    if(!list.contains(path)) list.add(path);
                } catch (URISyntaxException | IllegalArgumentException e) {
                    YuushyaLogger.warn("Yuushya: cannot resolve code source of " + cls + ": " + e);
                }
            }
        }
        return list;
    }

    /**
     * 取一个安全的包名。
     * <p>
     * 文件系统根路径（例如 zip 文件系统里的 "/"）的 {@code getFileName()} 会返回 null，
     * 直接 toString() 会 NPE 导致游戏启动崩溃。
     */
    private static String packName(Path path, int index){
        Path name = path.getFileName();
        if(name == null) name = path.toAbsolutePath().normalize().getFileName();
        String base = name != null ? name.toString() : "yuushya-root";
        return base + "#" + index;
    }

    private static boolean containsModData(Path path){
        if(path == null || !Files.exists(path)) return false;
        if(Files.isDirectory(path)) return Files.exists(path.resolve("data").resolve(MOD_ID));
        Path name = path.getFileName();
        return name != null && name.toString().endsWith(".jar");
    }

    private static PackResources openPack(Path path, int index){
        PackLocationInfo location = new PackLocationInfo(packName(path, index), Component.empty(), PackSource.DEFAULT, Optional.empty());
        return Files.isDirectory(path)
                ? new PathPackResources(location, path)
                : new FilePackResources.FileResourcesSupplier(path).openPrimary(location);
    }

    public static void loadResource(String modId, Class<?> ...clazz){
        int index = 0;
        for(Path path : candidateRootPaths(modId, clazz)){
            if(!containsModData(path)) continue;
            // 注意：这里不能关闭 packResource —— 管理器后续还要读它（jar 包关闭后再读会失败）
            try {
                YUUSHYA_MANAGER.push(openPack(path, index++));
                YuushyaLogger.info("Yuushya data root: " + path);
            } catch (Exception e) {
                YuushyaLogger.warn("Yuushya: failed to open data root " + path + " : " + e);
            }
        }
    }

    public static void loadPackResource(Path folder){
        if(folder == null || !Files.isDirectory(folder)) return;
        try(Stream<Path> files = Files.list(folder).filter(ADDON_FILTER).sorted(Comparator.comparing(Path::toString)) ){
            //TODO: 可以用Pack来包装，做一个addon管理Screen，见 PackSelectionScreen
            List<Path> fileList = files.toList();
            int index = 0;
            for(Path path:fileList){
                try {
                    YUUSHYA_MANAGER.push(openPack(path, index++));
                    YuushyaLogger.info("Yuushya addon: " + path);
                } catch (Exception e) {
                    YuushyaLogger.warn("Yuushya: failed to open addon " + path + " : " + e);
                }
            }
        } catch (IOException e) {e.printStackTrace();}
    }

    private static <V> List<Map.Entry<Identifier,V>> sortMapEntry(Set<Map.Entry<Identifier,V>> entrySet){
        return entrySet.stream().sorted(
                Comparator.comparing((Map.Entry<Identifier, V> a) -> a.getKey().getPath())
                .thenComparing(a -> a.getKey().getNamespace())).toList();
    }
    public static void getRegister() {
        for (Map.Entry<Identifier, Resource> entry : sortMapEntry(YUUSHYA_MANAGER.listResources("register", JSON_FILTER).entrySet())) { try {
            Resource resource = entry.getValue();
            try (BufferedReader reader = new BufferedReader(resource.openAsReader());) {
                JsonElement innerJson= JsonParser.parseReader(reader);
                mergeYuushyaRegistryBlockClass(innerJson);
                YuushyaRegistryData YuushyaData=NormalGSON.fromJson(innerJson, YuushyaRegistryData.class);
                addResultToRawMap(YuushyaData);
            }
        } catch (IOException e){ e.printStackTrace(); }}
    }

    public static void getCollision(){
        for (Map.Entry<Identifier, Resource> entry : sortMapEntry(YUUSHYA_MANAGER.listResources("collision", JSON_FILTER).entrySet())) { try {
            Identifier file = entry.getKey();
            Identifier namespaceId1 = Identifier.fromNamespaceAndPath(Yuushya.MOD_ID, file.getPath().substring("collision/".length(), file.getPath().length() - ".json".length()));
            Resource resource = entry.getValue();
            try (BufferedReader reader = new BufferedReader(resource.openAsReader())) {
                JsonElement jsonElement =JsonParser.parseReader(reader);
                CollisionItem collision = NormalGSON.fromJson(jsonElement ,CollisionItem.class);
                Map<String, VoxelShape> map = new HashMap<>();
                for(CollisionItem.Model variant:collision.blockstates){
                    map.put(variant.variant,getVoxelShape(variant));
                }
                getCollisionMap().put(namespaceId1.toString(),map);
                if(collision.children!=null){
                    for(String namespaceId:collision.children){
                        getCollisionMap().put(namespaceId,map);
                    }
                }
            }

        } catch (IOException e){ e.printStackTrace(); }}
    }



}
