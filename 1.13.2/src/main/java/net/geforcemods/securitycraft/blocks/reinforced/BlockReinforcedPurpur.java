package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//TODO: delete and break up into seperate blocks instantiated with BlockReinforcedBase/BlockReinforcedRotatedPillar
public class BlockReinforcedPurpur extends BlockOwnable implements IOverlayDisplay, IReinforcedBlock
{
	public static final PropertyEnum<BlockReinforcedPurpur.EnumType> VARIANT = PropertyEnum.<BlockReinforcedPurpur.EnumType>create("variant", BlockReinforcedPurpur.EnumType.class);

	public BlockReinforcedPurpur()
	{
		super(Material.ROCK);
		setDefaultState(blockState.getBaseState().withProperty(VARIANT, BlockReinforcedPurpur.EnumType.DEFAULT));
	}

	/**
	 * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
	 * IBlockstate
	 */
	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ(), ctx.getPlayer());
	}

	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EntityPlayer placer)
	{
		if (meta == BlockReinforcedPurpur.EnumType.LINES_Y.getMetadata())
			switch (facing.getAxis())
			{
				case Z:
					return getDefaultState().withProperty(VARIANT, BlockReinforcedPurpur.EnumType.LINES_Z);
				case X:
					return getDefaultState().withProperty(VARIANT, BlockReinforcedPurpur.EnumType.LINES_X);
				case Y:
					return getDefaultState().withProperty(VARIANT, BlockReinforcedPurpur.EnumType.LINES_Y);
			}

		return getDefaultState().withProperty(VARIANT, BlockReinforcedPurpur.EnumType.DEFAULT);
	}

	/**
	 * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
	 * returns the metadata of the dropped item based on the old metadata of the block.
	 */
	@Override
	public int damageDropped(IBlockState state)
	{
		BlockReinforcedPurpur.EnumType type = state.getValue(VARIANT);
		return type != BlockReinforcedPurpur.EnumType.LINES_X && type != BlockReinforcedPurpur.EnumType.LINES_Z ? type.getMetadata() : BlockReinforcedPurpur.EnumType.LINES_Y.getMetadata();
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
	{
		list.add(new ItemStack(this, 1, BlockReinforcedPurpur.EnumType.DEFAULT.getMetadata()));
		list.add(new ItemStack(this, 1, BlockReinforcedPurpur.EnumType.LINES_Y.getMetadata()));
	}

	/**
	 * Get the MapColor for this Block and the given BlockState
	 */
	public MapColor getMapColor(IBlockState state)
	{
		return MapColor.QUARTZ;
	}

	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 */
	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		switch (rot)
		{
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:

				switch (state.getValue(VARIANT))
				{
					case LINES_X:
						return state.withProperty(VARIANT, BlockReinforcedPurpur.EnumType.LINES_Z);
					case LINES_Z:
						return state.withProperty(VARIANT, BlockReinforcedPurpur.EnumType.LINES_X);
					default:
						return state;
				}

			default:
				return state;
		}
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
	{
		IBlockState state = world.getBlockState(pos);
		for (IProperty prop : state.getProperties().keySet())
			if (prop.getName().equals("variant") && prop.getValueClass() == EnumType.class)
			{
				EnumType current = (EnumType)state.getValue(prop);
				EnumType next = current == EnumType.LINES_X ? EnumType.LINES_Y :
					current == EnumType.LINES_Y ? EnumType.LINES_Z :
						current == EnumType.LINES_Z ? EnumType.LINES_X : current;
				if (next == current)
					return false;
				world.setBlockState(pos, state.withProperty(prop, next));
				return true;
			}
		return false;
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos)
	{
		return new ItemStack(Item.getItemFromBlock(SCContent.reinforcedPurpur), 1, BlockUtils.getBlockMeta(world, pos) != 0 ? 1 : 0);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos)
	{
		return true;
	}

	@Override
	public Block getVanillaBlock()
	{
		return Arrays.asList(new Block[] {
				Blocks.PURPUR_BLOCK,
				Blocks.PURPUR_PILLAR
		});
	}

	public static enum EnumType implements IStringSerializable
	{
		DEFAULT(0, "default", "default"),
		LINES_Y(1, "lines_y", "lines"),
		LINES_X(2, "lines_x", "lines"),
		LINES_Z(3, "lines_z", "lines");

		private static final BlockReinforcedPurpur.EnumType[] META_LOOKUP = new BlockReinforcedPurpur.EnumType[values().length];
		private final int meta;
		private final String serializedName;
		private final String unlocalizedName;

		private EnumType(int meta, String name, String unlocalizedName)
		{
			this.meta = meta;
			serializedName = name;
			this.unlocalizedName = unlocalizedName;
		}

		public int getMetadata()
		{
			return meta;
		}

		@Override
		public String toString()
		{
			return unlocalizedName;
		}

		public static BlockReinforcedPurpur.EnumType byMetadata(int meta)
		{
			if (meta < 0 || meta >= META_LOOKUP.length)
				meta = 0;

			return META_LOOKUP[meta];
		}

		@Override
		public String getName()
		{
			return serializedName;
		}

		static
		{
			for (BlockReinforcedPurpur.EnumType type : values())
				META_LOOKUP[type.getMetadata()] = type;
		}
	}
}