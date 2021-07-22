package net.geforcemods.securitycraft.fluids;

import java.util.Random;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;

public abstract class FakeWaterFluid extends FlowingFluid
{
	@Override
	public Fluid getFlowing()
	{
		return SCContent.FLOWING_FAKE_WATER.get();
	}

	@Override
	public Fluid getSource()
	{
		return SCContent.FAKE_WATER.get();
	}

	@Override
	public Item getBucket()
	{
		return SCContent.FAKE_WATER_BUCKET.get();
	}

	@Override
	protected FluidAttributes createAttributes()
	{
		return FluidAttributes.Water.builder(
				new ResourceLocation("block/water_still"),
				new ResourceLocation("block/water_flow"))
				.overlay(new ResourceLocation("block/water_overlay"))
				.translationKey("block.minecraft.water")
				.color(0xFF3F76E4).build(this);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(World world, BlockPos pos, FluidState state, Random random)
	{
		if(!state.isSource() && !state.getValue(FALLING))
		{
			if(random.nextInt(64) == 0)
				world.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
		}
		else if(random.nextInt(10) == 0)
			world.addParticle(ParticleTypes.UNDERWATER, pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(), 0.0D, 0.0D, 0.0D);
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	@Override
	public IParticleData getDripParticle()
	{
		return ParticleTypes.DRIPPING_WATER;
	}

	@Override
	protected boolean canConvertToSource()
	{
		return true;
	}

	@Override
	protected void beforeDestroyingBlock(IWorld world, BlockPos pos, BlockState state)
	{
		TileEntity te = state.hasTileEntity() ? world.getBlockEntity(pos) : null;

		Block.dropResources(state, world, pos, te);
	}

	@Override
	public int getSlopeFindDistance(IWorldReader world)
	{
		return 4;
	}

	@Override
	public BlockState createLegacyBlock(FluidState state)
	{
		return SCContent.FAKE_WATER_BLOCK.get().defaultBlockState().setValue(FlowingFluidBlock.LEVEL, getLegacyLevel(state));
	}

	@Override
	public boolean isSame(Fluid fluid)
	{
		return fluid == SCContent.FAKE_WATER.get() || fluid == SCContent.FLOWING_FAKE_WATER.get();
	}

	@Override
	public int getDropOff(IWorldReader world)
	{
		return 1;
	}

	@Override
	public int getTickDelay(IWorldReader world)
	{
		return 5;
	}

	@Override
	public boolean canBeReplacedWith(FluidState fluidState, IBlockReader world, BlockPos pos, Fluid fluid, Direction dir)
	{
		return dir == Direction.DOWN && !fluid.is(FluidTags.WATER);
	}

	@Override
	protected float getExplosionResistance()
	{
		return 100.0F;
	}

	public static class Flowing extends FakeWaterFluid
	{
		@Override
		protected void createFluidStateDefinition(StateContainer.Builder<Fluid, FluidState> builder)
		{
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getAmount(FluidState p_207192_1_)
		{
			return p_207192_1_.getValue(LEVEL);
		}

		@Override
		public boolean isSource(FluidState state)
		{
			return false;
		}
	}

	public static class Source extends FakeWaterFluid
	{
		@Override
		public int getAmount(FluidState p_207192_1_)
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