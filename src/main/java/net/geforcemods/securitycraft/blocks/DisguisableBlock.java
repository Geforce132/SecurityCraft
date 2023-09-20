package net.geforcemods.securitycraft.blocks;

import java.util.Optional;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class DisguisableBlock extends OwnableBlock implements IOverlayDisplay, SimpleWaterloggedBlock {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	protected DisguisableBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	public static boolean isNormalCube(BlockState state, BlockGetter level, BlockPos pos) {
		if (state.getBlock() instanceof DisguisableBlock disguisableBlock) { //should not happen, but just to be safe
			BlockState disguisedState = disguisableBlock.getDisguisedStateOrDefault(state, level, pos);

			if (disguisedState.getBlock() != state.getBlock())
				return disguisedState.isRedstoneConductor(level, pos);
		}

		return state.getMaterial().isSolidBlocking() && state.isCollisionShapeFullBlock(level, pos);
	}

	public static boolean isSuffocating(BlockState state, BlockGetter level, BlockPos pos) {
		if (state.getBlock() instanceof DisguisableBlock disguisableBlock) { //should not happen, but just to be safe
			BlockState disguisedState = disguisableBlock.getDisguisedStateOrDefault(state, level, pos);

			if (disguisedState.getBlock() != state.getBlock())
				return disguisedState.isSuffocating(level, pos);
		}

		return state.getMaterial().blocksMotion() && state.isCollisionShapeFullBlock(level, pos);
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getLightEmission(level, pos);
		else
			return super.getLightEmission(state, level, pos);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return defaultBlockState().setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getSoundType(level, pos, entity);
		else
			return super.getSoundType(state, level, pos, entity);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);
		else
			return super.getShape(state, level, pos, ctx);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getCollisionShape(level, pos, ctx);
		else
			return super.getCollisionShape(state, level, pos, ctx);
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getOcclusionShape(level, pos);
		else
			return super.getOcclusionShape(state, level, pos);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED))
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShadeBrightness(level, pos);
		else
			return super.getShadeBrightness(state, level, pos);
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
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

	public final BlockState getDisguisedStateOrDefault(BlockState state, BlockGetter level, BlockPos pos) {
		return getDisguisedBlockState(level, pos).orElse(state);
	}

	public static Optional<BlockState> getDisguisedBlockState(BlockGetter level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof IModuleInventory be && be.isModuleEnabled(ModuleType.DISGUISE))
			return getDisguisedBlockStateFromStack(be.getModule(ModuleType.DISGUISE));

		return Optional.empty();
	}

	public static Optional<BlockState> getDisguisedBlockStateFromStack(ItemStack module) {
		if (!module.isEmpty()) {
			BlockState disguisedState = NbtUtils.readBlockState(module.getOrCreateTag().getCompound("SavedState"));

			if (disguisedState != null && disguisedState.getBlock() != Blocks.AIR)
				return Optional.of(disguisedState);
			else { //fallback, mainly for upgrading old worlds from before the state selector existed
				Block block = ((ModuleItem) module.getItem()).getBlockAddon(module.getTag());

				if (block != null)
					return Optional.of(block.defaultBlockState());
			}
		}

		return Optional.empty();
	}

	public ItemStack getDisguisedStack(BlockGetter level, BlockPos pos) {
		if (level != null && level.getBlockEntity(pos) instanceof IModuleInventory be) {
			ItemStack stack = be.isModuleEnabled(ModuleType.DISGUISE) ? be.getModule(ModuleType.DISGUISE) : ItemStack.EMPTY;

			if (!stack.isEmpty()) {
				Block block = ((ModuleItem) stack.getItem()).getBlockAddon(stack.getTag());

				if (block != null)
					return new ItemStack(block);
			}
		}

		return new ItemStack(this);
	}

	@Override
	public ItemStack getDisplayStack(Level level, BlockState state, BlockPos pos) {
		return getDisguisedStack(level, pos);
	}

	@Override
	public boolean shouldShowSCInfo(Level level, BlockState state, BlockPos pos) {
		return getDisguisedStack(level, pos).getItem() == asItem();
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		return getDisguisedStack(level, pos);
	}
}
