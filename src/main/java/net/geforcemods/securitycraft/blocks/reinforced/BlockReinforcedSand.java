package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.BlockSand;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class BlockReinforcedSand extends BlockReinforcedFalling
{
	public static final PropertyEnum<BlockSand.EnumType> VARIANT = PropertyEnum.<BlockSand.EnumType>create("variant", BlockSand.EnumType.class);

	public BlockReinforcedSand()
	{
		super(Material.SAND, Blocks.SAND);

		setSoundType(SoundType.SAND);
		setDefaultState(blockState.getBaseState().withProperty(VARIANT, BlockSand.EnumType.SAND));
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		for(BlockSand.EnumType type : BlockSand.EnumType.values())
		{
			items.add(new ItemStack(this, 1, type.getMetadata()));
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(VARIANT, BlockSand.EnumType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, VARIANT);
	}

	@Override
	public int getAmount()
	{
		return 2;
	}
}
