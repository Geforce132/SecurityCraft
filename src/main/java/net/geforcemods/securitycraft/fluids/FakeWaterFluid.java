package net.geforcemods.securitycraft.fluids;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class FakeWaterFluid extends ForgeFlowingFluid {
	protected FakeWaterFluid(Properties properties) {
		super(properties);
	}

	@Override
	public Fluid getFlowing() {
		return SCContent.FLOWING_FAKE_WATER.get();
	}

	@Override
	public Fluid getSource() {
		return SCContent.FAKE_WATER.get();
	}

	@Override
	public Item getBucket() {
		return SCContent.FAKE_WATER_BUCKET.get();
	}

	@Override
	public void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
		if (!state.isSource() && !state.getValue(FALLING)) {
			if (random.nextInt(64) == 0)
				level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
		}
		else if (random.nextInt(10) == 0)
			level.addParticle(ParticleTypes.UNDERWATER, pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(), 0.0D, 0.0D, 0.0D);
	}

	@Nullable
	@Override
	public ParticleOptions getDripParticle() {
		return ParticleTypes.DRIPPING_WATER;
	}

	@Override
	protected boolean canConvertToSource(Level level) {
		return level.getGameRules().getBoolean(SecurityCraft.RULE_FAKE_WATER_SOURCE_CONVERSION.get());
	}

	@Override
	public boolean canConvertToSource(FluidState state, Level level, BlockPos pos) {
		return canConvertToSource(level);
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
		BlockEntity be = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;

		Block.dropResources(state, level, pos, be);
	}

	@Override
	public int getSlopeFindDistance(LevelReader level) {
		return 4;
	}

	@Override
	public BlockState createLegacyBlock(FluidState state) {
		return SCContent.FAKE_WATER_BLOCK.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
	}

	@Override
	public boolean isSame(Fluid fluid) {
		return fluid == SCContent.FAKE_WATER.get() || fluid == SCContent.FLOWING_FAKE_WATER.get();
	}

	@Override
	public int getDropOff(LevelReader level) {
		return 1;
	}

	@Override
	public int getTickDelay(LevelReader level) {
		return 5;
	}

	@Override
	protected float getExplosionResistance() {
		return 100.0F;
	}

	public static class Flowing extends FakeWaterFluid {
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

	public static class Source extends FakeWaterFluid {
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