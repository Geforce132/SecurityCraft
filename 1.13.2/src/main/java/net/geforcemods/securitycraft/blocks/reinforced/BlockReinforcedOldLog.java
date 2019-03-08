package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

//TODO: delete and break up into seperate blocks instantiated with BlockReinforcedBase
public class BlockReinforcedOldLog extends BlockReinforcedLog implements IReinforcedBlock
{
	public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class, new Predicate<BlockPlanks.EnumType>()
	{
		@Override
		public boolean apply(BlockPlanks.EnumType type)
		{
			return type.getMetadata() < 4;
		}
	});

	public BlockReinforcedOldLog()
	{
		setDefaultState(blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.OAK).withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
	{
		list.add(new ItemStack(this, 1, BlockPlanks.EnumType.OAK.getMetadata()));
		list.add(new ItemStack(this, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()));
		list.add(new ItemStack(this, 1, BlockPlanks.EnumType.BIRCH.getMetadata()));
		list.add(new ItemStack(this, 1, BlockPlanks.EnumType.JUNGLE.getMetadata()));
	}

	/**
	 * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
	 * returns the metadata of the dropped item based on the old metadata of the block.
	 */
	@Override
	public int damageDropped(IBlockState state)
	{
		return ((BlockPlanks.EnumType)state.getValue(VARIANT)).getMetadata();
	}

	@Override
	public Block getVanillaBlock()
	{
		return Arrays.asList(new Block[] {
				Blocks.LOG
		});
	}

	static final class SwitchEnumAxis
	{
		static final int[] AXIS_LOOKUP = new int[BlockLog.EnumAxis.values().length];

		static
		{
			try
			{
				AXIS_LOOKUP[BlockLog.EnumAxis.X.ordinal()] = 1;
			}
			catch (NoSuchFieldError e)
			{
				;
			}

			try
			{
				AXIS_LOOKUP[BlockLog.EnumAxis.Z.ordinal()] = 2;
			}
			catch (NoSuchFieldError e)
			{
				;
			}

			try
			{
				AXIS_LOOKUP[BlockLog.EnumAxis.NONE.ordinal()] = 3;
			}
			catch (NoSuchFieldError e)
			{
				;
			}
		}
	}
}