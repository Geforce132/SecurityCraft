package net.geforcemods.securitycraft.blocks;

import java.util.Optional;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
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

	public DisguisableBlock(Block.Properties properties) {
		super(properties);
	}

	public static boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos) {
		//should not happen, but just to be safe
		if (state.getBlock() instanceof DisguisableBlock) {
			BlockState disguisedState = ((DisguisableBlock) state.getBlock()).getDisguisedStateOrDefault(state, world, pos);

			if (disguisedState.getBlock() != state.getBlock())
				return disguisedState.isRedstoneConductor(world, pos);
		}

		return state.getMaterial().isSolidBlocking() && state.isCollisionShapeFullBlock(world, pos);
	}

	public static boolean isSuffocating(BlockState state, IBlockReader world, BlockPos pos) {
		//should not happen, but just to be safe
		if (state.getBlock() instanceof DisguisableBlock) {
			BlockState disguisedState = ((DisguisableBlock) state.getBlock()).getDisguisedStateOrDefault(state, world, pos);

			if (disguisedState.getBlock() != state.getBlock())
				return disguisedState.isSuffocating(world, pos);
		}

		return state.getMaterial().blocksMotion() && state.isCollisionShapeFullBlock(world, pos);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return defaultBlockState().setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, Entity entity) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getSoundType(world, pos, entity);
		else
			return super.getSoundType(state, world, pos, entity);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(world, pos, ctx);
		else
			return super.getShape(state, world, pos, ctx);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getCollisionShape(world, pos, ctx);
		else
			return super.getCollisionShape(state, world, pos, ctx);
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state, IBlockReader world, BlockPos pos) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getOcclusionShape(world, pos);
		else
			return super.getOcclusionShape(state, world, pos);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED))
			level.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public float getShadeBrightness(BlockState state, IBlockReader world, BlockPos pos) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShadeBrightness(world, pos);
		else
			return super.getShadeBrightness(state, world, pos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	public final BlockState getDisguisedStateOrDefault(BlockState state, IBlockReader world, BlockPos pos) {
		return getDisguisedBlockState(world, pos).orElse(state);
	}

	public Optional<BlockState> getDisguisedBlockState(IBlockReader world, BlockPos pos) {
		if (world.getBlockEntity(pos) instanceof IModuleInventory) {
			IModuleInventory te = (IModuleInventory) world.getBlockEntity(pos);
			ItemStack module = te.isModuleEnabled(ModuleType.DISGUISE) ? te.getModule(ModuleType.DISGUISE) : ItemStack.EMPTY;

			if (!module.isEmpty()) {
				BlockState disguisedState = NBTUtil.readBlockState(module.getOrCreateTag().getCompound("SavedState"));

				if (disguisedState != null && disguisedState.getBlock() != Blocks.AIR)
					return Optional.of(disguisedState);
				else { //fallback, mainly for upgrading old worlds from before the state selector existed
					Block block = ((ModuleItem) module.getItem()).getBlockAddon(module.getTag());

					if (block != null)
						return Optional.of(block.defaultBlockState());
				}
			}
		}

		return Optional.empty();
	}

	public ItemStack getDisguisedStack(IBlockReader world, BlockPos pos) {
		if (world != null && world.getBlockEntity(pos) instanceof IModuleInventory) {
			IModuleInventory te = (IModuleInventory) world.getBlockEntity(pos);
			ItemStack stack = te.isModuleEnabled(ModuleType.DISGUISE) ? te.getModule(ModuleType.DISGUISE) : ItemStack.EMPTY;

			if (!stack.isEmpty()) {
				Block block = ((ModuleItem) stack.getItem()).getBlockAddon(stack.getTag());

				if (block != null)
					return new ItemStack(block);
			}
		}

		return new ItemStack(this);
	}

	@Override
	public ItemStack getDisplayStack(World world, BlockState state, BlockPos pos) {
		return getDisguisedStack(world, pos);
	}

	@Override
	public boolean shouldShowSCInfo(World world, BlockState state, BlockPos pos) {
		return getDisguisedStack(world, pos).getItem() == asItem();
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return getDisguisedStack(world, pos);
	}
}
