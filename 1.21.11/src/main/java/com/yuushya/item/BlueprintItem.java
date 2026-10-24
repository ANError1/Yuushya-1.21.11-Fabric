package com.yuushya.item;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static com.yuushya.item.TemplateBlockItem.getStonecutterMenu;
import net.minecraft.network.chat.Component;

public class BlueprintItem  extends AbstractYuushyaItem {

    public BlueprintItem(Properties properties, Integer tipLines) {
        super(properties, tipLines);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ItemStack itemStack = player.getItemInHand(usedHand).copy();
        if(player.isCreative()) player.setItemInHand(usedHand,ItemStack.EMPTY);
        else player.getItemInHand(usedHand).setCount(0);
        player.openMenu(getMenuProvider(level,player.blockPosition(), itemStack));
        player.awardStat(Stats.INTERACT_WITH_STONECUTTER);
        return InteractionResult.CONSUME;
    }

    public MenuProvider getMenuProvider(Level level, BlockPos pos, ItemStack itemStack){
        return new SimpleMenuProvider((i, inventory, _player) -> getStonecutterMenu(i,inventory,level,pos,itemStack) , Component.translatable(getDescriptionId()));
    }
}
