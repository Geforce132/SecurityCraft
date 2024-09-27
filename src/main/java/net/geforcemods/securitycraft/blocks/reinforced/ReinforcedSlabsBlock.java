package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedSlabsBlock extends BlockSlab implements ITileEntityProvider, IOverlayDisplay, IReinforcedBlock {
	public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create("variant", EnumType.class);
	private final boolean isDouble;

	public ReinforcedSlabsBlock(boolean isDouble, Material blockMaterial) {
		super(blockMaterial);

		this.isDouble = isDouble;

		if (!isDouble())
			useNeighborBrightness = true;

		setSoundType(SoundType.STONE);
		setDefaultState(blockState.getBaseState().withProperty(VARIANT, EnumType.STONE));
	}

	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state.getValue(VARIANT).getColor();
	}

	@Override
	public float getExplosionResistance(Entity exploder) {
		return Float.MAX_VALUE;
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		return Float.MAX_VALUE;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer) placer));
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(SCContent.reinforcedStoneSlabs);
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (!isDouble) {
			for (EnumType et : EnumType.values()) {
				list.add(new ItemStack(this, 1, et.getMetadata()));
			}
		}
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	public String getTranslationKey(int meta) {
		return super.getTranslationKey() + "." + EnumType.byMetadata(meta).getTranslationKey();
	}

	@Override
	public IProperty<?> getVariantProperty() {
		return VARIANT;
	}

	@Override
	public Comparable<?> getTypeForItem(ItemStack stack) {
		return EnumType.byMetadata(stack.getMetadata() & 7);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta & 7)).withProperty(HALF, (meta & 8) == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		byte b0 = 0;
		int meta = b0 | state.getValue(VARIANT).getMetadata();

		if (state.getValue(HALF) == EnumBlockHalf.TOP)
			meta |= 8;

		return meta;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, HALF, VARIANT);
	}

	@Override
	public boolean isDouble() {
		return isDouble;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new OwnableBlockEntity();
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(SCContent.reinforcedStoneSlabs, 1, damageDropped(state));
	}

	public enum EnumType implements IStringSerializable {
		STONE(0, "stone", "stone", MapColor.STONE),
		COBBLESTONE(1, "cobblestone", "cobble", MapColor.STONE),
		SANDSTONE(2, "sandstone", "sandstone", MapColor.SAND),
		STONEBRICK(3, "stonebrick", "stonebrick", MapColor.STONE),
		BRICK(4, "brick", "brick", MapColor.RED),
		NETHERBRICK(5, "netherbrick", "netherbrick", MapColor.NETHERRACK),
		QUARTZ(6, "quartz", "quartz", MapColor.QUARTZ);

		private static final EnumType[] META_LOOKUP = new EnumType[values().length];
		private final int meta;
		private final String name;
		private final String unlocalizedName;
		private final MapColor color;

		private EnumType(int meta, String name, String unlocalizedName, MapColor color) {
			this.meta = meta;
			this.name = name;
			this.unlocalizedName = unlocalizedName;
			this.color = color;
		}

		public int getMetadata() {
			return meta;
		}

		@Override
		public String toString() {
			return name;
		}

		public static EnumType byMetadata(int meta) {
			if (meta < 0 || meta >= META_LOOKUP.length)
				meta = 0;

			return META_LOOKUP[meta];
		}

		@Override
		public String getName() {
			return name;
		}

		public String getTranslationKey() {
			return unlocalizedName;
		}

		public MapColor getColor() {
			return color;
		}

		static {
			EnumType[] values = values();
			int length = values.length;

			for (int i = 0; i < length; ++i) {
				EnumType type = values[i];
				META_LOOKUP[type.getMetadata()] = type;
			}
		}
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos) {
		return new ItemStack(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs), 1, getMetaFromState(state) % 8);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos) {
		return state.getBlock() == this;
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(isDouble ? Blocks.DOUBLE_STONE_SLAB : Blocks.STONE_SLAB);
	}

	@Override
	public IBlockState convertToReinforcedState(IBlockState state) {
		EnumType type = null;

		switch (state.getValue(BlockStoneSlab.VARIANT)) {
			case STONE:
				type = EnumType.STONE;
				break;
			case SAND:
				type = EnumType.SANDSTONE;
				break;
			case WOOD:
				break;
			case COBBLESTONE:
				type = EnumType.COBBLESTONE;
				break;
			case BRICK:
				type = EnumType.BRICK;
				break;
			case SMOOTHBRICK:
				type = EnumType.STONEBRICK;
				break;
			case NETHERBRICK:
				type = EnumType.NETHERBRICK;
				break;
			case QUARTZ:
				type = EnumType.QUARTZ;
				break;
		}

		if (type != null) {
			IBlockState convertedState = getDefaultState().withProperty(VARIANT, type);

			if (!isDouble)
				convertedState = convertedState.withProperty(HALF, state.getValue(HALF));

			return convertedState;
		}
		else
			return state;
	}

	@Override
	public IBlockState convertToVanillaState(IBlockState state) {
		BlockStoneSlab.EnumType type = null;

		switch (state.getValue(VARIANT)) {
			case STONE:
				type = BlockStoneSlab.EnumType.STONE;
				break;
			case COBBLESTONE:
				type = BlockStoneSlab.EnumType.COBBLESTONE;
				break;
			case SANDSTONE:
				type = BlockStoneSlab.EnumType.SAND;
				break;
			case STONEBRICK:
				type = BlockStoneSlab.EnumType.SMOOTHBRICK;
				break;
			case BRICK:
				type = BlockStoneSlab.EnumType.BRICK;
				break;
			case NETHERBRICK:
				type = BlockStoneSlab.EnumType.NETHERBRICK;
				break;
			case QUARTZ:
				type = BlockStoneSlab.EnumType.QUARTZ;
				break;
		}

		if (type != null) {
			if (isDouble)
				return Blocks.DOUBLE_STONE_SLAB.getDefaultState().withProperty(BlockStoneSlab.VARIANT, type).withProperty(BlockStoneSlab.SEAMLESS, false);
			else
				return Blocks.STONE_SLAB.getDefaultState().withProperty(BlockStoneSlab.VARIANT, type).withProperty(HALF, state.getValue(HALF));
		}
		else
			return state;
	}

	@Override
	public ItemStack convertToReinforcedStack(ItemStack stackToConvert, Block blockToConvert) {
		if (!isDouble) {
			switch (stackToConvert.getMetadata()) {
				case 0:
					return new ItemStack(this, 1, 0);
				case 1:
					return new ItemStack(this, 1, 2);
				case 3:
					return new ItemStack(this, 1, 1);
				case 4:
					return new ItemStack(this, 1, 4);
				case 5:
					return new ItemStack(this, 1, 3);
				case 6:
					return new ItemStack(this, 1, 5);
				case 7:
					return new ItemStack(this, 1, 6);
				default:
					return ItemStack.EMPTY;
			}
		}
		else
			return ItemStack.EMPTY;
	}

	@Override
	public ItemStack convertToVanillaStack(ItemStack stackToConvert) {
		switch (stackToConvert.getMetadata()) {
			case 0:
				return new ItemStack(this, 1, 0);
			case 1:
				return new ItemStack(this, 1, 3);
			case 2:
				return new ItemStack(this, 1, 1);
			case 3:
				return new ItemStack(this, 1, 5);
			case 4:
				return new ItemStack(this, 1, 4);
			case 5:
				return new ItemStack(this, 1, 6);
			case 6:
				return new ItemStack(this, 1, 7);
			default:
				return ItemStack.EMPTY;
		}
	}
}
