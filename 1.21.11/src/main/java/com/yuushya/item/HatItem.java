package com.yuushya.item;

import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HatItem extends AbstractYuushyaItem implements EquipmentSlotProvider {
    public HatItem(Properties properties, Integer tipLines) {
        super(properties, tipLines);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack handItemStack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            if (player.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                player.setItemSlot(EquipmentSlot.HEAD, handItemStack.copy());
                handItemStack.setCount(0);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public EquipmentSlot getPreferredEquipmentSlot(LivingEntity entity, ItemStack stack) {
        return EquipmentSlot.HEAD;
    }
}
