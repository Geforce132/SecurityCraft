package net.geforcemods.securitycraft.fluids;

import java.util.Random;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidAttributes;

public abstract class FakeLavaFluid extends FlowingFluid {
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
	protected FluidAttributes createAttributes() {
		//@formatter:off
		return FluidAttributes.builder(
				new ResourceLocation("block/lava_still"),
				new ResourceLocation("block/lava_flow"))
				.translationKey("block.minecraft.lava")
				.luminosity(15).density(3000).viscosity(6000).temperature(1300).build(this);
		//@formatter:on
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(World level, BlockPos pos, FluidState state, Random random) {
		BlockPos posAbove = pos.above();

		if (level.getBlockState(posAbove).isAir() && !level.getBlockState(posAbove).isSolidRender(level, posAbove)) {
			if (random.nextInt(100) == 0) {
				double x = pos.getX() + random.nextFloat();
				double y = pos.getY() + 1;
				double z = pos.getZ() + random.nextFloat();

				level.addParticle(ParticleTypes.LAVA, x, y, z, 0.0D, 0.0D, 0.0D);
				level.playLocalSound(x, y, z, SoundEvents.LAVA_POP, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}

			if (random.nextInt(200) == 0)
				level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
		}
	}

	@Override
	public void randomTick(World level, BlockPos pos, FluidState state, Random random) {
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

	private boolean isSurroundingBlockFlammable(World level, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (this.getCanBlockBurn(level, pos.relative(direction)))
				return true;
		}

		return false;
	}

	private boolean getCanBlockBurn(World level, BlockPos pos) {
		return level.isLoaded(pos) && level.getBlockState(pos).getMaterial().isFlammable();
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	@Override
	public IParticleData getDripParticle() {
		return ParticleTypes.DRIPPING_LAVA;
	}

	@Override
	protected void beforeDestroyingBlock(IWorld level, BlockPos pos, BlockState state) {
		triggerEffects(level, pos);
	}

	@Override
	public int getSlopeFindDistance(IWorldReader level) {
		return level.dimensionType().ultraWarm() ? 4 : 2;
	}

	@Override
	public BlockState createLegacyBlock(FluidState state) {
		return SCContent.FAKE_LAVA_BLOCK.get().defaultBlockState().setValue(FlowingFluidBlock.LEVEL, getLegacyLevel(state));
	}

	@Override
	public boolean isSame(Fluid fluid) {
		return fluid == SCContent.FAKE_LAVA.get() || fluid == SCContent.FLOWING_FAKE_LAVA.get();
	}

	@Override
	public int getDropOff(IWorldReader level) {
		return level.dimensionType().ultraWarm() ? 1 : 2;
	}

	@Override
	public boolean canBeReplacedWith(FluidState fluidState, IBlockReader level, BlockPos pos, Fluid fluid, Direction dir) {
		return fluidState.getHeight(level, pos) >= 0.44444445F && fluid.is(FluidTags.WATER);
	}

	@Override
	public int getTickDelay(IWorldReader level) {
		return level.dimensionType().ultraWarm() ? 10 : 30;
	}

	@Override
	public int getSpreadDelay(World level, BlockPos pos, FluidState fluidState1, FluidState fluidState2) {
		int i = getTickDelay(level);

		if (!fluidState1.isEmpty() && !fluidState2.isEmpty() && !fluidState1.getValue(FALLING) && !fluidState2.getValue(FALLING) && fluidState2.getHeight(level, pos) > fluidState1.getHeight(level, pos) && level.getRandom().nextInt(4) != 0)
			i *= 4;

		return i;
	}

	protected void triggerEffects(IWorld level, BlockPos pos) {
		level.levelEvent(1501, pos, 0);
	}

	@Override
	protected boolean canConvertToSource() {
		return false;
	}

	@Override
	protected void spreadTo(IWorld level, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
		if (direction == Direction.DOWN) {
			FluidState ifluidstate = level.getFluidState(pos);

			if (is(FluidTags.LAVA) && ifluidstate.is(FluidTags.WATER)) {
				if (state.getBlock() instanceof FlowingFluidBlock)
					level.setBlock(pos, ForgeEventFactory.fireFluidPlaceBlockEvent(level, pos, pos, Blocks.STONE.defaultBlockState()), 3);

				triggerEffects(level, pos);
				return;
			}
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
		@Override
		protected void createFluidStateDefinition(StateContainer.Builder<Fluid, FluidState> builder) {
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