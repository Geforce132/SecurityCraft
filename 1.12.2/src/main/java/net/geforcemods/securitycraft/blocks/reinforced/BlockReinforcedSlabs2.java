package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.imc.waila.ICustomWailaDisplay;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedSlabs2 extends BlockSlab implements ITileEntityProvider, ICustomWailaDisplay {

	public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockReinforcedSlabs2.EnumType.class);

	private final boolean isDouble;
	public BlockReinforcedSlabs2(boolean isDouble, Material blockMaterial){
		super(blockMaterial);

		this.isDouble = isDouble;
		if(!isDouble())
			useNeighborBrightness = true;

		setDefaultState(blockState.getBaseState().withProperty(VARIANT, BlockReinforcedSlabs2.EnumType.RED_SANDSTONE));
	}

	@Override
	public void breakBlock(World par1World, BlockPos pos, IBlockState state){
		super.breakBlock(par1World, pos, state);
		par1World.removeTileEntity(pos);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs2);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
	{
		if(isDouble)
			return;

		for (EnumType et : EnumType.values())
			items.add(new ItemStack(this, 1, et.getMetadata()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state){
		return new ItemStack(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs2));
	}

	@Override
	public int damageDropped(IBlockState state){
		return ((BlockReinforcedSlabs2.EnumType)state.getValue(VARIANT)).getMetadata();
	}

	@Override
	public String getUnlocalizedName(int meta){
		return super.getUnlocalizedName() + "." + BlockReinforcedSlabs2.EnumType.byMetadata(meta).getUnlocalizedName();
	}

	@Override
	public IProperty<?> getVariantProperty(){
		return VARIANT;
	}

	@Override
	public Comparable<?> getTypeForItem(ItemStack stack) {
		return BlockReinforcedSlabs2.EnumType.byMetadata(stack.getMetadata() & 7);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		IBlockState iblockstate = getDefaultState().withProperty(VARIANT, BlockReinforcedSlabs2.EnumType.byMetadata(meta & 7));

		iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);

		return iblockstate;
	}

	@Override
	public int getMetaFromState(IBlockState state){
		byte b0 = 0;
		int i = b0 | ((BlockReinforcedSlabs2.EnumType)state.getValue(VARIANT)).getMetadata();

		if(state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP)
			i |= 8;

		return i;
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
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityOwnable();
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		return new ItemStack(Item.getItemFromBlock(state.getBlock()), 1, damageDropped(state));
	}

	public static enum EnumType implements IStringSerializable{
		RED_SANDSTONE(0, "red_sandstone"),
		PURPUR(1, "purpur");

		private static final BlockReinforcedSlabs2.EnumType[] META_LOOKUP = new BlockReinforcedSlabs2.EnumType[values().length];
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

		public static BlockReinforcedSlabs2.EnumType byMetadata(int meta){
			if(meta < 0 || meta >= META_LOOKUP.length)
				meta = 0;

			return META_LOOKUP[meta];
		}

		@Override
		public String getName(){
			return name;
		}

		public String getUnlocalizedName(){
			return unlocalizedName;
		}

		static {
			BlockReinforcedSlabs2.EnumType[] var0 = values();
			int var1 = var0.length;

			for(int var2 = 0; var2 < var1; ++var2){
				BlockReinforcedSlabs2.EnumType var3 = var0[var2];
				META_LOOKUP[var3.getMetadata()] = var3;
			}
		}
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos)
	{
		return new ItemStack(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs2), 1, BlockUtils.getBlockMeta(world, pos) % 8);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos)
	{
		return true;
	}
}
