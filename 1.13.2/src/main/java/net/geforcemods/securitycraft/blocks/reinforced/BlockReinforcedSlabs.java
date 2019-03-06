package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;

public class BlockReinforcedSlabs extends BlockSlab implements IOverlayDisplay {

	public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockReinforcedSlabs.EnumType.class);

	private final boolean isDouble;
	public BlockReinforcedSlabs(boolean isDouble, Material blockMaterial){
		super(blockMaterial);

		this.isDouble = isDouble;
		if(!isDouble())
			useNeighborBrightness = true;

		setSoundType(SoundType.STONE);
		setDefaultState(blockState.getBaseState().withProperty(VARIANT, BlockReinforcedSlabs.EnumType.STONE));
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return Item.getItemFromBlock(SCContent.reinforcedStoneSlabs);
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
	{
		if(!isDouble)
			for (EnumType et : EnumType.values())
				list.add(new ItemStack(this, 1, et.getMetadata()));
	}

	@Override
	public ItemStack getItem(World world, BlockPos pos, IBlockState state){
		return new ItemStack(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs));
	}

	@Override
	public int damageDropped(IBlockState state){
		return ((BlockReinforcedSlabs.EnumType)state.getValue(VARIANT)).getMetadata();
	}

	@Override
	public String getTranslationKey(int meta){
		return super.getTranslationKey() + "." + BlockReinforcedSlabs.EnumType.byMetadata(meta).getTranslationKey();
	}

	@Override
	public IProperty<?> getVariantProperty(){
		return VARIANT;
	}

	@Override
	public Comparable<?> getTypeForItem(ItemStack stack) {
		return BlockReinforcedSlabs.EnumType.byMetadata(stack.getMetadata() & 7);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		IBlockState state = getDefaultState().withProperty(VARIANT, BlockReinforcedSlabs.EnumType.byMetadata(meta & 7));

		state = state.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);

		return state;
	}

	@Override
	public int getMetaFromState(IBlockState state){
		byte b0 = 0;
		int meta = b0 | ((BlockReinforcedSlabs.EnumType)state.getValue(VARIANT)).getMetadata();

		if(state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP)
			meta |= 8;

		return meta;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {HALF, VARIANT});
	}

	@Override
	public boolean isDouble(){
		return isDouble;
	}

	@Override
	public TileEntity createTileEntity(IBlockState state, IBlockReader reader) {
		return new TileEntityOwnable();
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		return new ItemStack(Item.getItemFromBlock(state.getBlock()), 1, damageDropped(state));
	}

	public static enum EnumType implements IStringSerializable{
		STONE(0, "stone"),
		COBBLESTONE(1, "cobblestone", "cobble"),
		SANDSTONE(2, "sandstone", "sandstone"),
		STONEBRICK(3, "stonebrick", "stonebrick"),
		BRICK(4, "brick", "brick"),
		NETHERBRICK(5, "netherbrick", "netherbrick"),
		QUARTZ(6, "quartz", "quartz");

		private static final BlockReinforcedSlabs.EnumType[] META_LOOKUP = new BlockReinforcedSlabs.EnumType[values().length];
		private final int meta;
		private final String name;
		private final String unlocalizedName;

		private EnumType(int meta, String name){
			this(meta, name, name);
		}

		private EnumType(int meta, String name, String unlocalizedName){
			this.meta = meta;
			this.name = name;
			this.unlocalizedName = unlocalizedName;
		}

		public int getMetadata(){
			return meta;
		}

		@Override
		public String toString(){
			return name;
		}

		public static BlockReinforcedSlabs.EnumType byMetadata(int meta){
			if(meta < 0 || meta >= META_LOOKUP.length)
				meta = 0;

			return META_LOOKUP[meta];
		}

		@Override
		public String getName(){
			return name;
		}

		public String getTranslationKey(){
			return unlocalizedName;
		}

		static {
			BlockReinforcedSlabs.EnumType[] values = values();
			int length = values.length;

			for(int i = 0; i < length; ++i){
				BlockReinforcedSlabs.EnumType type = values[i];
				META_LOOKUP[type.getMetadata()] = type;
			}
		}
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos)
	{
		return new ItemStack(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs), 1, BlockUtils.getBlockMeta(world, pos) % 8);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos)
	{
		return true;
	}
}
