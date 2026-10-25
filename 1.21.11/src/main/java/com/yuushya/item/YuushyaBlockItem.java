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

    /**
     * 1.21.2 起 {@code Item#getDescriptionId()} 变成 final，且固定为 {@code item.<ns>.<path>}，
     * BlockItem 不再返回方块的翻译键。这里改走 {@link #getName(ItemStack)}，
     * 让方块物品继续使用 {@code block.yuushya.*}，自定义名称（命名牌）仍然优先生效。
     */
    @Override
    public Component getName(ItemStack itemStack) {
        return Component.translatable(this.getBlock().getDescriptionId());
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltips, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, tooltips, tooltipFlag);
        String base = this.getBlock().getDescriptionId();
        for (int i = 1; i <= tipLines; i++) {
            tooltips.accept(Component.translatable(base + ".line" + i));
        }
    }
}
