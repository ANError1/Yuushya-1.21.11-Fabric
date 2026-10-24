package com.yuushya.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class AbstractYuushyaItem extends Item {

    private final Integer tipLines;//注释栏数

    public AbstractYuushyaItem(Properties properties, Integer tipLines) {
        super(properties);
        this.tipLines = tipLines;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltips, TooltipFlag tooltipFlag) {
        if (tipLines == null) return;
        for (int i = 1; i <= tipLines; i++) {
            tooltips.accept(Component.translatable(this.getDescriptionId() + ".line" + i));
        }
    }
}
