package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ReinforcedSlabBlock extends BaseReinforcedBlock implements SimpleWaterloggedBlock
{
	public static final EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape BOTTOM_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
	protected static final VoxelShape TOP_SHAPE = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

	public ReinforcedSlabBlock(Block.Properties properties, Block vB)
	{
		this(properties, () -> vB);
	}

	public ReinforcedSlabBlock(Block.Properties properties, Supplier<Block> vB)
	{
		super(properties, vB);
		registerDefaultState(stateDefinition.any().setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, false));
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state)
	{
		return state.getValue(TYPE) != SlabType.DOUBLE;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(TYPE, WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		SlabType type = state.getValue(TYPE);

		switch(type)
		{
			case DOUBLE:
				return Shapes.block();
			case TOP:
				return TOP_SHAPE;
			default:
				return BOTTOM_SHAPE;
		}
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockState state = world.getBlockState(pos);
		BlockEntity te = world.getBlockEntity(pos);

		if(state.getBlock() == this)
		{
			if(te instanceof IOwnable && !((IOwnable)te).getOwner().isOwner(ctx.getPlayer()))
			{
				PlayerUtils.sendMessageToPlayer(ctx.getPlayer(), Utils.localize("messages.securitycraft:reinforcedSlab"), Utils.localize("messages.securitycraft:reinforcedSlab.cannotDoubleSlab"), ChatFormatting.RED);

				return state;
			}

			return state.setValue(TYPE, SlabType.DOUBLE).setValue(WATERLOGGED, false);
		}
		else
		{
			FluidState fluidState = ctx.getLevel().getFluidState(pos);
			BlockState stateToSet = defaultBlockState().setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
			Direction dir = ctx.getClickedFace();

			return dir != Direction.DOWN && (dir == Direction.UP || !(ctx.getClickLocation().y - pos.getY() > 0.5D)) ? stateToSet : stateToSet.setValue(TYPE, SlabType.TOP);
		}
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext ctx)
	{
		ItemStack stack = ctx.getItemInHand();
		SlabType type = state.getValue(TYPE);

		if(type != SlabType.DOUBLE && stack.getItem() == asItem())
		{
			if(ctx.replacingClickedOnBlock())
			{
				boolean clickedUpperHalf = ctx.getClickLocation().y - ctx.getClickedPos().getY() > 0.5D;
				Direction dir = ctx.getClickedFace();

				if(type == SlabType.BOTTOM)
					return dir == Direction.UP || clickedUpperHalf && dir.getAxis().isHorizontal();
				else
					return dir == Direction.DOWN || !clickedUpperHalf && dir.getAxis().isHorizontal();

			}
			else return true;
		}
		else return false;

	}

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState)
	{
		return state.getValue(TYPE) != SlabType.DOUBLE ? SimpleWaterloggedBlock.super.placeLiquid(world, pos, state, fluidState) : false;
	}

	@Override
	public boolean canPlaceLiquid(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid)
	{
		return state.getValue(TYPE) != SlabType.DOUBLE ? SimpleWaterloggedBlock.super.canPlaceLiquid(world, pos, state, fluid) : false;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos)
	{
		if(state.getValue(WATERLOGGED))
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));

		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type)
	{
		switch(type)
		{
			case LAND:
				return false;
			case WATER:
				return world.getFluidState(pos).is(FluidTags.WATER);
			case AIR:
				return false;
			default:
				return false;
		}
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return defaultBlockState().setValue(TYPE, vanillaState.getValue(TYPE)).setValue(WATERLOGGED, vanillaState.getValue(WATERLOGGED));
	}
}