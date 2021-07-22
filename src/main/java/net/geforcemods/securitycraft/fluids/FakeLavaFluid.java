package net.geforcemods.securitycraft.fluids;

import java.util.Random;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;

public abstract class FakeLavaFluid extends FlowingFluid
{
	@Override
	public Fluid getFlowing()
	{
		return SCContent.FLOWING_FAKE_LAVA.get();
	}

	@Override
	public Fluid getSource()
	{
		return SCContent.FAKE_LAVA.get();
	}

	@Override
	public Item getBucket()
	{
		return SCContent.FAKE_LAVA_BUCKET.get();
	}

	@Override
	protected FluidAttributes createAttributes()
	{
		return FluidAttributes.builder(
				new ResourceLocation("block/lava_still"),
				new ResourceLocation("block/lava_flow"))
				.translationKey("block.minecraft.lava")
				.luminosity(15).density(3000).viscosity(6000).temperature(1300).build(this);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(Level world, BlockPos pos, FluidState state, Random random)
	{
		BlockPos blockpos = pos.above();

		if(world.getBlockState(blockpos).isAir() && !world.getBlockState(blockpos).isSolidRender(world, blockpos))
		{
			if(random.nextInt(100) == 0)
			{
				double x = pos.getX() + random.nextFloat();
				double y = pos.getY() + 1;
				double z = pos.getZ() + random.nextFloat();

				world.addParticle(ParticleTypes.LAVA, x, y, z, 0.0D, 0.0D, 0.0D);
				world.playLocalSound(x, y, z, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}

			if(random.nextInt(200) == 0)
				world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
		}

	}

	@Override
	public void randomTick(Level world, BlockPos pos, FluidState state, Random random)
	{
		if (world.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK))
		{
			int i = random.nextInt(3);

			if(i > 0)
			{
				BlockPos blockpos = pos;

				for(int j = 0; j < i; ++j)
				{
					blockpos = blockpos.offset(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);

					if(!world.isLoaded(blockpos))
						return;

					BlockState blockState = world.getBlockState(blockpos);

					if(blockState.isAir())
					{
						if(isSurroundingBlockFlammable(world, blockpos))
						{
							world.setBlockAndUpdate(blockpos, Blocks.FIRE.defaultBlockState());
							return;
						}
					}
					else if(blockState.getMaterial().blocksMotion())
						return;
				}
			}
			else
			{
				for(int k = 0; k < 3; ++k)
				{
					BlockPos blockpos1 = pos.offset(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);

					if(!world.isLoaded(blockpos1))
						return;

					if(world.isEmptyBlock(blockpos1.above()) && this.getCanBlockBurn(world, blockpos1))
						world.setBlockAndUpdate(blockpos1.above(), Blocks.FIRE.defaultBlockState());
				}
			}
		}
	}

	private boolean isSurroundingBlockFlammable(Level world, BlockPos pos)
	{
		for(Direction Direction : Direction.values())
		{
			if(this.getCanBlockBurn(world, pos.relative(Direction)))
				return true;
		}

		return false;
	}

	private boolean getCanBlockBurn(Level world, BlockPos pos)
	{
		return !world.isLoaded(pos) ? false : world.getBlockState(pos).getMaterial().isFlammable();
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	@Override
	public ParticleOptions getDripParticle()
	{
		return ParticleTypes.DRIPPING_LAVA;
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor world, BlockPos pos, BlockState state)
	{
		triggerEffects(world, pos);
	}

	@Override
	public int getSlopeFindDistance(LevelReader world)
	{
		return world.dimensionType().ultraWarm() ? 4 : 2;
	}

	@Override
	public BlockState createLegacyBlock(FluidState state)
	{
		return SCContent.FAKE_LAVA_BLOCK.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
	}

	@Override
	public boolean isSame(Fluid fluid)
	{
		return fluid == SCContent.FAKE_LAVA.get() || fluid == SCContent.FLOWING_FAKE_LAVA.get();
	}

	@Override
	public int getDropOff(LevelReader world)
	{
		return world.dimensionType().ultraWarm() ? 1 : 2;
	}

	@Override
	public boolean canBeReplacedWith(FluidState fluidState, BlockGetter world, BlockPos pos, Fluid fluid, Direction dir)
	{
		return fluidState.getHeight(world, pos) >= 0.44444445F && fluid.is(FluidTags.WATER);
	}

	@Override
	public int getTickDelay(LevelReader world)
	{
		return world.dimensionType().ultraWarm() ? 10 : 30;
	}

	@Override
	public int getSpreadDelay(Level world, BlockPos pos, FluidState fluidState1, FluidState fluidState2)
	{
		int i = getTickDelay(world);

		if(!fluidState1.isEmpty() && !fluidState2.isEmpty() && !fluidState1.getValue(FALLING) && !fluidState2.getValue(FALLING) && fluidState2.getHeight(world, pos) > fluidState1.getHeight(world, pos) && world.getRandom().nextInt(4) != 0)
			i *= 4;

		return i;
	}

	protected void triggerEffects(LevelAccessor world, BlockPos pos)
	{
		world.levelEvent(1501, pos, 0);
	}

	@Override
	protected boolean canConvertToSource()
	{
		return false;
	}

	@Override
	protected void spreadTo(LevelAccessor world, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState)
	{
		if(direction == Direction.DOWN)
		{
			FluidState ifluidstate = world.getFluidState(pos);

			if(is(FluidTags.LAVA) && ifluidstate.is(FluidTags.WATER))
			{
				if(blockState.getBlock() instanceof LiquidBlock)
					world.setBlock(pos, Blocks.STONE.defaultBlockState(), 3);

				triggerEffects(world, pos);
				return;
			}
		}

		super.spreadTo(world, pos, blockState, direction, fluidState);
	}

	@Override
	protected boolean isRandomlyTicking()
	{
		return true;
	}

	@Override
	protected float getExplosionResistance()
	{
		return 100.0F;
	}

	public static class Flowing extends FakeLavaFluid
	{
		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder)
		{
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getAmount(FluidState state)
		{
			return state.getValue(LEVEL);
		}

		@Override
		public boolean isSource(FluidState state)
		{
			return false;
		}
	}

	public static class Source extends FakeLavaFluid
	{
		@Override
		public int getAmount(FluidState state)
		{
			return 8;
		}

		@Override
		public boolean isSource(FluidState state)
		{
			return true;
		}
	}
}