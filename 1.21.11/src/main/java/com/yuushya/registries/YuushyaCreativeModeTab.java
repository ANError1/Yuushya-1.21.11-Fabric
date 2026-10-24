package com.yuushya.registries;

import com.yuushya.Yuushya;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 创造模式物品栏。
 * <p>
 * 原实现依赖 Architectury 的 {@code CreativeTabRegistry} 与 {@code Item.Properties#arch$tab}。
 * 1.21.2 之后 {@code Item.Properties#tab} 已被移除，因此改为：注册标签页时声明内容来源，
 * 物品注册完成后通过 {@link #addItem(String, Supplier)} 归入对应分组。
 */
public final class YuushyaCreativeModeTab {

    private static final Map<String, RegistrySupplier<CreativeModeTab>> TABS = new LinkedHashMap<>();
    /** 分组名 -> 该分组下的物品（延迟取值，标签页打开时才求值）。 */
    private static final Map<String, List<Supplier<Item>>> CONTENT = new LinkedHashMap<>();

    public static final RegistrySupplier<CreativeModeTab> YUUSHYA_ITEM =
            create("item", () -> YuushyaRegistries.ITEMS.getInstanceOrDefault("form_trans_item", Items.APPLE));

    private YuushyaCreativeModeTab() {
    }

    public static RegistrySupplier<CreativeModeTab> create(String name, Supplier<Item> icon) {
        RegistrySupplier<CreativeModeTab> existing = TABS.get(name);
        if (existing != null) {
            return existing;
        }
        Identifier id = Identifier.fromNamespaceAndPath(Yuushya.MOD_ID, name);
        List<Supplier<Item>> content = CONTENT.computeIfAbsent(name, key -> new ArrayList<>());
        CreativeModeTab tab = FabricItemGroup.builder()
                .title(Component.translatable("itemGroup.yuushya." + name))
                .icon(() -> new ItemStack(icon.get()))
                .displayItems((parameters, output) -> content.forEach(item -> output.accept(item.get())))
                .build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceKey.create(Registries.CREATIVE_MODE_TAB, id), tab);
        RegistrySupplier<CreativeModeTab> supplier = new RegistrySupplier<>(id, tab);
        TABS.put(name, supplier);
        return supplier;
    }

    /** 把物品归入指定分组；分组不存在时归入默认的 item 分组。 */
    public static void addItem(String group, Supplier<Item> item) {
        String key = (group == null || !CONTENT.containsKey(group)) ? "item" : group;
        CONTENT.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
    }

    public static void addItem(String group, Item item) {
        addItem(group, () -> item);
    }

    public static void register(String name, String icon) {
        create(name, () -> {
            Identifier location = Identifier.tryParse(icon);
            if (location != null && icon.contains(":")) {
                Item item = BuiltInRegistries.ITEM.getValue(location);
                return item == null ? Items.APPLE : item;
            }
            return YuushyaRegistries.ITEMS.getInstanceOrDefault(icon, Items.APPLE);
        });
    }

    public static RegistrySupplier<CreativeModeTab> toGroup(String creativeModeTab) {
        return TABS.getOrDefault(creativeModeTab, YUUSHYA_ITEM);
    }
}
