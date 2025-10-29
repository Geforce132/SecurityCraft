package net.geforcemods.securitycraft.blocks;

import org.joml.Vector3f;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.inventory.LensContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class LaserBlock extends DisguisableBlock {
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public LaserBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(POWERED, false).setValue(WATERLOGGED, false));
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		super.setPlacedBy(level, pos, state, entity, stack);
		setLaser(level, pos, entity instanceof Player player ? player : null);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		LaserBlockBlockEntity be = (LaserBlockBlockEntity) level.getBlockEntity(pos);

		if (be.isOwnedBy(player)) {
			if (!level.isClientSide) {
				if (!be.isEnabled())
					player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
				else
					player.openMenu(be);
			}

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	public void setLaser(Level level, BlockPos pos, Player player) {
		for (Direction facing : Direction.values()) {
			setLaser(level, pos, facing, player);
		}
	}

	public void setLaser(Level level, BlockPos pos, Direction facing, Player player) {
		LaserBlockBlockEntity thisBe = (LaserBlockBlockEntity) level.getBlockEntity(pos);

		if (!thisBe.isEnabled() || !thisBe.isSideEnabled(facing))
			return;

		int boundType = LaserFieldBlock.getBoundType(facing);

		for (int i = 1; i <= ConfigHandler.SERVER.laserBlockRange.get(); i++) {
			BlockPos offsetPos = pos.relative(facing, i);
			BlockState offsetState = level.getBlockState(offsetPos);
			Block offsetBlock = offsetState.getBlock();

			if (!offsetState.isAir() && !offsetState.canBeReplaced() && offsetBlock != SCContent.LASER_BLOCK.get())
				return;
			else if (offsetBlock == SCContent.LASER_BLOCK.get()) {
				LaserBlockBlockEntity thatBe = (LaserBlockBlockEntity) level.getBlockEntity(offsetPos);

				if (thisBe.getOwner().owns(thatBe) && thatBe.isEnabled()) {
					if (!thatBe.isSideEnabled(facing.getOpposite())) {
						thisBe.setSideEnabled(facing, false, null);
						return;
					}

					ModuleType failedType = thisBe.synchronizeWith(thatBe);

					if (failedType != null) {
						if (player != null) {
							PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:laser.sync_failed", Utils.getFormattedCoordinates(thatBe.getBlockPos()), Utils.localize(failedType.getTranslationKey())), ChatFormatting.RED);
							thisBe.setSideEnabled(facing, false, null);
							thatBe.setSideEnabled(facing.getOpposite(), false, null);
							player.closeContainer();
						}

						return;
					}

					for (int j = 1; j < i; j++) {
						offsetPos = pos.relative(facing, j);
						offsetState = level.getBlockState(offsetPos);

						if (offsetState.isAir() || offsetState.canBeReplaced()) {
							level.setBlockAndUpdate(offsetPos, SCContent.LASER_FIELD.get().getPotentiallyWaterloggedState(boundType, level, offsetPos));

							if (level.getBlockEntity(offsetPos) instanceof IOwnable ownable)
								ownable.setOwner(thisBe.getOwner().getUUID(), thisBe.getOwner().getName());
						}
					}

					thatBe.getLensContainer().setChanged();
				}

				return;
			}
		}
	}

	@Override
	public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
		if (!level.isClientSide())
			destroyAdjacentLasers(level, pos);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof LaserBlockBlockEntity be) {
			LensContainer lensContainer = be.getLensContainer();

			Containers.dropContents(level, pos, lensContainer);
			lensContainer.clearContent();
			level.updateNeighbourForOutputSignal(pos, this);
			LinkableBlockEntity.unlinkFromAllLinked(be);
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	public static void destroyAdjacentLasers(LevelAccessor level, BlockPos pos) {
		BlockUtils.removeInSequence((direction, state) -> {
			if (state.getBlock() != SCContent.LASER_FIELD.get())
				return false;

			return state.getValue(LaserFieldBlock.BOUNDTYPE) == LaserFieldBlock.getBoundType(direction);
		}, level, pos, Direction.values());
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		setLaser(level, pos, null);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		return state.getValue(POWERED) && level.getBlockEntity(pos) instanceof LaserBlockBlockEntity be && be.isModuleEnabled(ModuleType.REDSTONE) ? 15 : 0;
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		return getSignal(state, level, pos, side);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isClientSide && state.getValue(POWERED)) {
			level.setBlockAndUpdate(pos, state.setValue(POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, pos, SCContent.LASER_BLOCK.get());
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
		if (state.getValue(POWERED)) {
			double x = pos.getX() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.7F + (rand.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;
			float r = 0.6F + 0.4F;
			float g = Math.max(0.0F, 0.7F - 0.5F);
			float b = Math.max(0.0F, 0.6F - 0.7F);
			Vector3f vec = new Vector3f(r, g, b);

			level.addParticle(new DustParticleOptions(vec, 1), false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			level.addParticle(new DustParticleOptions(vec, 1), false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			level.addParticle(new DustParticleOptions(vec, 1), false, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			level.addParticle(new DustParticleOptions(vec, 1), false, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			level.addParticle(new DustParticleOptions(vec, 1), false, x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(POWERED, WATERLOGGED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LaserBlockBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return BaseEntityBlock.createTickerHelper(type, SCContent.LASER_BLOCK_BLOCK_ENTITY.get(), LevelUtils::blockEntityTicker);
	}
}
