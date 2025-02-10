package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSlab;
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

public class ReinforcedRedSandstoneAndPurpurSlabsBlock extends BlockSlab implements ITileEntityProvider, IOverlayDisplay, IReinforcedBlock {
	public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create("variant", EnumType.class);
	private final boolean isDouble;

	public ReinforcedRedSandstoneAndPurpurSlabsBlock(boolean isDouble, Material blockMaterial) {
		super(blockMaterial);

		this.isDouble = isDouble;

		if (!isDouble())
			useNeighborBrightness = true;

		setDefaultState(blockState.getBaseState().withProperty(VARIANT, EnumType.RED_SANDSTONE));
	}

	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getPlayerRelativeBlockHardness, state, player, level, pos);
	}

	@Override
	public boolean canHarvestBlock(IBlockAccess level, BlockPos pos, EntityPlayer player) {
		return ConfigHandler.alwaysDrop || super.canHarvestBlock(level, pos, player);
	}

	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
		return convertToVanillaState(state).getBlockHardness(world, pos);
	}

	@Override
	public Material getMaterial(IBlockState state) {
		return convertToVanillaState(state).getMaterial();
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().getSoundType(vanillaState, world, pos, entity);
	}

	@Override
	public String getHarvestTool(IBlockState state) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().getHarvestTool(vanillaState);
	}

	@Override
	public boolean isToolEffective(String type, IBlockState state) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().isToolEffective(type, vanillaState);
	}

	@Override
	public int getHarvestLevel(IBlockState state) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().getHarvestLevel(vanillaState);
	}

	@Override
	public boolean isTranslucent(IBlockState state) {
		return convertToVanillaState(state).isTranslucent();
	}

	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
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
		return Item.getItemFromBlock(SCContent.reinforcedStoneSlabs2);
	}

	@Override
	public void getSubBlocks(CreativeTabs item, NonNullList<ItemStack> items) {
		if (!isDouble) {
			for (EnumType et : EnumType.values()) {
				items.add(new ItemStack(this, 1, et.getMetadata()));
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
		return new ItemStack(SCContent.reinforcedStoneSlabs2, 1, damageDropped(state));
	}

	@Override
	public List<Block> getVanillaBlocks() {
		if (isDouble)
			return Arrays.asList(Blocks.DOUBLE_STONE_SLAB2, Blocks.PURPUR_DOUBLE_SLAB);
		else
			return Arrays.asList(Blocks.STONE_SLAB2, Blocks.PURPUR_SLAB);
	}

	@Override
	public IBlockState convertToReinforcedState(IBlockState state) {
		Block block = state.getBlock();

		if (block == Blocks.STONE_SLAB2)
			return getDefaultState().withProperty(VARIANT, EnumType.RED_SANDSTONE).withProperty(HALF, state.getValue(HALF));
		else if (block == Blocks.DOUBLE_STONE_SLAB2)
			return getDefaultState().withProperty(VARIANT, EnumType.RED_SANDSTONE).withProperty(HALF, EnumBlockHalf.TOP);
		else if (block == Blocks.PURPUR_SLAB)
			return getDefaultState().withProperty(VARIANT, EnumType.PURPUR).withProperty(HALF, state.getValue(HALF));
		else if (block == Blocks.PURPUR_DOUBLE_SLAB)
			return getDefaultState().withProperty(VARIANT, EnumType.PURPUR).withProperty(HALF, EnumBlockHalf.TOP);
		else
			return state;
	}

	@Override
	public IBlockState convertToVanillaState(IBlockState state) {
		if (!isDouble) {
			switch (state.getValue(VARIANT)) {
				case RED_SANDSTONE:
					return Blocks.STONE_SLAB2.getDefaultState().withProperty(HALF, state.getValue(HALF));
				case PURPUR:
					return Blocks.PURPUR_SLAB.getDefaultState().withProperty(HALF, state.getValue(HALF));
				default:
					return state;
			}
		}
		else {
			switch (state.getValue(VARIANT)) {
				case RED_SANDSTONE:
					return Blocks.DOUBLE_STONE_SLAB2.getDefaultState();
				case PURPUR:
					return Blocks.PURPUR_DOUBLE_SLAB.getDefaultState();
				default:
					return state;
			}
		}
	}

	@Override
	public ItemStack convertToReinforcedStack(ItemStack stackToConvert, Block blockToConvert) {
		int index = getVanillaBlocks().indexOf(blockToConvert);

		if (index >= 0)
			return new ItemStack(this, 1, index);
		else
			return ItemStack.EMPTY;
	}

	@Override
	public ItemStack convertToVanillaStack(ItemStack stackToConvert) {
		int meta = stackToConvert.getMetadata();

		if (meta >= 0 && meta <= 1)
			return new ItemStack(getVanillaBlocks().get(meta));
		else
			return ItemStack.EMPTY;
	}

	public enum EnumType implements IStringSerializable {
		RED_SANDSTONE(0, "red_sandstone", BlockSand.EnumType.RED_SAND.getMapColor()),
		PURPUR(1, "purpur", MapColor.MAGENTA);

		private static final EnumType[] META_LOOKUP = new EnumType[values().length];
		private final int meta;
		private final String name;
		private final MapColor color;

		private EnumType(int meta, String name, MapColor color) {
			this.meta = meta;
			this.name = name;
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
			return name;
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
		return new ItemStack(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs2), 1, getMetaFromState(state) % 8);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos) {
		return state.getBlock() == this;
	}
}
