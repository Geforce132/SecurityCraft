package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ReinforcedMetalsBlock extends OwnableBlock implements IOverlayDisplay, IReinforcedBlock {
	public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create("variant", EnumType.class);

	public ReinforcedMetalsBlock() {
		super(Material.ROCK);
		setDefaultState(blockState.getBaseState().withProperty(VARIANT, EnumType.GOLD));
		setSoundType(SoundType.METAL);
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
	public int damageDropped(IBlockState state) {
		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		EnumType[] values = EnumType.values();

		for (EnumType type : values) {
			list.add(new ItemStack(this, 1, type.getMetadata()));
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT);
	}

	@Override
	public boolean isBeaconBase(IBlockAccess world, BlockPos pos, BlockPos beacon) {
		return world.getBlockState(pos).getValue(VARIANT) != EnumType.REDSTONE;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return state.getValue(VARIANT) == EnumType.REDSTONE;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.getValue(VARIANT) == EnumType.REDSTONE ? 15 : 0;
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos) {
		return new ItemStack(Item.getItemFromBlock(SCContent.reinforcedMetals), 1, getMetaFromState(state));
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos) {
		return state.getBlock() == this;
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(Blocks.GOLD_BLOCK, Blocks.IRON_BLOCK, Blocks.DIAMOND_BLOCK, Blocks.EMERALD_BLOCK, Blocks.REDSTONE_BLOCK);
	}

	@Override
	public IBlockState convertToReinforcedState(IBlockState state) {
		Block block = state.getBlock();

		if (block == Blocks.GOLD_BLOCK)
			return getDefaultState().withProperty(VARIANT, EnumType.GOLD);
		else if (block == Blocks.IRON_BLOCK)
			return getDefaultState().withProperty(VARIANT, EnumType.IRON);
		else if (block == Blocks.DIAMOND_BLOCK)
			return getDefaultState().withProperty(VARIANT, EnumType.DIAMOND);
		else if (block == Blocks.EMERALD_BLOCK)
			return getDefaultState().withProperty(VARIANT, EnumType.EMERALD);
		else if (block == Blocks.REDSTONE_BLOCK)
			return getDefaultState().withProperty(VARIANT, EnumType.REDSTONE);
		else
			return state;
	}

	@Override
	public IBlockState convertToVanillaState(IBlockState state) {
		switch (state.getValue(VARIANT)) {
			case GOLD:
				return Blocks.GOLD_BLOCK.getDefaultState();
			case IRON:
				return Blocks.IRON_BLOCK.getDefaultState();
			case DIAMOND:
				return Blocks.DIAMOND_BLOCK.getDefaultState();
			case EMERALD:
				return Blocks.EMERALD_BLOCK.getDefaultState();
			case REDSTONE:
				return Blocks.REDSTONE_BLOCK.getDefaultState();
			default:
				return state;
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

		if (meta >= 0 && meta <= 4)
			return new ItemStack(getVanillaBlocks().get(meta));
		else
			return ItemStack.EMPTY;
	}

	public enum EnumType implements IStringSerializable {
		GOLD(0, "gold", MapColor.GOLD),
		IRON(1, "iron", MapColor.IRON),
		DIAMOND(2, "diamond", MapColor.DIAMOND),
		EMERALD(3, "emerald", MapColor.EMERALD),
		REDSTONE(4, "redstone", MapColor.TNT);

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

		public MapColor getColor() {
			return color;
		}

		static {
			EnumType[] values = values();

			for (EnumType type : values) {
				META_LOOKUP[type.getMetadata()] = type;
			}
		}
	}

	public static class DoorActivator implements Function<Object, IDoorActivator>, IDoorActivator {
		private List<Block> blocks = Arrays.asList(SCContent.reinforcedMetals);

		@Override
		public IDoorActivator apply(Object o) {
			return this;
		}

		@Override
		public boolean isPowering(World world, BlockPos pos, IBlockState state, TileEntity te, EnumFacing direction, int distance) {
			return state.getValue(VARIANT) == EnumType.REDSTONE && distance == 1;
		}

		@Override
		public List<Block> getBlocks() {
			return blocks;
		}
	}
}