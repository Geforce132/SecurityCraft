package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import com.mojang.math.Vector3f;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class LaserBlock extends DisguisableBlock {
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public LaserBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(POWERED, false).setValue(WATERLOGGED, false));
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		super.setPlacedBy(level, pos, state, entity, stack);

		if (!level.isClientSide)
			setLaser(level, pos);
	}

	public void setLaser(Level level, BlockPos pos) {
		LaserBlockBlockEntity thisBe = (LaserBlockBlockEntity) level.getBlockEntity(pos);

		for (Direction facing : Direction.values()) {
			int boundType = facing == Direction.UP || facing == Direction.DOWN ? 1 : (facing == Direction.NORTH || facing == Direction.SOUTH ? 2 : 3);

			inner:
			for (int i = 1; i <= ConfigHandler.SERVER.laserBlockRange.get(); i++) {
				BlockPos offsetPos = pos.relative(facing, i);
				BlockState offsetState = level.getBlockState(offsetPos);
				Block offsetBlock = offsetState.getBlock();

				if (!offsetState.isAir() && !offsetState.getMaterial().isReplaceable() && offsetBlock != SCContent.LASER_BLOCK.get())
					break inner;
				else if (offsetBlock == SCContent.LASER_BLOCK.get()) {
					LaserBlockBlockEntity thatBe = (LaserBlockBlockEntity) level.getBlockEntity(offsetPos);

					if (thisBe.getOwner().owns(thatBe)) {
						LinkableBlockEntity.link(thisBe, thatBe);

						for (ModuleType type : thatBe.getInsertedModules()) {
							thisBe.insertModule(thatBe.getModule(type), false);
						}

						if (thisBe.isEnabled() && thatBe.isEnabled()) {
							for (int j = 1; j < i; j++) {
								offsetPos = pos.relative(facing, j);
								offsetState = level.getBlockState(offsetPos);

								if (offsetState.isAir() || offsetState.getMaterial().isReplaceable()) {
									level.setBlockAndUpdate(offsetPos, SCContent.LASER_FIELD.get().defaultBlockState().setValue(LaserFieldBlock.BOUNDTYPE, boundType));

									if (level.getBlockEntity(offsetPos) instanceof IOwnable ownable)
										ownable.setOwner(thisBe.getOwner().getUUID(), thisBe.getOwner().getName());
								}
							}
						}
					}

					break inner;
				}
			}
		}
	}

	@Override
	public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
		if (!level.isClientSide())
			destroyAdjacentLasers(level, pos);
	}

	public static void destroyAdjacentLasers(LevelAccessor level, BlockPos pos) {
		BlockUtils.destroyInSequence(SCContent.LASER_FIELD.get(), level, pos, Direction.values());
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		setLaser(level, pos);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, LevelReader level, BlockPos pos, Direction side) {
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
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		if (!level.isClientSide && state.getValue(POWERED)) {
			level.setBlockAndUpdate(pos, state.setValue(POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, pos, SCContent.LASER_BLOCK.get());
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, Random rand) {
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
