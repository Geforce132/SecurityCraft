package net.geforcemods.securitycraft.blocks;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
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
		this.slabMaterial = blockMaterial;
		
		if(!this.isDouble()){
			this.useNeighborBrightness = true;
		}
		
		if(blockMaterial == Material.ground){
			this.setStepSound(soundTypeGravel);
		}else{
			this.setStepSound(soundTypeStone);
		}	
		
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockReinforcedSlabs.EnumType.STONE));
	}
	
	public void breakBlock(World par1World, BlockPos pos, IBlockState state){
        super.breakBlock(par1World, pos, state);
        par1World.removeTileEntity(pos);
    }
	
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
        return slabMaterial == Material.ground ? Item.getItemFromBlock(mod_SecurityCraft.reinforcedDirtSlab) : Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs);
    }
	
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list){
        if(slabMaterial != Material.ground){
        	BlockReinforcedSlabs.EnumType[] aenumtype = BlockReinforcedSlabs.EnumType.values();

            for(int i = 0; i < aenumtype.length; i++){
            	BlockReinforcedSlabs.EnumType enumtype = aenumtype[i];
            	
            	if(enumtype.getMetadata() == 3) //skip dirt slab
            		continue;
            	
                list.add(new ItemStack(itemIn, 1, enumtype.getMetadata()));              
            }
        }else{
            list.add(new ItemStack(itemIn, 1, BlockReinforcedSlabs.EnumType.DIRT.getMetadata()));              
        }
    }

    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, BlockPos pos){
        return slabMaterial == Material.ground ? Item.getItemFromBlock(mod_SecurityCraft.reinforcedDirtSlab) : Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs);
    }
    
    public int damageDropped(IBlockState state){
        return ((BlockReinforcedSlabs.EnumType)state.getValue(VARIANT)).getMetadata();
    }
    
    public String getUnlocalizedName(int meta){
        return super.getUnlocalizedName() + "." + BlockReinforcedSlabs.EnumType.byMetadata(meta).getUnlocalizedName();
    }
    
    public IProperty getVariantProperty(){
        return VARIANT;
    }
    
    public Object getVariant(ItemStack stack) {
		return BlockReinforcedSlabs.EnumType.byMetadata(stack.getMetadata() & 7);
	}
    
    public IBlockState getStateFromMeta(int meta){
        IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, BlockReinforcedSlabs.EnumType.byMetadata(meta & 7));
        
        iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);

        return iblockstate;
    }

    public int getMetaFromState(IBlockState state){
        byte b0 = 0;
        int i = b0 | ((BlockReinforcedSlabs.EnumType)state.getValue(VARIANT)).getMetadata();

        if(state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP){
            i |= 8;
        }

        return i;
    }
    
    protected BlockState createBlockState(){
        return slabMaterial == Material.ground ? new BlockState(this, new IProperty[] {HALF}) : new BlockState(this, new IProperty[] {HALF, VARIANT});
    }
    
    public boolean isDouble(){
		return isDouble;
	}

	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityOwnable();
	}

	
	public static enum EnumType implements IStringSerializable{
        STONE(0, "stone"),
        COBBLESTONE(1, "cobblestone", "cobble"),
        SANDSTONE(2, "sandstone", "sandstone"),
        DIRT(3, "dirt", "dirt"),
		STONEBRICK(4, "stonebrick", "stonebrick"),
		BRICK(5, "brick", "brick"),
		NETHERBRICK(6, "netherbrick", "netherbrick");
    
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
            return this.meta;
        }

        public String toString(){
            return this.name;
        }

        public static BlockReinforcedSlabs.EnumType byMetadata(int meta){
            if(meta < 0 || meta >= META_LOOKUP.length){
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName(){
            return this.name;
        }

        public String getUnlocalizedName(){
            return this.unlocalizedName;
        }

        static {
        	BlockReinforcedSlabs.EnumType[] var0 = values();
            int var1 = var0.length;

            for(int var2 = 0; var2 < var1; ++var2){
            	BlockReinforcedSlabs.EnumType var3 = var0[var2];
                META_LOOKUP[var3.getMetadata()] = var3;
            }
        }
    }
	
	@Override
    @SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
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
