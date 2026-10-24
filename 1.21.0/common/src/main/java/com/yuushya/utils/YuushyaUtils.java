package com.yuushya.utils;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.yuushya.registries.YuushyaRegistryData;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;


import java.awt.*;
import java.util.Arrays;

import static com.yuushya.block.blockstate.YuushyaBlockStates.*;

public class YuushyaUtils {


    public static int vertexSize() { return DefaultVertexFormat.BLOCK.getVertexSize() / 4;} // 一个顶点用多少位int表示，原版和开了光影的OptiFine不同所以得在这算出来

    public static MapColor toBlockMaterial(String material){
        return switch (material){
            case "air", "structure_void", "portal", "miscellaneous", "redstone_lamp", "glass", "barrier", "cake" ->MapColor.NONE;
            case "carpet", "web", "wool" ->MapColor.WOOL;
            case "plants", "tall_plants", "nether_plants", "leaves", "cactus", "coral", "gourd", "dragon_egg" ->MapColor.PLANT;
            case "underwater_plant", "sea_grass", "water", "bubble_column" ->MapColor.WATER;
            case "lava", "fire", "tnt" ->MapColor.FIRE;
            case "snow_layer", "snow_block", "powder_snow" ->MapColor.SNOW;
            case "sculk"->MapColor.COLOR_BLACK;
            case "clay"->MapColor.CLAY;
            case "soil"->MapColor.DIRT;
            case "solid_organic"->MapColor.GRASS;
            case "packed_ice", "ice" ->MapColor.ICE;
            case "sand"->MapColor.SAND;
            case "sponge"->MapColor.COLOR_YELLOW;
            case "shulker", "amethyst" ->MapColor.COLOR_PURPLE;
            case "wood", "nether_wood", "bamboo_sapling", "bamboo" ->MapColor.WOOD;
            case "stone", "piston" ->MapColor.STONE;
            default->MapColor.METAL;
        };
    }

//    public static MaterialColor toMaterialColor(String material){
//        return toBlockMaterial(material).getColor();
//    }

    public static SoundType toSound(String sound){
        return switch (sound){
            case "wood"-> SoundType.WOOD;
            case "gravel"-> SoundType.GRAVEL;
            case "plant"-> SoundType.GRASS;
            case "lily_pads"-> SoundType.LILY_PAD;
            case "stone"-> SoundType.STONE;
            case "metal"-> SoundType.METAL;
            case "glass"-> SoundType.GLASS;
            case "wool"-> SoundType.WOOL;
            case "sand"-> SoundType.SAND;
            case "snow"-> SoundType.SNOW;
            case "ladder"-> SoundType.LADDER;
            case "anvil"-> SoundType.ANVIL;
            case "slime"-> SoundType.SLIME_BLOCK;
            case "honey"-> SoundType.HONEY_BLOCK;
            case "wet_grass"-> SoundType.WET_GRASS;
            case "coral"-> SoundType.CORAL_BLOCK;
            case "bamboo"-> SoundType.BAMBOO;
            case "bamboo_sapling"-> SoundType.BAMBOO_SAPLING;
            case "scaffolding"-> SoundType.SCAFFOLDING;
            case "sweet_berry_bush"-> SoundType.SWEET_BERRY_BUSH;
            case "crop"-> SoundType.CROP;
            case "stem"-> SoundType.HARD_CROP;
            case "vine"-> SoundType.VINE;
            case "nether_wart"-> SoundType.NETHER_WART;
            case "lantern"-> SoundType.LANTERN;
            case "nether_stem"-> SoundType.STEM;
            case "nylium"-> SoundType.NYLIUM;
            case "fungus"-> SoundType.FUNGUS;
            case "root"-> SoundType.ROOTS;
            case "shroomlight"-> SoundType.SHROOMLIGHT;
            case "nether_vine"-> SoundType.WEEPING_VINES;
            case "nether_vine_lower_pitch"-> SoundType.TWISTING_VINES;
            case "soul_sand"-> SoundType.SOUL_SAND;
            case "soul_soil"-> SoundType.SOUL_SOIL;
            case "basalt"-> SoundType.BASALT;
            case "wart"-> SoundType.WART_BLOCK;
            case "netherrack"-> SoundType.NETHERRACK;
            case "nether_brick"-> SoundType.NETHER_BRICKS;
            case "nether_sprout"-> SoundType.NETHER_SPROUTS;
            case "nether_ore"-> SoundType.NETHER_ORE;
            case "bone"-> SoundType.BONE_BLOCK;
            case "netherite"-> SoundType.NETHERITE_BLOCK;
            case "ancient_debris"-> SoundType.ANCIENT_DEBRIS;
            case "lodestone"-> SoundType.LODESTONE;
            case "chain"-> SoundType.CHAIN;
            case "nether_gold"-> SoundType.NETHER_GOLD_ORE;
            case "gilded_blackstone"-> SoundType.GILDED_BLACKSTONE;
            case "candle"-> SoundType.CANDLE;
            case "amethyst"-> SoundType.AMETHYST;
            case "amethyst_cluster"-> SoundType.AMETHYST_CLUSTER;
            case "small_amethyst_bud"-> SoundType.SMALL_AMETHYST_BUD;
            case "medium_amethyst_bud"-> SoundType.MEDIUM_AMETHYST_BUD;
            case "large_amethyst_bud"-> SoundType.LARGE_AMETHYST_BUD;
            case "tuff"-> SoundType.TUFF;
            case "calcite"-> SoundType.CALCITE;
            case "dripstone_block"-> SoundType.DRIPSTONE_BLOCK;
            case "pointed_dripstone"-> SoundType.POINTED_DRIPSTONE;
            case "copper"-> SoundType.COPPER;
            case "cave_vines"-> SoundType.CAVE_VINES;
            case "spore_blossom"-> SoundType.SPORE_BLOSSOM;
            case "azalea"-> SoundType.AZALEA;
            case "flowering_azalea"-> SoundType.FLOWERING_AZALEA;
            case "moss_carpet"-> SoundType.MOSS_CARPET;
            case "moss"-> SoundType.MOSS;
            case "big_dripleaf"-> SoundType.BIG_DRIPLEAF;
            case "small_dripleaf"-> SoundType.SMALL_DRIPLEAF;
            case "rooted_dirt"-> SoundType.ROOTED_DIRT;
            case "hanging_roots"-> SoundType.HANGING_ROOTS;
            case "azalea_leaves"-> SoundType.AZALEA_LEAVES;
            case "sculk_sensor"-> SoundType.SCULK_SENSOR;
            case "glow_lichen"-> SoundType.GLOW_LICHEN;
            case "deepslate"-> SoundType.DEEPSLATE;
            case "deepslate_bricks"-> SoundType.DEEPSLATE_BRICKS;
            case "deepslate_tiles"-> SoundType.DEEPSLATE_TILES;
            case "polished_deepslate"-> SoundType.POLISHED_DEEPSLATE;
            default->SoundType.METAL;
        };
    }
    public static IntegerProperty getFormFromState(BlockState blockState){
        if(blockState.hasProperty(FORM2)) return FORM2;
        else if(blockState.hasProperty(FORM3)) return FORM3;
        else if(blockState.hasProperty(FORM4)) return FORM4;
        else if(blockState.hasProperty(FORM5)) return FORM5;
        else if(blockState.hasProperty(FORM6)) return FORM6;
        else if(blockState.hasProperty(FORM7)) return FORM7;
        else if(blockState.hasProperty(FORM8)) return FORM8;
        return null;
    }
    public static Rarity toRarity(String rarity) {
        return switch (rarity) {
            case "common" -> Rarity.COMMON;
            case "uncommon" -> Rarity.UNCOMMON;
            case "rare" -> Rarity.RARE;
            case "epic" -> Rarity.EPIC;
            default -> Rarity.COMMON;
        };
    }

    public static RenderType toRenderType(String renderType){
        return switch (renderType){
            case "cutout"->RenderType.cutout();
            case "cutoutmipped"->RenderType.cutoutMipped();
            case "translucent"->RenderType.translucent();
            case "solid"->RenderType.solid();
            default -> RenderType.cutout();
        };
    }
    public static BlockColor toBlockColor(String type, String value){
        return switch (type) {
            case "null" -> (blockState, blockAndTintGetter, blockPos, i) -> 0;
            case "singlecolor" -> (state, world, pos, tintIndex) -> Color.getColor(value).getRGB();
            case "vanilla" -> switch (value) {
                case "tall_plant" -> (blockState, blockAndTintGetter, blockPos, i) -> blockAndTintGetter == null || blockPos == null ? -1 : BiomeColors.getAverageGrassColor(blockAndTintGetter, blockState.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? blockPos.below() : blockPos);
                case "grass" -> (blockState, blockAndTintGetter, blockPos, i) -> blockAndTintGetter == null || blockPos == null ? GrassColor.get(0.5, 1.0) : BiomeColors.getAverageGrassColor(blockAndTintGetter, blockPos);
                case "spruce" -> (blockState, blockAndTintGetter, blockPos, i) -> FoliageColor.getEvergreenColor();
                case "birch" -> (blockState, blockAndTintGetter, blockPos, i) -> FoliageColor.getBirchColor();
                case "leaves" -> (blockState, blockAndTintGetter, blockPos, i) -> blockAndTintGetter == null || blockPos == null ? FoliageColor.getDefaultColor() : BiomeColors.getAverageFoliageColor(blockAndTintGetter, blockPos);
                case "water" -> (blockState, blockAndTintGetter, blockPos, i) -> blockAndTintGetter == null || blockPos == null ? -1 : BiomeColors.getAverageWaterColor(blockAndTintGetter, blockPos);
                case "redstone" -> (blockState, blockAndTintGetter, blockPos, i) -> RedStoneWireBlock.getColorForPower(blockState.getValue(RedStoneWireBlock.POWER));
                case "sugar_cane" -> (blockState, blockAndTintGetter, blockPos, i) -> blockAndTintGetter == null || blockPos == null ? -1 : BiomeColors.getAverageGrassColor(blockAndTintGetter, blockPos);
                case "attached_stem" -> (blockState, blockAndTintGetter, blockPos, i) -> 14731036;
                case "stem_with_age" -> (blockState, blockAndTintGetter, blockPos, i) -> {
                    int j = blockState.getValue(StemBlock.AGE);
                    int k = j * 32;
                    int l = 255 - j * 8;
                    int m = j * 4;
                    return k << 16 | l << 8 | m;
                };
                case "lily_pad" -> (blockState, blockAndTintGetter, blockPos, i) -> blockAndTintGetter == null || blockPos == null ? 7455580 : 2129968;
                default -> (blockState, blockAndTintGetter, blockPos, i) -> 0;
            };
            default -> (blockState, blockAndTintGetter, blockPos, i) -> 0;
        };
    }

    public static BlockBehaviour.OffsetType toOffsetType(String offset){
        if (offset==null) return BlockBehaviour.OffsetType.NONE;
        return switch (offset){
            case "xz"-> BlockBehaviour.OffsetType.XZ;
            case "xyz"-> BlockBehaviour.OffsetType.XYZ;
            default -> BlockBehaviour.OffsetType.NONE;
        };
    }

    //TODO
    public static BlockState getBlockState(BlockState blockState, LevelAccessor world, BlockPos blockPos){
        return blockState;
    }


    public static <T> ListTag toListTag(T... values){
        ListTag listTag=new ListTag();
        Arrays.stream(values).toList().forEach((e)->{
            if(e instanceof Float e1)
                listTag.add(FloatTag.valueOf(e1));
            else if( e instanceof Double e1)
                listTag.add(DoubleTag.valueOf(e1));
        });
        return listTag;
    }

    //TODO: 这里可以做详细设置
    public static BlockSetType toBlockSetType(YuushyaRegistryData.Block.Properties properties) {
        if(properties!=null){
            if("wood".equals(properties.material)) return BlockSetType.OAK;
            else return BlockSetType.IRON;
        }
        return BlockSetType.CHERRY;
    }
}
