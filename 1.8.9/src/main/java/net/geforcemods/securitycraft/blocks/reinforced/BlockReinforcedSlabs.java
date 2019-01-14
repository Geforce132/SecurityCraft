package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedSlabs extends BlockSlab implements ITileEntityProvider {

	public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockReinforcedSlabs.EnumType.class);

	private final boolean isDouble;
	private final Material slabMaterial;

	public BlockReinforcedSlabs(boolean isDouble, Material blockMaterial){
		super(blockMaterial);

		this.isDouble = isDouble;
		slabMaterial = blockMaterial;

		if(!isDouble())
			useNeighborBrightness = true;

		if(blockMaterial == Material.ground)
			setStepSound(soundTypeGravel);
		else
			setStepSound(soundTypeStone);

		setDefaultState(blockState.getBaseState().withProperty(VARIANT, BlockReinforcedSlabs.EnumType.STONE));
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, BlockPos pos, Entity entity)
	{
		return !(entity instanceof EntityWither);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return slabMaterial == Material.ground ? Item.getItemFromBlock(SCContent.reinforcedDirtSlab) : Item.getItemFromBlock(SCContent.reinforcedStoneSlabs);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		if(slabMaterial != Material.ground){
			BlockReinforcedSlabs.EnumType[] values = BlockReinforcedSlabs.EnumType.values();

			for(int i = 0; i < values.length; i++){
				BlockReinforcedSlabs.EnumType type = values[i];

				if(type.getMetadata() == 3) //skip dirt slab
					continue;

				list.add(new ItemStack(item, 1, type.getMetadata()));
			}
		}
		else
			list.add(new ItemStack(item, 1, BlockReinforcedSlabs.EnumType.DIRT.getMetadata()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, BlockPos pos){
		return slabMaterial == Material.ground ? Item.getItemFromBlock(SCContent.reinforcedDirtSlab) : Item.getItemFromBlock(SCContent.reinforcedStoneSlabs);
	}

	@Override
	public int damageDropped(IBlockState state){
		return ((BlockReinforcedSlabs.EnumType)state.getValue(VARIANT)).getMetadata();
	}

	@Override
	public String getUnlocalizedName(int meta){
		return super.getUnlocalizedName() + "." + BlockReinforcedSlabs.EnumType.byMetadata(meta).getUnlocalizedName();
	}

	@Override
	public IProperty getVariantProperty(){
		return VARIANT;
	}

	@Override
	public Object getVariant(ItemStack stack) {
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
	protected BlockState createBlockState(){
		return slabMaterial == Material.ground ? new BlockState(this, new IProperty[] {HALF}) : new BlockState(this, new IProperty[] {HALF, VARIANT});
	}

	@Override
	public boolean isDouble(){
		return isDouble;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}


	public static enum EnumType implements IStringSerializable{
		STONE(0, "stone"),
		COBBLESTONE(1, "cobblestone", "cobble"),
		SANDSTONE(2, "sandstone", "sandstone"),
		DIRT(3, "dirt", "dirt"),
		STONEBRICK(4, "stonebrick", "stonebrick"),
		BRICK(5, "brick", "brick"),
		NETHERBRICK(6, "netherbrick", "netherbrick"),
		QUARTZ(7, "quartz", "quartz");

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

		public String getUnlocalizedName(){
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
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass)
	{
		return 0x999999;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(IBlockState state)
	{
		return 0x999999;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return 0x999999;
	}
}
