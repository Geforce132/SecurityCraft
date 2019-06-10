package net.geforcemods.securitycraft.fluids;

import java.util.Random;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class FakeLavaFluid extends FlowingFluid
{
	@Override
	public Fluid getFlowingFluid()
	{
		return SCContent.flowingFakeLava;
	}

	@Override
	public Fluid getStillFluid()
	{
		return SCContent.fakeLava;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.SOLID;
	}

	@Override
	public Item getFilledBucket()
	{
		return SCContent.fLavaBucket;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(World world, BlockPos pos, IFluidState state, Random random)
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
	public void randomTick(World world, BlockPos pos, IFluidState state, Random random)
	{
		if (world.getGameRules().getBoolean("doFireTick"))
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

					BlockState BlockState = world.getBlockState(blockpos);

					if(BlockState.isAir())
					{
						if(isSurroundingBlockFlammable(world, blockpos))
						{
							world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
							return;
						}
					}
					else if(BlockState.getMaterial().blocksMovement())
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

	private boolean isSurroundingBlockFlammable(IWorldReader world, BlockPos pos)
	{
		for(Direction Direction : Direction.values())
		{
			if(this.getCanBlockBurn(world, pos.offset(Direction)))
				return true;
		}

		return false;
	}

	private boolean getCanBlockBurn(IWorldReader world, BlockPos pos)
	{
		return pos.getY() >= 0 && pos.getY() < 256 && !world.isBlockLoaded(pos) ? false : world.getBlockState(pos).getMaterial().isFlammable();
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
		return world.getDimension().doesWaterVaporize() ? 4 : 2;
	}

	@Override
	public BlockState getBlockState(IFluidState state)
	{
		return SCContent.fakeLavaBlock.getDefaultState().with(BlockFlowingFluid.LEVEL, getLevelFromState(state));
	}

	@Override
	public boolean isEquivalentTo(Fluid fluid)
	{
		return fluid == SCContent.fakeLava || fluid == SCContent.flowingFakeLava;
	}

	@Override
	public int getLevelDecreasePerBlock(IWorldReader world)
	{
		return world.getDimension().doesWaterVaporize() ? 1 : 2;
	}

	@Override
	public boolean canOtherFlowInto(IFluidState state, Fluid fluid, Direction direction)
	{
		return state.getHeight() >= 0.44444445F && fluid.isIn(FluidTags.WATER);
	}

	@Override
	public int getTickRate(IWorldReader world)
	{
		return world.getDimension().isNether() ? 10 : 30;
	}

	@Override
	public int getTickRate(World world, IFluidState p_205578_2_, IFluidState p_205578_3_)
	{
		int i = this.getTickRate(world);

		if(!p_205578_2_.isEmpty() && !p_205578_3_.isEmpty() && !p_205578_2_.get(FALLING) && !p_205578_3_.get(FALLING) && p_205578_3_.getHeight() > p_205578_2_.getHeight() && world.getRandom().nextInt(4) != 0)
			i *= 4;

		return i;
	}

	protected void triggerEffects(IWorld p_205581_1_, BlockPos p_205581_2_)
	{
		double x = p_205581_2_.getX();
		double y = p_205581_2_.getY();
		double z = p_205581_2_.getZ();

		p_205581_1_.playSound((PlayerEntity)null, p_205581_2_, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (p_205581_1_.getRandom().nextFloat() - p_205581_1_.getRandom().nextFloat()) * 0.8F);

		for(int i = 0; i < 8; ++i)
		{
			p_205581_1_.addParticle(ParticleTypes.LARGE_SMOKE, x + Math.random(), y + 1.2D, z + Math.random(), 0.0D, 0.0D, 0.0D);
		}

	}

	@Override
	protected boolean canSourcesMultiply()
	{
		return false;
	}

	@Override
	protected void flowInto(IWorld world, BlockPos pos, BlockState blockState, Direction direction, IFluidState fluidState)
	{
		if(direction == Direction.DOWN)
		{
			IFluidState ifluidstate = world.getFluidState(pos);

			if(isIn(FluidTags.LAVA) && ifluidstate.isTagged(FluidTags.WATER))
			{
				if(blockState.getBlock() instanceof BlockFlowingFluid)
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
		protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder)
		{
			super.fillStateContainer(builder);
			builder.add(LEVEL_1_8);
		}

		@Override
		public int getLevel(IFluidState state)
		{
			return state.get(LEVEL_1_8);
		}

		@Override
		public boolean isSource(IFluidState state)
		{
			return false;
		}
	}

	public static class Source extends FakeLavaFluid
	{
		@Override
		public int getLevel(IFluidState state)
		{
			return 8;
		}

		@Override
		public boolean isSource(IFluidState state)
		{
			return true;
		}
	}
}