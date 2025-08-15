package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @deprecated Use {@link AlarmBlock} and its LIT property
 */
@Deprecated
public class OldLitAlarmBlock extends OwnableBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing");

	public OldLitAlarmBlock(Material material) {
		super(material);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		setLightLevel(1.0F);
		destroyTimeForOwner = 3.5F;
		setHarvestLevel("pickaxe", 0);
		blockMapColor = MapColor.RED;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
		return new ItemStack(Item.getItemFromBlock(SCContent.alarm));
	}

	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune) {
		return Item.getItemFromBlock(SCContent.alarm);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing;

		switch (meta & 7) {
			case 0:
				facing = EnumFacing.DOWN;
				break;
			case 1:
				facing = EnumFacing.EAST;
				break;
			case 2:
				facing = EnumFacing.WEST;
				break;
			case 3:
				facing = EnumFacing.SOUTH;
				break;
			case 4:
				facing = EnumFacing.NORTH;
				break;
			case 5:
			default:
				facing = EnumFacing.UP;
		}

		return getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta;

		switch (OldLitAlarmBlock.SwitchEnumFacing.FACING_LOOKUP[state.getValue(FACING).ordinal()]) {
			case 1:
				meta = 1;
				break;
			case 2:
				meta = 2;
				break;
			case 3:
				meta = 3;
				break;
			case 4:
				meta = 4;
				break;
			case 6:
				meta = 0;
				break;
			case 5:
			default:
				meta = 5;
		}

		return meta;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new AlarmBlockEntity();
	}

	static final class SwitchEnumFacing {
		static final int[] FACING_LOOKUP = new int[EnumFacing.values().length];

		static {
			try {
				FACING_LOOKUP[EnumFacing.EAST.ordinal()] = 1;
			}
			catch (NoSuchFieldError e) {}

			try {
				FACING_LOOKUP[EnumFacing.WEST.ordinal()] = 2;
			}
			catch (NoSuchFieldError e) {}

			try {
				FACING_LOOKUP[EnumFacing.SOUTH.ordinal()] = 3;
			}
			catch (NoSuchFieldError e) {}

			try {
				FACING_LOOKUP[EnumFacing.NORTH.ordinal()] = 4;
			}
			catch (NoSuchFieldError e) {}

			try {
				FACING_LOOKUP[EnumFacing.UP.ordinal()] = 5;
			}
			catch (NoSuchFieldError e) {}

			try {
				FACING_LOOKUP[EnumFacing.DOWN.ordinal()] = 6;
			}
			catch (NoSuchFieldError e) {}
		}

		private SwitchEnumFacing() {}
	}
}