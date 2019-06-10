package net.geforcemods.securitycraft.fluids;

import java.util.Random;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.ParticleTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.ParticleTypes.IParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class FakeWaterFluid extends FlowingFluid
{
	@Override
	public Fluid getFlowingFluid()
	{
		return SCContent.flowingFakeWater;
	}

	@Override
	public Fluid getStillFluid()
	{
		return SCContent.fakeWater;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public Item getFilledBucket()
	{
		return SCContent.fWaterBucket;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(World world, BlockPos pos, IFluidState state, Random random)
	{
		if(!state.isSource() && !state.get(FALLING))
		{
			if(random.nextInt(64) == 0)
				world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
		}
		else if(random.nextInt(10) == 0)
			world.addParticle(ParticleTypes.UNDERWATER, pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(), 0.0D, 0.0D, 0.0D);
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	@Override
	public IParticleData getDripParticleData()
	{
		return ParticleTypes.DRIPPING_WATER;
	}

	@Override
	protected boolean canSourcesMultiply()
	{
		return true;
	}

	@Override
	protected void beforeReplacingBlock(IWorld world, BlockPos pos, BlockState state)
	{
		state.dropBlockAsItem(world.getWorld(), pos, 0);
	}

	@Override
	public int getSlopeFindDistance(IWorldReader world)
	{
		return 4;
	}

	@Override
	public BlockState getBlockState(IFluidState state)
	{
		return SCContent.fakeWaterBlock.getDefaultState().with(BlockFlowingFluid.LEVEL, getLevelFromState(state));
	}

	@Override
	public boolean isEquivalentTo(Fluid fluid)
	{
		return fluid == SCContent.fakeWater || fluid == SCContent.flowingFakeWater;
	}

	@Override
	public int getLevelDecreasePerBlock(IWorldReader world)
	{
		return 1;
	}

	@Override
	public int getTickRate(IWorldReader world)
	{
		return 5;
	}

	@Override
	public boolean canOtherFlowInto(IFluidState state, Fluid fluid, Direction direction)
	{
		return direction == Direction.DOWN && !fluid.isIn(FluidTags.WATER);
	}

	@Override
	protected float getExplosionResistance()
	{
		return 100.0F;
	}

	public static class Flowing extends FakeWaterFluid
	{
		@Override
		protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder)
		{
			super.fillStateContainer(builder);
			builder.add(LEVEL_1_8);
		}

		@Override
		public int getLevel(IFluidState p_207192_1_)
		{
			return p_207192_1_.get(LEVEL_1_8);
		}

		@Override
		public boolean isSource(IFluidState state)
		{
			return false;
		}
	}

	public static class Source extends FakeWaterFluid
	{
		@Override
		public int getLevel(IFluidState p_207192_1_)
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