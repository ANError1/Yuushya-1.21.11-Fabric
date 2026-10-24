package com.yuushya.item;

import com.yuushya.registries.YuushyaRegistryData;
import com.yuushya.utils.YuushyaUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class YuushyaItemFactory {

    public static AbstractYuushyaItem create(Identifier id, YuushyaRegistryData.Item item) {
        return switch (item.classType) {
            case "HatItem" -> new HatItem(getItemProperties(id, item), item.properties.lines);
            case "StructureCreatorItem" ->
                    new StructureCreatorItem(getItemProperties(id, item), item.properties.lines, item.usage.createNbt, item.usage.cancelNbt);
            case "BlueprintItem" -> new BlueprintItem(getItemProperties(id, item), item.properties.lines);
            default -> new AbstractYuushyaItem(getItemProperties(id, item), item.properties.lines);
        };
    }

    /** 1.21.2 起物品必须携带注册 id。 */
    public static Item.Properties getItemProperties(Identifier id, YuushyaRegistryData.Item item) {
        Item.Properties properties = new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(item.properties.maxCount);
        if (item.properties.rarity != null && !item.properties.rarity.isEmpty())
            properties = properties.rarity(YuushyaUtils.toRarity(item.properties.rarity));
        if (item.properties.maxDamage != 0) properties = properties.durability(item.properties.maxDamage);
        if (item.properties.fireProof) properties = properties.fireResistant();
        return properties;
    }
}
