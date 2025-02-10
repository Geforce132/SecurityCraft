package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

public class BaseReinforcedBlock extends OwnableBlock implements IReinforcedBlock {
	private List<Block> vanillaBlocks;

	public BaseReinforcedBlock(Block... vB) {
		super(vB[0].getMaterial(vB[0].getDefaultState()));
		vanillaBlocks = Arrays.asList(vB);
	}

	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
		return convertToVanillaState(state).getBlockHardness(world, pos);
	}

	@Override
	public Material getMaterial(IBlockState state) {
		return convertToVanillaState(state).getMaterial();
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().getSoundType(vanillaState, world, pos, entity);
	}

	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess level, BlockPos pos) {
		return convertToVanillaState(state).getMapColor(level, pos);
	}

	@Override
	public String getHarvestTool(IBlockState state) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().getHarvestTool(vanillaState);
	}

	@Override
	public boolean isToolEffective(String type, IBlockState state) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().isToolEffective(type, vanillaState);
	}

	@Override
	public int getHarvestLevel(IBlockState state) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().getHarvestLevel(vanillaState);
	}

	@Override
	public boolean isTranslucent(IBlockState state) {
		return convertToVanillaState(state).isTranslucent();
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
		IBlockState plant = plantable.getPlant(world, pos.offset(direction));
		EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));

		if (super.canSustainPlant(state, world, pos, direction, plantable))
			return true;

		if (plant.getBlock() == Blocks.CACTUS)
			return this == SCContent.reinforcedSand;

		if (plantable instanceof BlockBush) { //a nasty workaround because BaseReinforcedBlock can't use BlockBush#canSustainBush because it is protected
			boolean condition = false;

			if (plantable instanceof BlockLilyPad)
				condition = state.getBlock() == SCContent.fakeWater || state.getBlock() == SCContent.reinforcedIce;
			else if (plantable instanceof BlockDeadBush)
				condition = state.getBlock() == SCContent.reinforcedSand || state.getBlock() == SCContent.reinforcedHardenedClay || state.getBlock() == SCContent.reinforcedStainedHardenedClay || state.getBlock() == SCContent.reinforcedDirt;
			if (plantable instanceof BlockNetherWart)
				condition = state.getBlock() == SCContent.reinforcedSoulSand;

			if (condition)
				return true;
		}

		switch (plantType) {
			case Desert:
				return state.getBlock() == SCContent.reinforcedSand || state.getBlock() == SCContent.reinforcedHardenedClay || state.getBlock() == SCContent.reinforcedStainedHardenedClay;
			case Nether:
				return state.getBlock() == SCContent.reinforcedSoulSand;
			case Plains:
				return state.getBlock() == SCContent.reinforcedGrass || state.getBlock() == SCContent.reinforcedDirt;
			case Beach:
				boolean isBeach = state.getBlock() == SCContent.reinforcedGrass || state.getBlock() == SCContent.reinforcedDirt || state.getBlock() == SCContent.reinforcedSand;
				boolean hasWater = world.getBlockState(pos.east()).getMaterial() == Material.WATER || world.getBlockState(pos.west()).getMaterial() == Material.WATER || world.getBlockState(pos.north()).getMaterial() == Material.WATER || world.getBlockState(pos.south()).getMaterial() == Material.WATER;

				return isBeach && hasWater;
			default:
				return false;
		}
	}

	@Override
	public EnumPushReaction getPushReaction(IBlockState state) {
		EnumPushReaction originalPushReaction = super.getPushReaction(state);

		return originalPushReaction == EnumPushReaction.DESTROY ? EnumPushReaction.NORMAL : originalPushReaction;
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return vanillaBlocks;
	}
}
