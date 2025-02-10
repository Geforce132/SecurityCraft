package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReinforcedRedstoneLampBlock extends BaseReinforcedBlock {
	public static final PropertyBool LIT = PropertyBool.create("lit");

	public ReinforcedRedstoneLampBlock() {
		super(Blocks.REDSTONE_LAMP, Blocks.LIT_REDSTONE_LAMP);

		setDefaultState(getDefaultState().withProperty(LIT, false));
		setSoundType(SoundType.GLASS);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		if (!world.isRemote) {
			boolean isOn = state.getValue(LIT);

			if (isOn && !world.isBlockPowered(pos))
				world.setBlockState(pos, getDefaultState().withProperty(LIT, false), 2);
			else if (!isOn && world.isBlockPowered(pos))
				world.setBlockState(pos, getDefaultState().withProperty(LIT, true), 2);
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (!world.isRemote) {
			boolean isOn = state.getValue(LIT);

			if (isOn && !world.isBlockPowered(pos))
				world.scheduleUpdate(pos, this, 4);
			else if (!isOn && world.isBlockPowered(pos))
				world.setBlockState(pos, getDefaultState().withProperty(LIT, true), 2);
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (!world.isRemote && state.getValue(LIT) && !world.isBlockPowered(pos))
			world.setBlockState(pos, getDefaultState().withProperty(LIT, false), 2);
	}

	@Override
	public int getLightValue(IBlockState state) {
		return state.getValue(LIT) ? 15 : 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(LIT, meta == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(LIT) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, LIT);
	}

	@Override
	public IBlockState convertToReinforcedState(IBlockState state) {
		Block block = state.getBlock();

		if (block == Blocks.REDSTONE_LAMP)
			return getDefaultState().withProperty(LIT, false);
		else if (block == Blocks.LIT_REDSTONE_LAMP)
			return getDefaultState().withProperty(LIT, true);
		else
			return state;
	}

	@Override
	public IBlockState convertToVanillaState(IBlockState state) {
		if (state.getValue(LIT))
			return Blocks.LIT_REDSTONE_LAMP.getDefaultState();
		else
			return Blocks.REDSTONE_LAMP.getDefaultState();
	}

	@Override
	public ItemStack convertToReinforcedStack(ItemStack stackToConvert, Block blockToConvert) {
		if (blockToConvert == Blocks.REDSTONE_LAMP)
			return new ItemStack(this);
		else
			return ItemStack.EMPTY;
	}

	@Override
	public ItemStack convertToVanillaStack(ItemStack stackToConvert) {
		return new ItemStack(Blocks.REDSTONE_LAMP);
	}
}
