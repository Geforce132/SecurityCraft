package net.geforcemods.securitycraft.blocks;

import java.util.Optional;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class DisguisableBlock extends OwnableBlock implements IOverlayDisplay, IWaterLoggable {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	protected DisguisableBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	public static boolean isNormalCube(BlockState state, IBlockReader level, BlockPos pos) {
		//should not happen, but just to be safe
		if (state.getBlock() instanceof DisguisableBlock) {
			BlockState disguisedState = ((DisguisableBlock) state.getBlock()).getDisguisedStateOrDefault(state, level, pos);

			if (disguisedState.getBlock() != state.getBlock())
				return disguisedState.isRedstoneConductor(level, pos);
		}

		return state.getMaterial().isSolidBlocking() && state.isCollisionShapeFullBlock(level, pos);
	}

	public static boolean isSuffocating(BlockState state, IBlockReader level, BlockPos pos) {
		//should not happen, but just to be safe
		if (state.getBlock() instanceof DisguisableBlock) {
			BlockState disguisedState = ((DisguisableBlock) state.getBlock()).getDisguisedStateOrDefault(state, level, pos);

			if (disguisedState.getBlock() != state.getBlock())
				return disguisedState.isSuffocating(level, pos);
		}

		return state.getMaterial().blocksMotion() && state.isCollisionShapeFullBlock(level, pos);
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader level, BlockPos pos) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getLightValue(level, pos);
		else
			return super.getLightValue(state, level, pos);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return defaultBlockState().setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public SoundType getSoundType(BlockState state, IWorldReader level, BlockPos pos, Entity entity) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getSoundType(level, pos, entity);
		else
			return super.getSoundType(state, level, pos, entity);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);
		else
			return super.getShape(state, level, pos, ctx);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getCollisionShape(level, pos, ctx);
		else
			return super.getCollisionShape(state, level, pos, ctx);
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state, IBlockReader level, BlockPos pos) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getOcclusionShape(level, pos);
		else
			return super.getOcclusionShape(state, level, pos);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED))
			level.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public float getShadeBrightness(BlockState state, IBlockReader level, BlockPos pos) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShadeBrightness(level, pos);
		else
			return super.getShadeBrightness(state, level, pos);
	}

	@Override
	public int getLightBlock(BlockState state, IBlockReader level, BlockPos pos) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getLightBlock(level, pos);
		else
			return super.getLightBlock(state, level, pos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	public final BlockState getDisguisedStateOrDefault(BlockState state, IBlockReader level, BlockPos pos) {
		return getDisguisedBlockState(level, pos).orElse(state);
	}

	public static Optional<BlockState> getDisguisedBlockState(IBlockReader level, BlockPos pos) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof IModuleInventory) {
			IModuleInventory be = (IModuleInventory) te;

			if (be.isModuleEnabled(ModuleType.DISGUISE))
				return getDisguisedBlockStateFromStack(be.getModule(ModuleType.DISGUISE));
		}

		return Optional.empty();
	}

	public static Optional<BlockState> getDisguisedBlockStateFromStack(ItemStack module) {
		if (!module.isEmpty()) {
			BlockState disguisedState = NBTUtil.readBlockState(module.getOrCreateTag().getCompound("SavedState"));

			if (disguisedState != null && disguisedState.getBlock() != Blocks.AIR)
				return Optional.of(disguisedState);
			else { //fallback, mainly for upgrading old worlds from before the state selector existed
				Block block = ModuleItem.getBlockAddon(module);

				if (block != null)
					return Optional.of(block.defaultBlockState());
			}
		}

		return Optional.empty();
	}

	public ItemStack getDisguisedStack(IBlockReader level, BlockPos pos) {
		if (level != null && level.getBlockEntity(pos) instanceof IModuleInventory) {
			IModuleInventory be = (IModuleInventory) level.getBlockEntity(pos);
			ItemStack stack = be.isModuleEnabled(ModuleType.DISGUISE) ? be.getModule(ModuleType.DISGUISE) : ItemStack.EMPTY;

			if (!stack.isEmpty()) {
				Block block = ModuleItem.getBlockAddon(stack);

				if (block != null)
					return new ItemStack(block);
			}
		}

		return new ItemStack(this);
	}

	@Override
	public ItemStack getDisplayStack(World level, BlockState state, BlockPos pos) {
		return getDisguisedStack(level, pos);
	}

	@Override
	public boolean shouldShowSCInfo(World level, BlockState state, BlockPos pos) {
		return getDisguisedStack(level, pos).getItem() == asItem();
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader level, BlockPos pos, PlayerEntity player) {
		return getDisguisedStack(level, pos);
	}
}
