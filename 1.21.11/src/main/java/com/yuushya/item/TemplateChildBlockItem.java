package com.yuushya.item;

import com.yuushya.Yuushya;
import net.minecraft.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

public class TemplateChildBlockItem extends BlockItem {

    private final String classType;
    private final String templateBlockName;
    private final String blockName;
    public TemplateChildBlockItem(Block block, Properties properties,String classType,String templateBlockName, String blockName) {
        super(block, properties);
        this.blockName = blockName;
        this.templateBlockName = templateBlockName;
        this.classType = classType;
    }
    @Override
    public Component getName(ItemStack stack) {
        String blockDescriptionId = Util.makeDescriptionId("block", classType.equals("remain")?Identifier.parse(blockName):Identifier.fromNamespaceAndPath(Yuushya.MOD_ID,blockName));
        String templateBlockDescriptionId = Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(Yuushya.MOD_ID,templateBlockName));
        return Component.translatable(templateBlockDescriptionId).append(" x ").append(Component.translatable(blockDescriptionId));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltips, TooltipFlag tooltipFlag) {

    }
}
