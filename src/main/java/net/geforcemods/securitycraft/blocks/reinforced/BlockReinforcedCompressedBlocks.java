package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedCompressedBlocks extends BlockOwnable implements IReinforcedBlock
{
	public static final PropertyEnum<BlockReinforcedCompressedBlocks.EnumType> VARIANT = PropertyEnum.create("variant", BlockReinforcedCompressedBlocks.EnumType.class);

	public BlockReinforcedCompressedBlocks()
	{
		super(Material.rock, true);
		setDefaultState(blockState.getBaseState().withProperty(VARIANT, BlockReinforcedCompressedBlocks.EnumType.LAPIS));
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, BlockPos pos, Entity entity)
	{
		return !(entity instanceof EntityWither);
	}

	/**
	 * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
	 * returns the metadata of the dropped item based on the old metadata of the block.
	 */
	@Override
	public int damageDropped(IBlockState state)
	{
		return state.getValue(VARIANT).getMetadata();
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		BlockReinforcedCompressedBlocks.EnumType[] values = BlockReinforcedCompressedBlocks.EnumType.values();

		for (BlockReinforcedCompressedBlocks.EnumType type : values)
			list.add(new ItemStack(item, 1, type.getMetadata()));
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(VARIANT, BlockReinforcedCompressedBlocks.EnumType.byMetadata(meta));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {VARIANT});
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(new Block[]{
				Blocks.lapis_block,
				Blocks.coal_block
		});
	}

	@Override
	public int getAmount()
	{
		return 2;
	}

	public static enum EnumType implements IStringSerializable
	{
		LAPIS(0, "lapis", "lapis"),
		COAL(1, "coal", "coal");

		private static final BlockReinforcedCompressedBlocks.EnumType[] META_LOOKUP = new BlockReinforcedCompressedBlocks.EnumType[values().length];
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

		public static BlockReinforcedCompressedBlocks.EnumType byMetadata(int meta)
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

		public String getUnlocalizedName()
		{
			return unlocalizedName;
		}

		static
		{
			for(BlockReinforcedCompressedBlocks.EnumType type : values())
				META_LOOKUP[type.getMetadata()] = type;
		}
	}
}