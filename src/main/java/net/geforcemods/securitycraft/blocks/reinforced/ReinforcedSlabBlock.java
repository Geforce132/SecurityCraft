package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ReinforcedSlabBlock extends BaseReinforcedBlock implements IWaterLoggable
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
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(TYPE, WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
	{
		SlabType type = state.getValue(TYPE);

		switch(type)
		{
			case DOUBLE:
				return VoxelShapes.block();
			case TOP:
				return TOP_SHAPE;
			default:
				return BOTTOM_SHAPE;
		}
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		World world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockState state = world.getBlockState(pos);
		TileEntity te = world.getBlockEntity(pos);

		if(state.getBlock() == this)
		{
			if(te instanceof IOwnable && !((IOwnable)te).getOwner().isOwner(ctx.getPlayer()))
			{
				PlayerUtils.sendMessageToPlayer(ctx.getPlayer(), Utils.localize("messages.securitycraft:reinforcedSlab"), Utils.localize("messages.securitycraft:reinforcedSlab.cannotDoubleSlab"), TextFormatting.RED);

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
	public boolean canBeReplaced(BlockState state, BlockItemUseContext ctx)
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
	public boolean placeLiquid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState)
	{
		return state.getValue(TYPE) != SlabType.DOUBLE ? IWaterLoggable.super.placeLiquid(world, pos, state, fluidState) : false;
	}

	@Override
	public boolean canPlaceLiquid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid)
	{
		return state.getValue(TYPE) != SlabType.DOUBLE ? IWaterLoggable.super.canPlaceLiquid(world, pos, state, fluid) : false;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		if(state.getValue(WATERLOGGED))
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));

		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean isPathfindable(BlockState state, IBlockReader world, BlockPos pos, PathType type)
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