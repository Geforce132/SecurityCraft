package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class ReinforcedOldLogBlock extends ReinforcedLogBlock implements IReinforcedBlock {
	public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create("variant", EnumType.class, type -> type.getMetadata() < 4);

	public ReinforcedOldLogBlock() {
		super(4, Blocks.LOG);
		setDefaultState(blockState.getBaseState().withProperty(VARIANT, EnumType.OAK).withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
	}

	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
		EnumType type = state.getValue(VARIANT);

		if (state.getValue(LOG_AXIS) == EnumAxis.Y)
			return type.getMapColor();

		switch (type) {
			case SPRUCE:
				return EnumType.DARK_OAK.getMapColor();
			case BIRCH:
				return MapColor.QUARTZ;
			case JUNGLE:
			default:
				return EnumType.SPRUCE.getMapColor();
		}
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

		switch (ReinforcedOldLogBlock.SwitchEnumAxis.AXIS_LOOKUP[state.getValue(LOG_AXIS).ordinal()]) {
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

	static final class SwitchEnumAxis {
		static final int[] AXIS_LOOKUP = new int[BlockLog.EnumAxis.values().length];

		static {
			try {
				AXIS_LOOKUP[BlockLog.EnumAxis.X.ordinal()] = 1;
			}
			catch (NoSuchFieldError e) {}

			try {
				AXIS_LOOKUP[BlockLog.EnumAxis.Z.ordinal()] = 2;
			}
			catch (NoSuchFieldError e) {}

			try {
				AXIS_LOOKUP[BlockLog.EnumAxis.NONE.ordinal()] = 3;
			}
			catch (NoSuchFieldError e) {}
		}

		private SwitchEnumAxis() {}
	}
}