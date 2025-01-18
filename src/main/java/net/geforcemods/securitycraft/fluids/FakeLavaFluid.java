package net.geforcemods.securitycraft.fluids;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class FakeLavaFluid extends ForgeFlowingFluid {
	protected FakeLavaFluid(Properties properties) {
		super(properties);
	}

	@Override
	public Fluid getFlowing() {
		return SCContent.FLOWING_FAKE_LAVA.get();
	}

	@Override
	public Fluid getSource() {
		return SCContent.FAKE_LAVA.get();
	}

	@Override
	public Item getBucket() {
		return SCContent.FAKE_LAVA_BUCKET.get();
	}

	@Override
	public void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
		BlockPos posAbove = pos.above();

		if (level.getBlockState(posAbove).isAir() && !level.getBlockState(posAbove).isSolidRender(level, posAbove)) {
			if (random.nextInt(100) == 0) {
				double x = pos.getX() + random.nextFloat();
				double y = pos.getY() + 1;
				double z = pos.getZ() + random.nextFloat();

				level.addParticle(ParticleTypes.LAVA, x, y, z, 0.0D, 0.0D, 0.0D);
				level.playLocalSound(x, y, z, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}

			if (random.nextInt(200) == 0)
				level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
		}
	}

	@Override
	public void randomTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
		if (level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
			int i = random.nextInt(3);

			if (i > 0) {
				BlockPos posToUpdate = pos;

				for (int j = 0; j < i; ++j) {
					posToUpdate = posToUpdate.offset(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);

					if (!level.isLoaded(posToUpdate))
						return;

					BlockState stateToUpdate = level.getBlockState(posToUpdate);

					if (stateToUpdate.isAir()) {
						if (isSurroundingBlockFlammable(level, posToUpdate)) {
							level.setBlockAndUpdate(posToUpdate, ForgeEventFactory.fireFluidPlaceBlockEvent(level, posToUpdate, pos, Blocks.FIRE.defaultBlockState()));
							return;
						}
					}
					else if (stateToUpdate.getMaterial().blocksMotion())
						return;
				}
			}
			else {
				for (int k = 0; k < 3; ++k) {
					BlockPos posToUpdate = pos.offset(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);

					if (!level.isLoaded(posToUpdate))
						return;

					BlockPos posAbove = posToUpdate.above();

					if (level.isEmptyBlock(posAbove) && this.getCanBlockBurn(level, posToUpdate))
						level.setBlockAndUpdate(posAbove, ForgeEventFactory.fireFluidPlaceBlockEvent(level, posAbove, pos, Blocks.FIRE.defaultBlockState()));
				}
			}
		}
	}

	private boolean isSurroundingBlockFlammable(Level level, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (getCanBlockBurn(level, pos.relative(direction)))
				return true;
		}

		return false;
	}

	private boolean getCanBlockBurn(Level level, BlockPos pos) {
		return level.isLoaded(pos) && level.getBlockState(pos).getMaterial().isFlammable();
	}

	@Nullable
	@Override
	public ParticleOptions getDripParticle() {
		return ParticleTypes.DRIPPING_LAVA;
	}

	@Override
	protected boolean canConvertToSource(Level level) {
		return level.getGameRules().getBoolean(SecurityCraft.RULE_FAKE_LAVA_SOURCE_CONVERSION.get());
	}

	@Override
	public boolean canConvertToSource(FluidState state, Level level, BlockPos pos) {
		return canConvertToSource(level);
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
		triggerEffects(level, pos);
	}

	@Override
	public int getSlopeFindDistance(LevelReader level) {
		return level.dimensionType().ultraWarm() ? 4 : 2;
	}

	@Override
	public BlockState createLegacyBlock(FluidState state) {
		return SCContent.FAKE_LAVA_BLOCK.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
	}

	@Override
	public boolean isSame(Fluid fluid) {
		return fluid == SCContent.FAKE_LAVA.get() || fluid == SCContent.FLOWING_FAKE_LAVA.get();
	}

	@Override
	public int getDropOff(LevelReader level) {
		return level.dimensionType().ultraWarm() ? 1 : 2;
	}

	@Override
	public boolean canBeReplacedWith(FluidState fluidState, BlockGetter level, BlockPos pos, Fluid fluid, Direction dir) {
		return fluidState.getHeight(level, pos) >= 0.44444445F && fluid.is(FluidTags.WATER);
	}

	@Override
	public int getTickDelay(LevelReader level) {
		return level.dimensionType().ultraWarm() ? 10 : 30;
	}

	@Override
	public int getSpreadDelay(Level level, BlockPos pos, FluidState fluidState1, FluidState fluidState2) {
		int tickDelay = getTickDelay(level);

		if (!fluidState1.isEmpty() && !fluidState2.isEmpty() && !fluidState1.getValue(FALLING) && !fluidState2.getValue(FALLING) && fluidState2.getHeight(level, pos) > fluidState1.getHeight(level, pos) && level.getRandom().nextInt(4) != 0)
			tickDelay *= 4;

		return tickDelay;
	}

	protected void triggerEffects(LevelAccessor level, BlockPos pos) {
		level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
	}

	@Override
	protected void spreadTo(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
		if (direction == Direction.DOWN && is(FluidTags.LAVA) && fluidState.is(FluidTags.WATER)) {
			if (state.getBlock() instanceof LiquidBlock)
				level.setBlock(pos, ForgeEventFactory.fireFluidPlaceBlockEvent(level, pos, pos, Blocks.STONE.defaultBlockState()), 3);

			triggerEffects(level, pos);
			return;
		}

		super.spreadTo(level, pos, state, direction, fluidState);
	}

	@Override
	protected boolean isRandomlyTicking() {
		return true;
	}

	@Override
	protected float getExplosionResistance() {
		return 100.0F;
	}

	public static class Flowing extends FakeLavaFluid {
		public Flowing(Properties properties) {
			super(properties);
		}

		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		@Override
		public boolean isSource(FluidState state) {
			return false;
		}
	}

	public static class Source extends FakeLavaFluid {
		public Source(Properties properties) {
			super(properties);
		}

		@Override
		public int getAmount(FluidState state) {
			return 8;
		}

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}
	}
}