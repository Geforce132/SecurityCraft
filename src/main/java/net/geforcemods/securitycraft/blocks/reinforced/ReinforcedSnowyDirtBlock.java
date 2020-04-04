package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.FlowersFeature;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.PlantType;

import java.util.List;
import java.util.Random;

public class ReinforcedSnowyDirtBlock extends SnowyDirtBlock implements IReinforcedBlock, IGrowable
{
	private Block BLOCK;

	public ReinforcedSnowyDirtBlock(Material mat, SoundType soundType, Block vB)
	{
		super(Block.Properties.create(mat).sound(soundType).hardnessAndResistance(-1.0F, 6000000.0F));
		this.BLOCK = vB;
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (facing != Direction.UP) {
			return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		} else {
			Block block = facingState.getBlock();
			return stateIn.with(SNOWY, Boolean.valueOf(block == Blocks.SNOW_BLOCK || block == Blocks.SNOW || block == SCContent.REINFORCED_SNOW_BLOCK.get()));
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Block block = context.getWorld().getBlockState(context.getPos().up()).getBlock();
		return this.getDefaultState().with(SNOWY, Boolean.valueOf(block == Blocks.SNOW_BLOCK || block == Blocks.SNOW || block == SCContent.REINFORCED_SNOW_BLOCK.get()));
	}

	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (this == SCContent.REINFORCED_MYCELIUM.get()) {
			super.animateTick(state, world, pos, rand);
			if (rand.nextInt(10) == 0) {
				world.addParticle(ParticleTypes.MYCELIUM, (double) pos.getX() + (double) rand.nextFloat(), pos.getY() + 1.1D, (double) pos.getZ() + (double) rand.nextFloat(), 0.0D, 0.0D, 0.0D);
			}
		}

	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable)
	{
		PlantType type = plantable.getPlantType(world, pos.offset(facing));

		switch (type) {
			case Cave:   return Block.hasSolidSide(state, world, pos, Direction.UP);
			case Plains: return true;
			case Beach:
				boolean isBeach = SCTags.Blocks.REINFORCED_DIRT.contains(this) || this.getBlock() == SCContent.REINFORCED_SAND.get() || this.getBlock() == SCContent.REINFORCED_RED_SAND.get();
				boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER ||
						world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
						world.getBlockState(pos.north()).getMaterial() == Material.WATER ||
						world.getBlockState(pos.south()).getMaterial() == Material.WATER);
				return isBeach && hasWater;
			default: break;
		}
		return false;
	}

	@Override
	public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return this == SCContent.REINFORCED_GRASS_BLOCK.get() && worldIn.getBlockState(pos.up()).isAir();
	}

	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return this == SCContent.REINFORCED_GRASS_BLOCK.get();
	}

	public void grow(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
		BlockPos blockpos = p_225535_3_.up();
		BlockState blockstate = Blocks.GRASS.getDefaultState();

		for(int i = 0; i < 128; ++i) {
			BlockPos blockpos1 = blockpos;
			int j = 0;

			while(true) {
				if (j >= i / 16) {
					BlockState blockstate2 = p_225535_1_.getBlockState(blockpos1);
					if (blockstate2.getBlock() == blockstate.getBlock() && p_225535_2_.nextInt(10) == 0) {
						((IGrowable)blockstate.getBlock()).grow(p_225535_1_, p_225535_2_, blockpos1, blockstate2);
					}

					if (!blockstate2.isAir()) {
						break;
					}

					BlockState blockstate1;
					if (p_225535_2_.nextInt(8) == 0) {
						List<ConfiguredFeature<?, ?>> list = p_225535_1_.getBiome(blockpos1).getFlowers();
						if (list.isEmpty()) {
							break;
						}

						ConfiguredFeature<?, ?> configuredfeature = ((DecoratedFeatureConfig)(list.get(0)).config).feature;
						blockstate1 = ((FlowersFeature)configuredfeature.feature).getFlowerToPlace(p_225535_2_, blockpos1, configuredfeature.config);
					} else {
						blockstate1 = blockstate;
					}

					if (blockstate1.isValidPosition(p_225535_1_, blockpos1)) {
						p_225535_1_.setBlockState(blockpos1, blockstate1, 3);
					}
					break;
				}

				blockpos1 = blockpos1.add(p_225535_2_.nextInt(3) - 1, (p_225535_2_.nextInt(3) - 1) * p_225535_2_.nextInt(3) / 2, p_225535_2_.nextInt(3) - 1);
				if (p_225535_1_.getBlockState(blockpos1.down()).getBlock() != this || p_225535_1_.getBlockState(blockpos1).isCollisionShapeOpaque(p_225535_1_, blockpos1)) {
					break;
				}

				++j;
			}
		}

	}

	@Override
	public Block getVanillaBlock()
	{
		return BLOCK;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState().with(SNOWY, vanillaState.get(SNOWY));
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		return NonNullList.from(ItemStack.EMPTY, new ItemStack(this));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
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

