package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
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
	protected static final VoxelShape BOTTOM_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
	protected static final VoxelShape TOP_SHAPE = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

	public ReinforcedSlabBlock(Block.Properties properties, Block vB)
	{
		this(properties, () -> vB);
	}

	public ReinforcedSlabBlock(Block.Properties properties, Supplier<Block> vB)
	{
		super(properties, vB);
		setDefaultState(stateContainer.getBaseState().with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, false));
	}

	@Override
	public boolean isTransparent(BlockState state)
	{
		return state.get(TYPE) != SlabType.DOUBLE;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(TYPE, WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
	{
		SlabType type = state.get(TYPE);

		switch(type)
		{
			case DOUBLE:
				return VoxelShapes.fullCube();
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
		World world = ctx.getWorld();
		BlockPos pos = ctx.getPos();
		BlockState state = world.getBlockState(pos);
		TileEntity te = world.getTileEntity(pos);

		if(state.getBlock() == this)
		{
			if(te instanceof IOwnable && !((IOwnable)te).getOwner().isOwner(ctx.getPlayer()))
			{
				if(world.isRemote)
					PlayerUtils.sendMessageToPlayer(ctx.getPlayer(), ClientUtils.localize("messages.securitycraft:reinforcedSlab"), ClientUtils.localize("messages.securitycraft:reinforcedSlab.cannotDoubleSlab"), TextFormatting.RED);

				return state;
			}

			return state.with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, false);
		}
		else
		{
			FluidState fluidState = ctx.getWorld().getFluidState(pos);
			BlockState stateToSet = getDefaultState().with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
			Direction dir = ctx.getFace();

			return dir != Direction.DOWN && (dir == Direction.UP || !(ctx.getHitVec().y - pos.getY() > 0.5D)) ? stateToSet : stateToSet.with(TYPE, SlabType.TOP);
		}
	}

	@Override
	public boolean isReplaceable(BlockState state, BlockItemUseContext ctx)
	{
		ItemStack stack = ctx.getItem();
		SlabType type = state.get(TYPE);

		if(type != SlabType.DOUBLE && stack.getItem() == asItem())
		{
			if(ctx.replacingClickedOnBlock())
			{
				boolean clickedUpperHalf = ctx.getHitVec().y - ctx.getPos().getY() > 0.5D;
				Direction dir = ctx.getFace();

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
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public boolean receiveFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState)
	{
		return state.get(TYPE) != SlabType.DOUBLE ? IWaterLoggable.super.receiveFluid(world, pos, state, fluidState) : false;
	}

	@Override
	public boolean canContainFluid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid)
	{
		return state.get(TYPE) != SlabType.DOUBLE ? IWaterLoggable.super.canContainFluid(world, pos, state, fluid) : false;
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		if(state.get(WATERLOGGED))
			world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader world, BlockPos pos, PathType type)
	{
		switch(type)
		{
			case LAND:
				return false;
			case WATER:
				return world.getFluidState(pos).isTagged(FluidTags.WATER);
			case AIR:
				return false;
			default:
				return false;
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder)
	{
		if(state.get(TYPE) == SlabType.DOUBLE)
			return NonNullList.from(ItemStack.EMPTY, new ItemStack(this), new ItemStack(this));
		else return NonNullList.from(ItemStack.EMPTY, new ItemStack(this));
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState().with(TYPE, vanillaState.get(TYPE)).with(WATERLOGGED, vanillaState.get(WATERLOGGED));
	}
}