package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.compat.waila.ICustomWailaDisplay;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedMetals extends BlockOwnable implements ICustomWailaDisplay, IReinforcedBlock
{
	public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockReinforcedMetals.EnumType.class);

	public BlockReinforcedMetals()
	{
		super(Material.ROCK);
		setDefaultState(blockState.getBaseState().withProperty(VARIANT, BlockReinforcedMetals.EnumType.GOLD));
		setSoundType(SoundType.METAL);
	}

	/**
	 * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
	 * returns the metadata of the dropped item based on the old metadata of the block.
	 */
	@Override
	public int damageDropped(IBlockState state)
	{
		return ((BlockReinforcedMetals.EnumType)state.getValue(VARIANT)).getMetadata();
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
	{
		BlockReinforcedMetals.EnumType[] values = BlockReinforcedMetals.EnumType.values();

		for (BlockReinforcedMetals.EnumType type : values)
			list.add(new ItemStack(this, 1, type.getMetadata()));
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(VARIANT, BlockReinforcedMetals.EnumType.byMetadata(meta));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((BlockReinforcedMetals.EnumType)state.getValue(VARIANT)).getMetadata();
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {VARIANT});
	}

	@Override
	public boolean isBeaconBase(IBlockAccess world, BlockPos pos, BlockPos beacon)
	{
		return true;
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos)
	{
		return new ItemStack(Item.getItemFromBlock(SCContent.reinforcedMetals), 1, BlockUtils.getBlockMeta(world, pos));
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos)
	{
		return true;
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(new Block[] {
				Blocks.GOLD_BLOCK,
				Blocks.IRON_BLOCK,
				Blocks.DIAMOND_BLOCK,
				Blocks.EMERALD_BLOCK
		});
	}

	@Override
	public int getAmount()
	{
		return 4;
	}

	public static enum EnumType implements IStringSerializable
	{
		GOLD(0, "gold", "gold"),
		IRON(1, "iron", "iron"),
		DIAMOND(2, "diamond", "diamond"),
		EMERALD(3, "emerald", "emerald");
		private static final BlockReinforcedMetals.EnumType[] META_LOOKUP = new BlockReinforcedMetals.EnumType[values().length];
		private final int meta;
		private final String name;
		private final String unlocalizedName;

		private EnumType(int meta, String name, String unlocalizedName)
		{
			this.meta = meta;
			this.name = name;
			this.unlocalizedName = unlocalizedName;
		}

		public int getMetadata()
		{
			return meta;
		}

		@Override
		public String toString()
		{
			return name;
		}

		public static BlockReinforcedMetals.EnumType byMetadata(int meta)
		{
			if (meta < 0 || meta >= META_LOOKUP.length)
				meta = 0;

			return META_LOOKUP[meta];
		}

		@Override
		public String getName()
		{
			return name;
		}

		public String getTranslationKey()
		{
			return unlocalizedName;
		}

		static
		{
			BlockReinforcedMetals.EnumType[] values = values();

			for (BlockReinforcedMetals.EnumType type : values)
				META_LOOKUP[type.getMetadata()] = type;
		}
	}
}