package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.BlockChest;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockKeypadChest extends BlockChest {

	public BlockKeypadChest(){
		super(Type.BASIC);
		setSoundType(SoundType.WOOD);
	}
	
	/**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){
    	if(!par1World.isRemote) {
            if(par1World.getTileEntity(pos) != null && par1World.getTileEntity(pos) instanceof TileEntityKeypadChest) {
            	((TileEntityKeypadChest) par1World.getTileEntity(pos)).openPasswordGUI(par5EntityPlayer);
            }  

            return true;
        }
        
        return true;
    }
    
    public static void activate(World par1World, BlockPos pos, EntityPlayer player){
    	player.displayGUIChest(((BlockChest) BlockUtils.getBlock(par1World, pos)).getLockableContainer(par1World, pos));
	}
    
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
        super.onBlockPlacedBy(par1World, pos, state, par5EntityLivingBase, par6ItemStack);
                
        if(par1World.getTileEntity(pos.east()) != null && par1World.getTileEntity(pos.east()) instanceof TileEntityKeypadChest){
        	((TileEntityKeypadChest)(par1World.getTileEntity(pos))).setPassword(((TileEntityKeypadChest) par1World.getTileEntity(pos.east())).getPassword());
		}else if(par1World.getTileEntity(pos.west()) != null && par1World.getTileEntity(pos.west()) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest)(par1World.getTileEntity(pos))).setPassword(((TileEntityKeypadChest) par1World.getTileEntity(pos.west())).getPassword());
		}else if(par1World.getTileEntity(pos.south()) != null && par1World.getTileEntity(pos.south()) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest)(par1World.getTileEntity(pos))).setPassword(((TileEntityKeypadChest) par1World.getTileEntity(pos.south())).getPassword());
		}else if(par1World.getTileEntity(pos.north()) != null && par1World.getTileEntity(pos.north()) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest)(par1World.getTileEntity(pos))).setPassword(((TileEntityKeypadChest) par1World.getTileEntity(pos.north())).getPassword());
		}
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor){
        super.onNeighborChange(world, pos, neighbor);
        TileEntityKeypadChest tileentitychest = (TileEntityKeypadChest)world.getTileEntity(pos);

        if (tileentitychest != null)
        {
            tileentitychest.updateContainingBlockInfo();
        }
      
    }
	
	/**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createTileEntity(World par1World, int par2)
    {
        return new TileEntityKeypadChest();
    }

}
