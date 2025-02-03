package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.KeypadTrapdoorBlockEntity;
import net.geforcemods.securitycraft.blocks.reinforced.BaseIronTrapDoorBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;

public class KeypadTrapDoorBlock extends BaseIronTrapDoorBlock implements IDisguisable, IOverlayDisplay {
	public KeypadTrapDoorBlock(BlockBehaviour.Properties properties, BlockSetType blockSetType) {
		super(properties, blockSetType);
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != state.getBlock())
			return disguisedState.getDestroyProgress(player, level, pos);
		else
			return super.getDestroyProgress(state, player, level, pos);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);
		else
			return super.getShape(state, level, pos, ctx);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this) {
			if (state.getValue(OPEN))
				return Shapes.empty();
			else
				return disguisedState.getShape(level, pos, ctx);
		}
		else
			return super.getShape(state, level, pos, ctx);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		KeypadTrapdoorBlockEntity be = (KeypadTrapdoorBlockEntity) level.getBlockEntity(pos);

		if (state.getValue(OPEN) && be.getSignalLength() > 0)
			return InteractionResult.PASS;
		else if (!level.isClientSide) {
			if (be.isDisabled())
				player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else if (be.verifyPasscodeSet(level, pos, be, player)) {
				if (be.isDenied(player)) {
					if (be.sendsDenylistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);
				}
				else if (be.isAllowed(player)) {
					if (be.sendsAllowlistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), ChatFormatting.GREEN);

					activate(state, level, pos, be.getSignalLength());
				}
				else
					be.openPasscodeGUI(level, pos, player);
			}
		}

		return InteractionResult.SUCCESS;
	}

	public void activate(BlockState state, Level level, BlockPos pos, int signalLength) {
		level.setBlockAndUpdate(pos, state.cycle(OPEN));
		playSound(null, level, pos, true);

		if (signalLength > 0)
			level.scheduleTick(pos, this, signalLength);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (state.getValue(OPEN)) {
			level.setBlockAndUpdate(pos, state.setValue(OPEN, false));
			playSound(null, level, pos, false);
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof IPasscodeProtected be)
			SaltData.removeSalt(be.getSaltKey());

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean flag) {}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return super.getStateForPlacement(ctx).setValue(OPEN, false).setValue(POWERED, false);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KeypadTrapdoorBlockEntity(pos, state);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		return getDisguisedStack(level, pos);
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		AuxiliaryLightManager lightManager = level.getAuxLightManager(pos);
		int lightValue = 0;

		if (lightManager != null)
			lightValue = lightManager.getLightAt(pos);

		return lightValue > 0 ? lightValue : super.getLightEmission(state, level, pos);
	}

	@Override
	public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getSoundType(level, pos, entity);
		else
			return super.getSoundType(state, level, pos, entity);
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShadeBrightness(level, pos);
		else
			return super.getShadeBrightness(state, level, pos);
	}

	@Override
	public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, BlockState queryState, BlockPos queryPos) {
		return IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);
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
	public boolean hasDynamicLightEmission(BlockState state) {
		return true;
	}

	public static class Convertible implements IPasscodeConvertible {
		@Override
		public boolean isUnprotectedBlock(BlockState state) {
			return state.is(SCContent.REINFORCED_IRON_TRAPDOOR.get());
		}

		@Override
		public boolean isProtectedBlock(BlockState state) {
			return state.is(SCContent.KEYPAD_TRAPDOOR.get());
		}

		@Override
		public boolean protect(Player player, Level level, BlockPos pos) {
			return convert(level, pos, SCContent.KEYPAD_TRAPDOOR.get());
		}

		@Override
		public boolean unprotect(Player player, Level level, BlockPos pos) {
			return convert(level, pos, SCContent.REINFORCED_IRON_TRAPDOOR.get());
		}

		private boolean convert(Level level, BlockPos pos, Block convertedBlock) {
			BlockState state = level.getBlockState(pos);
			Direction facing = state.getValue(FACING);
			Half half = state.getValue(HALF);
			boolean waterlogged = state.getValue(WATERLOGGED);
			BlockEntity be = level.getBlockEntity(pos);
			CompoundTag tag;

			if (be instanceof IModuleInventory moduleInv)
				moduleInv.dropAllModules();

			tag = be.saveWithFullMetadata(level.registryAccess());
			level.setBlockAndUpdate(pos, convertedBlock.defaultBlockState().setValue(FACING, facing).setValue(OPEN, false).setValue(HALF, half).setValue(POWERED, false).setValue(WATERLOGGED, waterlogged));
			level.getBlockEntity(pos).loadWithComponents(tag, level.registryAccess());
			return true;
		}
	}
}
