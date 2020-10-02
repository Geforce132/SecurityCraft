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
import net.minecraftforge.fluids.FluidAttributes;

public abstract class FakeLavaFluid extends FlowingFluid
{
	@Override
	public Fluid getFlowingFluid()
	{
		return SCContent.FLOWING_FAKE_LAVA.get();
	}

	@Override
	public Fluid getStillFluid()
	{
		return SCContent.FAKE_LAVA.get();
	}

	@Override
	public Item getFilledBucket()
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
	public void animateTick(World world, BlockPos pos, FluidState state, Random random)
	{
		BlockPos blockpos = pos.up();

		if(world.getBlockState(blockpos).isAir() && !world.getBlockState(blockpos).isOpaqueCube(world, blockpos))
		{
			if(random.nextInt(100) == 0)
			{
				double x = pos.getX() + random.nextFloat();
				double y = pos.getY() + 1;
				double z = pos.getZ() + random.nextFloat();

				world.addParticle(ParticleTypes.LAVA, x, y, z, 0.0D, 0.0D, 0.0D);
				world.playSound(x, y, z, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}

			if(random.nextInt(200) == 0)
				world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
		}

	}

	@Override
	public void randomTick(World world, BlockPos pos, FluidState state, Random random)
	{
		if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK))
		{
			int i = random.nextInt(3);

			if(i > 0)
			{
				BlockPos blockpos = pos;

				for(int j = 0; j < i; ++j)
				{
					blockpos = blockpos.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);

					if(!world.isBlockPresent(blockpos))
						return;

					BlockState blockState = world.getBlockState(blockpos);

					if(blockState.isAir())
					{
						if(isSurroundingBlockFlammable(world, blockpos))
						{
							world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
							return;
						}
					}
					else if(blockState.getMaterial().blocksMovement())
						return;
				}
			}
			else
			{
				for(int k = 0; k < 3; ++k)
				{
					BlockPos blockpos1 = pos.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);

					if(!world.isBlockPresent(blockpos1))
						return;

					if(world.isAirBlock(blockpos1.up()) && this.getCanBlockBurn(world, blockpos1))
						world.setBlockState(blockpos1.up(), Blocks.FIRE.getDefaultState());
				}
			}
		}
	}

	private boolean isSurroundingBlockFlammable(World world, BlockPos pos)
	{
		for(Direction Direction : Direction.values())
		{
			if(this.getCanBlockBurn(world, pos.offset(Direction)))
				return true;
		}

		return false;
	}

	private boolean getCanBlockBurn(World world, BlockPos pos)
	{
		return !world.isBlockPresent(pos) ? false : world.getBlockState(pos).getMaterial().isFlammable();
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	@Override
	public IParticleData getDripParticleData()
	{
		return ParticleTypes.DRIPPING_LAVA;
	}

	@Override
	protected void beforeReplacingBlock(IWorld world, BlockPos pos, BlockState state)
	{
		triggerEffects(world, pos);
	}

	@Override
	public int getSlopeFindDistance(IWorldReader world)
	{
		return world.getDimensionType().isUltrawarm() ? 4 : 2;
	}

	@Override
	public BlockState getBlockState(FluidState state)
	{
		return SCContent.FAKE_LAVA_BLOCK.get().getDefaultState().with(FlowingFluidBlock.LEVEL, getLevelFromState(state));
	}

	@Override
	public boolean isEquivalentTo(Fluid fluid)
	{
		return fluid == SCContent.FAKE_LAVA.get() || fluid == SCContent.FLOWING_FAKE_LAVA.get();
	}

	@Override
	public int getLevelDecreasePerBlock(IWorldReader world)
	{
		return world.getDimensionType().isUltrawarm() ? 1 : 2;
	}

	@Override
	public boolean canDisplace(FluidState fluidState, IBlockReader world, BlockPos pos, Fluid fluid, Direction dir)
	{
		return fluidState.getActualHeight(world, pos) >= 0.44444445F && fluid.isIn(FluidTags.WATER);
	}

	@Override
	public int getTickRate(IWorldReader world)
	{
		return world.getDimensionType().isUltrawarm() ? 10 : 30;
	}

	@Override
	public int func_215667_a(World world, BlockPos pos, FluidState fluidState1, FluidState fluidState2)
	{
		int i = getTickRate(world);

		if(!fluidState1.isEmpty() && !fluidState2.isEmpty() && !fluidState1.get(FALLING) && !fluidState2.get(FALLING) && fluidState2.getActualHeight(world, pos) > fluidState1.getActualHeight(world, pos) && world.getRandom().nextInt(4) != 0)
			i *= 4;

		return i;
	}

	protected void triggerEffects(IWorld world, BlockPos pos)
	{
		world.playEvent(1501, pos, 0);
	}

	@Override
	protected boolean canSourcesMultiply()
	{
		return false;
	}

	@Override
	protected void flowInto(IWorld world, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState)
	{
		if(direction == Direction.DOWN)
		{
			FluidState ifluidstate = world.getFluidState(pos);

			if(isIn(FluidTags.LAVA) && ifluidstate.isTagged(FluidTags.WATER))
			{
				if(blockState.getBlock() instanceof FlowingFluidBlock)
					world.setBlockState(pos, Blocks.STONE.getDefaultState(), 3);

				triggerEffects(world, pos);
				return;
			}
		}

		super.flowInto(world, pos, blockState, direction, fluidState);
	}

	@Override
	protected boolean ticksRandomly()
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
		protected void fillStateContainer(StateContainer.Builder<Fluid, FluidState> builder)
		{
			super.fillStateContainer(builder);
			builder.add(LEVEL_1_8);
		}

		@Override
		public int getLevel(FluidState state)
		{
			return state.get(LEVEL_1_8);
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
		public int getLevel(FluidState state)
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