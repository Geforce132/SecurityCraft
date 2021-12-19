package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class BlockReinforcedOldLog extends BlockReinforcedLog implements IReinforcedBlock {
	public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create("variant", EnumType.class, type -> type.getMetadata() < 4);

	public BlockReinforcedOldLog() {
		setDefaultState(blockState.getBaseState().withProperty(VARIANT, EnumType.OAK).withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, EnumType.OAK.getMetadata()));
		list.add(new ItemStack(this, 1, EnumType.SPRUCE.getMetadata()));
		list.add(new ItemStack(this, 1, EnumType.BIRCH.getMetadata()));
		list.add(new ItemStack(this, 1, EnumType.JUNGLE.getMetadata()));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState state = getDefaultState().withProperty(VARIANT, EnumType.byMetadata((meta & 3) % 4));

		switch (meta & 12) {
			case 0:
				state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
				break;
			case 4:
				state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
				break;
			case 8:
				state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
				break;
			default:
				state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
		}

		return state;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		byte b0 = 0;
		int meta = b0 | state.getValue(VARIANT).getMetadata();

		switch (BlockReinforcedOldLog.SwitchEnumAxis.AXIS_LOOKUP[state.getValue(LOG_AXIS).ordinal()]) {
			case 1:
				meta |= 4;
				break;
			case 2:
				meta |= 8;
				break;
			case 3:
				meta |= 12;
		}

		return meta;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT, LOG_AXIS);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(Blocks.LOG);
	}

	@Override
	public int getAmount() {
		return 4;
	}

	static final class SwitchEnumAxis {
		static final int[] AXIS_LOOKUP = new int[BlockLog.EnumAxis.values().length];

		static {
			try {
				AXIS_LOOKUP[BlockLog.EnumAxis.X.ordinal()] = 1;
			}
			catch (NoSuchFieldError e) {
			}

			try {
				AXIS_LOOKUP[BlockLog.EnumAxis.Z.ordinal()] = 2;
			}
			catch (NoSuchFieldError e) {
			}

			try {
				AXIS_LOOKUP[BlockLog.EnumAxis.NONE.ordinal()] = 3;
			}
			catch (NoSuchFieldError e) {
			}
		}
	}
}