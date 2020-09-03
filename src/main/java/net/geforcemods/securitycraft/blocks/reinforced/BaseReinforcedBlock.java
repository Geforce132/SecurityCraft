package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.BushBlock;
import net.minecraft.block.FungusBlock;
import net.minecraft.block.NetherRootsBlock;
import net.minecraft.block.NetherSproutsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class BaseReinforcedBlock extends OwnableBlock implements IReinforcedBlock
{
	private final Supplier<Block> vanillaBlockSupplier;

	public BaseReinforcedBlock(Block.Properties properties, Block vB)
	{
		this(properties, () -> vB);
	}

	public BaseReinforcedBlock(Block.Properties properties, Supplier<Block> vB)
	{
		super(properties);

		vanillaBlockSupplier = vB;
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable)
	{
		BlockState plant = plantable.getPlant(world, pos.offset(facing));
		PlantType type = plantable.getPlantType(world, pos.offset(facing));

		if(plant.getBlock() == Blocks.CACTUS)
			return this == SCContent.REINFORCED_SAND.get() || this == SCContent.REINFORCED_RED_SAND.get();

		if (plantable instanceof BushBlock) //a workaround because BaseReinforcedBlock can't use isValidGround because it is protected
		{
			boolean bushCondition = state.isIn(SCContent.REINFORCED_GRASS_BLOCK.get()) || state.isIn(SCContent.REINFORCED_DIRT.get()) || state.isIn(SCContent.REINFORCED_COARSE_DIRT.get()) || state.isIn(SCContent.REINFORCED_PODZOL.get());

			if (plantable instanceof NetherSproutsBlock || plantable instanceof NetherRootsBlock || plantable instanceof FungusBlock)
				return state.isIn(SCTags.Blocks.REINFORCED_NYLIUM) || state.isIn(SCContent.REINFORCED_SOUL_SOIL.get()) || bushCondition;
		}

		if(type == PlantType.DESERT)
			return this == SCContent.REINFORCED_SAND.get() || this == SCContent.REINFORCED_RED_SAND.get();
		else if(type == PlantType.CAVE)
			return state.isSolidSide(world, pos, Direction.UP);
		else if(type == PlantType.PLAINS)
			return isIn(SCTags.Blocks.REINFORCED_DIRT);
		else if(type == PlantType.BEACH)
		{
			boolean isBeach = isIn(SCTags.Blocks.REINFORCED_DIRT) || this == SCContent.REINFORCED_SAND.get() || this == SCContent.REINFORCED_RED_SAND.get();
			boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER ||
					world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
					world.getBlockState(pos.north()).getMaterial() == Material.WATER ||
					world.getBlockState(pos.south()).getMaterial() == Material.WATER);
			return isBeach && hasWater;
		}
		return false;
	}

	@Override
	public boolean isConduitFrame(BlockState state, IWorldReader world, BlockPos pos, BlockPos conduit)
	{
		return this == SCContent.REINFORCED_PRISMARINE.get() || this == SCContent.REINFORCED_PRISMARINE_BRICKS.get() || this == SCContent.REINFORCED_SEA_LANTERN.get() || this == SCContent.REINFORCED_DARK_PRISMARINE.get();
	}

	@Override
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (this.getVanillaBlock() instanceof BreakableBlock)
			return adjacentBlockState.getBlock() == this ? true : super.isSideInvisible(state, adjacentBlockState, side);
		return false;
	}

	@Override
	public Block getVanillaBlock()
	{
		return vanillaBlockSupplier.get();
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState();
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder)
	{
		return NonNullList.from(ItemStack.EMPTY, new ItemStack(this));
	}
}
