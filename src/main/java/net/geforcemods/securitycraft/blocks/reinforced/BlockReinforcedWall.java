package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedWall extends BlockReinforcedBase
{
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.<EnumType>create("variant", EnumType.class);
	protected static final AxisAlignedBB[] AABB_BY_INDEX = {new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.3125D, 0.0D, 0.0D, 0.6875D, 0.875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.3125D, 1.0D, 0.875D, 0.6875D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};
	protected static final AxisAlignedBB[] CLIP_AABB_BY_INDEX = {AABB_BY_INDEX[0].setMaxY(1.5D), AABB_BY_INDEX[1].setMaxY(1.5D), AABB_BY_INDEX[2].setMaxY(1.5D), AABB_BY_INDEX[3].setMaxY(1.5D), AABB_BY_INDEX[4].setMaxY(1.5D), AABB_BY_INDEX[5].setMaxY(1.5D), AABB_BY_INDEX[6].setMaxY(1.5D), AABB_BY_INDEX[7].setMaxY(1.5D), AABB_BY_INDEX[8].setMaxY(1.5D), AABB_BY_INDEX[9].setMaxY(1.5D), AABB_BY_INDEX[10].setMaxY(1.5D), AABB_BY_INDEX[11].setMaxY(1.5D), AABB_BY_INDEX[12].setMaxY(1.5D), AABB_BY_INDEX[13].setMaxY(1.5D), AABB_BY_INDEX[14].setMaxY(1.5D), AABB_BY_INDEX[15].setMaxY(1.5D)};

	public BlockReinforcedWall()
	{
		super(Material.ROCK, 2, Blocks.COBBLESTONE_WALL);
		setDefaultState(blockState.getBaseState().withProperty(UP, false).withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false).withProperty(VARIANT, EnumType.NORMAL));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		state = getActualState(state, source, pos);
		return AABB_BY_INDEX[getAABBIndex(state)];
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState)
	{
		if(!isActualState)
			state = this.getActualState(state, world, pos);

		addCollisionBoxToList(pos, entityBox, collidingBoxes, CLIP_AABB_BY_INDEX[getAABBIndex(state)]);
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		blockState = this.getActualState(blockState, worldIn, pos);
		return CLIP_AABB_BY_INDEX[getAABBIndex(blockState)];
	}

	private static int getAABBIndex(IBlockState state)
	{
		int i = 0;

		if(state.getValue(NORTH))
			i |= 1 << EnumFacing.NORTH.getHorizontalIndex();

		if(state.getValue(EAST))
			i |= 1 << EnumFacing.EAST.getHorizontalIndex();

		if(state.getValue(SOUTH))
			i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();

		if(state.getValue(WEST))
			i |= 1 << EnumFacing.WEST.getHorizontalIndex();

		return i;
	}

	@Override
	public String getLocalizedName()
	{
		return I18n.translateToLocal(getTranslationKey() + "." + EnumType.NORMAL.getTranslationKey() + ".name");
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess world, BlockPos pos)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	private boolean canConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		BlockFaceShape shape = state.getBlockFaceShape(world, pos, facing);
		return !isExcepBlockForAttachWithPiston(block) && shape == BlockFaceShape.SOLID || (shape == BlockFaceShape.MIDDLE_POLE_THICK || shape == BlockFaceShape.MIDDLE_POLE && block instanceof BlockFenceGate);
	}

	protected static boolean isExcepBlockForAttachWithPiston(Block block)
	{
		return Block.isExceptBlockForAttachWithPiston(block) || block == Blocks.BARRIER || block == Blocks.MELON_BLOCK || block == Blocks.PUMPKIN || block == Blocks.LIT_PUMPKIN;
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		for(EnumType type : EnumType.values())
		{
			items.add(new ItemStack(this, 1, type.getMetadata()));
		}
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return side == EnumFacing.DOWN ? super.shouldSideBeRendered(state, world, pos, side) : true;
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		boolean canConnectNorth =  canWallConnectTo(world, pos, EnumFacing.NORTH);
		boolean canConnectEast = canWallConnectTo(world, pos, EnumFacing.EAST);
		boolean canConnectSouth = canWallConnectTo(world, pos, EnumFacing.SOUTH);
		boolean canConnectWest = canWallConnectTo(world, pos, EnumFacing.WEST);
		boolean flag4 = canConnectNorth && !canConnectEast && canConnectSouth && !canConnectWest || !canConnectNorth && canConnectEast && !canConnectSouth && canConnectWest;
		return state.withProperty(UP, !flag4 || !world.isAirBlock(pos.up())).withProperty(NORTH, canConnectNorth).withProperty(EAST, canConnectEast).withProperty(SOUTH, canConnectSouth).withProperty(WEST, canConnectWest);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, UP, NORTH, EAST, WEST, SOUTH, VARIANT);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE_THICK : BlockFaceShape.CENTER_BIG;
	}

	@Override
	public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		return canConnectTo(world, pos.offset(facing), facing.getOpposite());
	}

	private boolean canWallConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		BlockPos other = pos.offset(facing);
		Block block = world.getBlockState(other).getBlock();
		return block.canBeConnectedTo(world, other, facing.getOpposite()) || canConnectTo(world, other, facing.getOpposite());
	}

	public static enum EnumType implements IStringSerializable
	{
		NORMAL(0, "reinforced_cobblestone", "normal"),
		MOSSY(1, "reinforced_mossy_cobblestone", "mossy");

		private static final EnumType[] META_LOOKUP = new EnumType[values().length];
		private final int meta;
		private final String name;
		private final String translationKey;

		private EnumType(int meta, String name, String unlocalizedName)
		{
			this.meta = meta;
			this.name = name;
			this.translationKey = unlocalizedName;
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

		public static EnumType byMetadata(int meta)
		{
			if(meta < 0 || meta >= META_LOOKUP.length)
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
			return translationKey;
		}

		static
		{
			for(EnumType type : values())
			{
				META_LOOKUP[type.getMetadata()] = type;
			}
		}
	}
}
