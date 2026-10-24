package com.yuushya.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/**
 * 带说明行的方块物品。
 * <p>
 * 1.21.5 起 {@code Block#appendHoverText} 被移除，方块的物品提示统一由 {@link BlockItem} 提供，
 * 因此把原先写在方块匿名类里的 .lineN 提示搬到这里。
 */
public class YuushyaBlockItem extends BlockItem {

    private final int tipLines;

    public YuushyaBlockItem(Block block, Properties properties, int tipLines) {
        super(block, properties);
        this.tipLines = tipLines;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltips, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, tooltips, tooltipFlag);
        for (int i = 1; i <= tipLines; i++) {
            tooltips.accept(Component.translatable(this.getDescriptionId() + ".line" + i));
        }
    }
}
