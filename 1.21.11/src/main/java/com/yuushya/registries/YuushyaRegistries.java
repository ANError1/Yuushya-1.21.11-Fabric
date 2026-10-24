package com.yuushya.registries;

import com.google.gson.JsonObject;
import com.yuushya.Yuushya;
import com.yuushya.block.YuushyaBlockFactory;
import com.yuushya.entity.ChairEntity;
import com.yuushya.item.*;
import com.yuushya.item.data_component.Structure;
import com.yuushya.particle.YuushyaParticleBlock;
import com.yuushya.utils.YuushyaUtils;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.*;

import static com.yuushya.registries.YuushyaRegistryConfig.*;
import static com.yuushya.utils.GsonTools.NormalGSON;
import static com.yuushya.utils.GsonTools.combineYuushyaDataBlockJson;


public class YuushyaRegistries {
    public static final YuushyaDeferredRegister<Block> BLOCKS = new YuushyaDeferredRegister<>(Registries.BLOCK);
    public static final YuushyaDeferredRegister<Item> ITEMS = new YuushyaDeferredRegister<>(Registries.ITEM);
    public static final YuushyaDeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = new YuushyaDeferredRegister<>(Registries.BLOCK_ENTITY_TYPE);
    public static final YuushyaDeferredRegister<EntityType<?>> ENTITIES = new YuushyaDeferredRegister<>(Registries.ENTITY_TYPE);
    public static final YuushyaDeferredRegister<ParticleType<?>> PARTICLE_TYPES = new YuushyaDeferredRegister<>(Registries.PARTICLE_TYPE);
    public static final YuushyaDeferredRegister<DataComponentType<?>> DATA_COMPONENTS = new YuushyaDeferredRegister<>(Registries.DATA_COMPONENT_TYPE);

    public static final Map<String,YuushyaRegistryData.Block> BlockALL=new HashMap<>();
    public static final Map<String,YuushyaRegistryData.Block> BlockTemplate=new LinkedHashMap<>();
    public static final Map<String,YuushyaRegistryData.Block> BlockDefault=new LinkedHashMap<>();
    public static final Map<String,YuushyaRegistryData.Block> BlockRemain=new HashMap<>();
    public static final Map<String,YuushyaRegistryData.Block> TextureTypeMap=new LinkedHashMap<>();

    // ---- 1.21.2+ 注册需要 id ----

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(Yuushya.MOD_ID, name);
    }

    public static Item.Properties itemProperties(String name) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id(name)));
    }

    public static BlockBehaviour.Properties blockProperties(String name, YuushyaRegistryData.Block.Properties properties) {
        return YuushyaBlockFactory.getBlockProperties(id(name), properties);
    }

    private static int lines(YuushyaRegistryData.Block block) {
        return block.properties == null || block.properties.lines <= 0 ? 0 : block.properties.lines;
    }

    public static void registerRegistries(){
        for(YuushyaRegistryData.ItemGroup itemGroup: YuushyaRawItemGroupMap.values()){
            YuushyaCreativeModeTab.register(itemGroup.name,itemGroup.icon);
        }
        for (YuushyaRegistryData.Item item: YuushyaRawItemMap.values()){
            Identifier itemId = id(item.name);
            ITEMS.register(item.name,()-> YuushyaItemFactory.create(itemId, item));
            YuushyaCreativeModeTab.addItem(item.itemGroup, ITEMS.get(item.name));
        }
        for (YuushyaRegistryData.Block block:YuushyaRawBlockMap.values()){
            switch (block.classType){
                case "_comment","class"->{}
                case "remain"->{BlockRemain.put(block.name, block);}
                case "template"->{
                    BlockTemplate.put(block.name, block);
                }
                default -> {
                    BlockDefault.put(block.name,block);
                    if (block.classType.equals("block")){
                        if (block.renderType==null||block.renderType.isEmpty()) block.renderType="cutout";
                        if (block.itemGroup == null) block.itemGroup="extra_blocks";
                        if (block.texture==null){ block.texture=new YuushyaRegistryData.Block.Texture();block.texture.type="all";}
                    }
                    if (block.texture!=null&&block.texture.type!=null&&!block.texture.type.isEmpty()){
                        TextureTypeMap.put(block.name, block);
                    }
                }
            }
        }
        for (YuushyaRegistryData.Block blockRemain:BlockRemain.values()){
            Identifier blockIdentifier = Identifier.parse(blockRemain.name);
            if (blockRemain.texture==null){blockRemain.texture=new YuushyaRegistryData.Block.Texture();blockRemain.texture.type="all";}
            if (blockRemain.texture.value==null||blockRemain.texture.value.isEmpty()) {
                blockRemain.texture.value= Identifier.fromNamespaceAndPath(blockIdentifier.getNamespace(),"block/"+blockIdentifier.getPath()).toString();}
            if (blockRemain.texture.type!=null&&!blockRemain.texture.type.isEmpty()){
                TextureTypeMap.put(blockRemain.name, blockRemain);}
        }
        for (YuushyaRegistryData.Block templateBlock:BlockTemplate.values()) {
            int templateLines = templateBlock.properties == null ? 1 : templateBlock.properties.lines;
            ITEMS.register(templateBlock.name,()->new TemplateBlockItem(itemProperties(templateBlock.name),templateLines,templateBlock.name));
            YuushyaCreativeModeTab.addItem(templateBlock.itemGroup, ITEMS.get(templateBlock.name));
        }
        for(YuushyaRegistryData.Block block:BlockDefault.values()){
            BlockALL.put(block.name, block);
            BlockBehaviour.Properties properties = blockProperties(block.name, block.properties);
            BLOCKS.register(block.name, ()->YuushyaBlockFactory.create(properties, block));
            if (block.properties != null && block.properties.food != null) {
                ITEMS.register(block.name, ()->new FoodItem(BLOCKS.get(block.name).get(),block,itemProperties(block.name)));
            } else {
                ITEMS.register(block.name, ()->new YuushyaBlockItem(BLOCKS.get(block.name).get(),itemProperties(block.name),lines(block)));
            }
            YuushyaCreativeModeTab.addItem(block.itemGroup, ITEMS.get(block.name));
        }

        for (YuushyaRegistryData.Block templateBlock:BlockTemplate.values()){
            JsonObject templateBlockJson=NormalGSON.toJsonTree(templateBlock,YuushyaRegistryData.Block.class).getAsJsonObject();
            List<YuushyaRegistryData.Block> list=getTemplateUsageList(templateBlock);
            for(YuushyaRegistryData.Block block:list){
                JsonObject blockJson=NormalGSON.toJsonTree(block,YuushyaRegistryData.Block.class).getAsJsonObject();
                YuushyaRegistryData.Block blockNew=combineYuushyaDataBlockJson(blockJson,templateBlockJson);
                blockNew.properties.parent=block.name;
                String name; BlockBehaviour.Properties properties;
                if (block.classType.equals("remain")){
                    Identifier blockIdentifier = Identifier.parse(block.name);
                    properties = YuushyaBlockFactory.getBlockProperties(BlockBehaviour.Properties.ofFullCopy(BuiltInRegistries.BLOCK.getValue(blockIdentifier)),blockNew.properties);
                    if(blockNew.properties!=null)
                        properties.mapColor(MapColor.COLOR_PINK);
                    else
                        properties.mapColor(MapColor.NONE);
                    name=blockIdentifier.getPath();
                }
                else{
                    properties=YuushyaBlockFactory.getBlockProperties(blockNew.properties);
                    name= block.name;
                }
                blockNew.name=templateBlock.name+"_"+name;
                blockNew.classType = "template:"+templateBlock.name;
                BlockBehaviour.Properties templateChildProperties = YuushyaBlockFactory.copyPropertiesWithId(properties, id(blockNew.name));
                BLOCKS.register(blockNew.name, ()->YuushyaBlockFactory.create(templateChildProperties,blockNew));
                ITEMS.register(blockNew.name,()->new TemplateChildBlockItem(BLOCKS.get(blockNew.name).get(),itemProperties(blockNew.name),block.classType,templateBlock.name,block.name));
                YuushyaCreativeModeTab.addItem(blockNew.itemGroup, ITEMS.get(blockNew.name));
                BlockALL.put(blockNew.name,blockNew);
            }
        }

        for(YuushyaRegistryData.Particle particle: YuushyaRawParticleMap.values()){
            if (particle.spawner==null) particle.spawner=new YuushyaRegistryData.Block();
            if (particle.spawner.properties==null) {particle.spawner.properties=new YuushyaRegistryData.Block.Properties();}
            if (particle.spawner.name==null||particle.spawner.name.isEmpty()) particle.spawner.name=particle.name+"_spawner";
            String spawnerName = particle.spawner.name;
            BlockBehaviour.Properties spawnerProperties = blockProperties(spawnerName, particle.spawner.properties);
            BLOCKS.register(spawnerName, () -> new YuushyaParticleBlock(spawnerProperties, particle.spawner.properties.lines,"ParticleBlock",particle.spawner.usage,()-> (SimpleParticleType) PARTICLE_TYPES.get(particle.name).get()));
            ITEMS.register(spawnerName, () -> new BlockItem(BLOCKS.get(spawnerName).get(), itemProperties(spawnerName)));
            YuushyaCreativeModeTab.addItem(particle.spawner.itemGroup, ITEMS.get(spawnerName));
            PARTICLE_TYPES.register(particle.name, YuushyaParticleBlock.YuushyaParticleType::create);
        }
    }

    public static List<YuushyaRegistryData.Block> getTemplateUsageList(YuushyaRegistryData.Block templateBlock){
        List<YuushyaRegistryData.Block> list=new ArrayList<>();
        if (templateBlock.texture==null||(templateBlock.texture.forClass==null&&templateBlock.texture.forSpecified==null) ){
            list.addAll(TextureTypeMap.values());}
        else{
            if (templateBlock.texture.forClass!=null){
                list.addAll(TextureTypeMap.values().stream().filter((e)->templateBlock.texture.forClass.contains(e.texture.type)).toList());}
            if (templateBlock.texture.forSpecified!=null){
                templateBlock.texture.forSpecified.forEach((name)->{
                    Identifier identifier=Identifier.tryParse(name);
                    if (identifier!=null){
                        if (TextureTypeMap.containsKey(identifier.toString())) list.add(TextureTypeMap.get(identifier.toString()));
                        if (TextureTypeMap.containsKey(identifier.getPath())) list.add(TextureTypeMap.get(identifier.getPath()));
                    }
                });
            }
        }
        return list;
    }

    public static void registerAll(){
        ITEMS.register("form_trans_item",()->new FormTransItem(itemProperties("form_trans_item").stacksTo(1),2));
        YuushyaCreativeModeTab.addItem("item", ITEMS.get("form_trans_item"));
        ITEMS.register("blockstate_update_item",()->new BlockUpdateItem(itemProperties("blockstate_update_item").stacksTo(1),2));
        YuushyaCreativeModeTab.addItem("item", ITEMS.get("blockstate_update_item"));
        ITEMS.register("pilatory",()-> new SetHatItem(itemProperties("pilatory").stacksTo(64),2));
        YuushyaCreativeModeTab.addItem("item", ITEMS.get("pilatory"));

        CHAIR_ENTITY = ENTITIES.register("ride_entity",()->EntityType.Builder.of(ChairEntity::new, MobCategory.MISC)
                .sized(ChairEntity.WIDTH,ChairEntity.HEIGHT)
                .build(ResourceKey.create(Registries.ENTITY_TYPE, id("ride_entity"))));
        STRUCTURE = DATA_COMPONENTS.register("structure",()->DataComponentType.<Structure>builder().persistent(Structure.CODEC).networkSynchronized(Structure.STREAM_CODEC).build());
        TRANS_DIRECTION = DATA_COMPONENTS.register("mode",()->DataComponentType.<Integer>builder().persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    }


    public static void registerClient(){
        BlockALL.values().forEach((block)->{
            RegistrySupplier<Block> registered = BLOCKS.get(block.name);
            if (registered == null) return;
            BlockRenderLayerMap.putBlock(registered.get(), YuushyaUtils.toRenderType(block.renderType));
            if (block.colorTint!=null&&block.colorTint.colorType!=null&&!block.colorTint.colorType.isEmpty()&& !block.colorTint.colorType.equals("null")){
                ColorProviderRegistry.BLOCK.register(YuushyaUtils.toBlockColor(block.colorTint.colorType,block.colorTint.colorString), registered.get());
            }
        });
    }

    public static RegistrySupplier<EntityType<?>> CHAIR_ENTITY = null;
    public static RegistrySupplier<DataComponentType<?>> STRUCTURE = null;
    public static RegistrySupplier<DataComponentType<?>> TRANS_DIRECTION = null;
}
