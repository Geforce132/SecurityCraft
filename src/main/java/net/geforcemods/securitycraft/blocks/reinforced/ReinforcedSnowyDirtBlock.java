package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SnowyDirtBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FlowersFeature;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.PlantType;

public class ReinforcedSnowyDirtBlock extends SnowyDirtBlock implements IReinforcedBlock, IGrowable
{
	private Block vanillaBlock;

	public ReinforcedSnowyDirtBlock(Block.Properties properties, Block vB)
	{
		super(properties);
		this.vanillaBlock = vB;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		if(facing != Direction.UP)
			return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
		else
		{
			Block block = facingState.getBlock();
			return state.setValue(SNOWY, block == Blocks.SNOW_BLOCK || block == Blocks.SNOW || block == SCContent.REINFORCED_SNOW_BLOCK.get());
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		Block block = ctx.getLevel().getBlockState(ctx.getClickedPos().above()).getBlock();
		return defaultBlockState().setValue(SNOWY, block == Blocks.SNOW_BLOCK || block == Blocks.SNOW || block == SCContent.REINFORCED_SNOW_BLOCK.get());
	}

	@Override
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand)
	{
		if(this == SCContent.REINFORCED_MYCELIUM.get())
		{
			super.animateTick(state, world, pos, rand);

			if(rand.nextInt(10) == 0)
				world.addParticle(ParticleTypes.MYCELIUM, (double) pos.getX() + (double) rand.nextFloat(), pos.getY() + 1.1D, (double) pos.getZ() + (double) rand.nextFloat(), 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable)
	{
		PlantType type = plantable.getPlantType(world, pos.relative(facing));

		if(type == PlantType.CAVE)
			return state.isFaceSturdy(world, pos, Direction.UP);
		else if(type == PlantType.PLAINS)
			return true;
		else if(type == PlantType.BEACH)
		{
			boolean isBeach = is(SCTags.Blocks.REINFORCED_DIRT) || this == SCContent.REINFORCED_SAND.get() || this == SCContent.REINFORCED_RED_SAND.get();
			boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER ||
					world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
					world.getBlockState(pos.north()).getMaterial() == Material.WATER ||
					world.getBlockState(pos.south()).getMaterial() == Material.WATER);
			return isBeach && hasWater;
		}

		return false;
	}

	@Override
	public boolean isValidBonemealTarget(IBlockReader world, BlockPos pos, BlockState state, boolean isClient)
	{
		return this == SCContent.REINFORCED_GRASS_BLOCK.get() && world.getBlockState(pos.above()).isAir(world, pos.above());
	}

	@Override
	public boolean isBonemealSuccess(World world, Random rand, BlockPos pos, BlockState state)
	{
		return this == SCContent.REINFORCED_GRASS_BLOCK.get();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void performBonemeal(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
		BlockPos posAbove = pos.above();
		BlockState grass = Blocks.GRASS.defaultBlockState();

		for(int i = 0; i < 128; ++i)
		{
			BlockPos tempPos = posAbove;
			int j = 0;

			while(true)
			{
				if(j >= i / 16)
				{
					BlockState tempState = world.getBlockState(tempPos);

					if(tempState.getBlock() == grass.getBlock() && rand.nextInt(10) == 0)
						((IGrowable)grass.getBlock()).performBonemeal(world, rand, tempPos, tempState);

					if(!tempState.isAir(world, tempPos))
						break;

					BlockState placeState;

					if(rand.nextInt(8) == 0)
					{
						List<ConfiguredFeature<?, ?>> flowers = world.getBiome(tempPos).getGenerationSettings().getFlowerFeatures();

						if(flowers.isEmpty())
							break;

						ConfiguredFeature<?, ?> configuredfeature = flowers.get(0);
						FlowersFeature flowersfeature = (FlowersFeature)configuredfeature.feature;

						placeState = flowersfeature.getRandomFlower(rand, tempPos, configuredfeature.config());
					}
					else
						placeState = grass;

					if(placeState.canSurvive(world, tempPos))
						world.setBlock(tempPos, placeState, 3);

					break;
				}

				tempPos = tempPos.offset(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

				if(world.getBlockState(tempPos.below()).getBlock() != this || world.getBlockState(tempPos).isCollisionShapeFullBlock(world, tempPos))
					break;

				++j;
			}
		}
	}

	@Override
	public Block getVanillaBlock()
	{
		return vanillaBlock;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return defaultBlockState().setValue(SNOWY, vanillaState.getValue(SNOWY));
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new OwnableTileEntity();
	}
}

