package com.yuushya.block;

import com.yuushya.registries.YuushyaRegistries;
import com.yuushya.registries.YuushyaRegistryData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class SnowLayerLikeBlock extends SnowLayerBlock{
    public SnowLayerLikeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1));
    }
    @Override
    protected @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_LAYER[state.getValue(LAYERS)];
    }
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {}
    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        int i = state.getValue(LAYERS);
        if (useContext.getItemInHand().is(this.asItem()) && i < 8) {
            if (useContext.replacingClickedOnBlock()) {
                return useContext.getClickedFace() == Direction.UP;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    /*@Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int i = state.getValue(LAYERS);
        if ((player.getItemInHand(InteractionHand.MAIN_HAND).is(this.asItem())||
                player.getItemInHand(InteractionHand.MAIN_HAND).is(YuushyaRegistries.ITEMS.get("yuushya:snow_layer")))
                && i < 8) {
            BlockState new_state = state.setValue(LAYERS,Math.min(8, i + 1));
            level.setBlock(pos,new_state,1);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.FAIL;
    }*/
}
