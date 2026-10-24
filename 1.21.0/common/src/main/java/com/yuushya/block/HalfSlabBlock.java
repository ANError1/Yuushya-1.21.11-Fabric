package com.yuushya.block;

import com.mojang.serialization.MapCodec;
import com.yuushya.block.blockstate.HalfSlabState;
import com.yuushya.block.blockstate.YuushyaBlockStates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HalfSlabBlock extends Block implements SimpleWaterloggedBlock {
    public static final MapCodec<HalfSlabBlock> CODEC = simpleCodec(HalfSlabBlock::new);
    public static final EnumProperty<HalfSlabState> TYPE;
    public static final EnumProperty<HalfSlabState> OTHER;
    public static final BooleanProperty WATERLOGGED;
    public @NotNull MapCodec<? extends HalfSlabBlock> codec() {
        return CODEC;
    }
    public HalfSlabBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(TYPE, HalfSlabState.BOTTOM).setValue(OTHER, HalfSlabState.NONE).setValue(WATERLOGGED, false));
    }
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return state.getValue(TYPE) != HalfSlabState.BOTH && state.getValue(OTHER) != HalfSlabState.BOTH;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE, OTHER, WATERLOGGED);
    }
    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        HalfSlabState type = state.getValue(TYPE);
        HalfSlabState other = state.getValue(OTHER);
        VoxelShape shape0 = switch (type){
            case BOTH -> Shapes.join(Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0), BooleanOp.OR);
            case TOP -> Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
            case BOTTOM -> Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
            case NONE -> Shapes.empty();
        };
        VoxelShape shape1 = switch (other){
            case BOTH -> Block.box(0.0, 4.0, 0.0, 16.0, 12.0, 16.0);
            case TOP -> Block.box(0.0, 8.0, 0.0, 16.0, 12.0, 16.0);
            case BOTTOM -> Block.box(0.0, 4.0, 0.0, 16.0, 8.0, 16.0);
            case NONE -> Shapes.empty();
        };
        return Shapes.join(shape0,shape1, BooleanOp.OR);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = context.getLevel().getBlockState(blockPos);
        Direction direction = context.getClickedFace();
        double clickPos = context.getClickLocation().y - (double)blockPos.getY();
        if (blockState.is(this)) {
            if ((clickPos <= 0.25 && blockState.getValue(TYPE) == HalfSlabState.TOP) || (clickPos >= 0.75 && blockState.getValue(TYPE) == HalfSlabState.BOTTOM)){
                return blockState.setValue(TYPE, HalfSlabState.BOTH);
            } else if ((clickPos >= 0.5 && clickPos <= 0.75 && blockState.getValue(OTHER) == HalfSlabState.BOTTOM) || (clickPos >= 0.25 && clickPos <= 0.5 && blockState.getValue(OTHER) == HalfSlabState.TOP)){
                return blockState.setValue(OTHER, HalfSlabState.BOTH);
            }
            else if (clickPos <= 0.25 && blockState.getValue(TYPE) == HalfSlabState.NONE) {
                return blockState.setValue(TYPE, HalfSlabState.BOTTOM);
            } else if (clickPos >= 0.25 && clickPos <= 0.5 && blockState.getValue(OTHER) == HalfSlabState.NONE) {
                return blockState.setValue(OTHER, HalfSlabState.BOTTOM);
            } else if (clickPos >= 0.5 && clickPos <= 0.75 && blockState.getValue(OTHER) == HalfSlabState.NONE) {
                return blockState.setValue(OTHER, HalfSlabState.TOP);
            } else if (clickPos >= 0.75 && blockState.getValue(TYPE) == HalfSlabState.NONE) {
                return blockState.setValue(TYPE, HalfSlabState.TOP);
            }
            return null;
        } else {
            FluidState fluidState = context.getLevel().getFluidState(blockPos);
            BlockState blockState2 = this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
            if (direction == Direction.UP || clickPos <= 0.25){
                return blockState2.setValue(TYPE, HalfSlabState.BOTTOM);
            } else if (clickPos > 0.25 && clickPos <= 0.5 ){
                return blockState2.setValue(OTHER, HalfSlabState.BOTTOM).setValue(TYPE, HalfSlabState.NONE);
            } else if (clickPos > 0.5 && clickPos <= 0.75 ){
                return blockState2.setValue(OTHER, HalfSlabState.TOP).setValue(TYPE, HalfSlabState.NONE);
            } else if (direction == Direction.DOWN || clickPos > 0.75) {
                return blockState2.setValue(TYPE, HalfSlabState.TOP);
            } else {
                return null;
            }
        }
    }
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        ItemStack itemStack = useContext.getItemInHand();
        if (state.getValue(TYPE) == HalfSlabState.NONE && state.getValue(OTHER) == HalfSlabState.NONE){
            return true;
        }else if (itemStack.is(this.asItem())) {
            double clickPos = useContext.getClickLocation().y - (double)useContext.getClickedPos().getY();
            return (state.getValue(TYPE) == HalfSlabState.BOTTOM && clickPos >= 0.75) || (state.getValue(OTHER) == HalfSlabState.BOTTOM && clickPos >= 0.5 && clickPos <= 0.75)
                    || (state.getValue(TYPE) == HalfSlabState.TOP && clickPos <= 0.25) || (state.getValue(OTHER) == HalfSlabState.TOP && clickPos >= 0.25 && clickPos <= 0.5)
                    || (state.getValue(TYPE) == HalfSlabState.NONE && (clickPos <= 0.25 || clickPos >= 0.75)) || (state.getValue(OTHER) == HalfSlabState.NONE && clickPos >= 0.25 && clickPos <= 0.75);
        } else {
            return false;
        }
    }
    protected @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return !(state.getValue(TYPE) == HalfSlabState.BOTH && state.getValue(OTHER) == HalfSlabState.BOTH) && SimpleWaterloggedBlock.super.placeLiquid(level, pos, state, fluidState);
    }

    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return !(state.getValue(TYPE) == HalfSlabState.BOTH && state.getValue(OTHER) == HalfSlabState.BOTH) && SimpleWaterloggedBlock.super.canPlaceLiquid(player, level, pos, state, fluid);
    }

    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    static {
        TYPE = YuushyaBlockStates.HS_TYPE;
        OTHER = YuushyaBlockStates.HS_OTHER;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
    }
}
