package net.geforcemods.securitycraft.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockInventoryScanner extends BlockContainer {
	
	@SideOnly(Side.CLIENT)
    private IIcon furnaceIconTop;
    @SideOnly(Side.CLIENT)
    private IIcon furnaceIconFront;
    
	public BlockInventoryScanner(Material par1Material) {
		super(par1Material);
	}
    
    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4){
        super.onBlockAdded(par1World, par2, par3, par4);
        this.setDefaultDirection(par1World, par2, par3, par4);
    }

    /**
     * set a blocks direction
     */
    private void setDefaultDirection(World par1World, int par2, int par3, int par4){
        if(!par1World.isRemote){
        	Block block = par1World.getBlock(par2, par3, par4 - 1);
            Block block1 = par1World.getBlock(par2, par3, par4 + 1);
            Block block2 = par1World.getBlock(par2 - 1, par3, par4);
            Block block3 = par1World.getBlock(par2 + 1, par3, par4);
            byte b0 = 3;

            if (block.func_149730_j() && !block1.func_149730_j()){
                b0 = 3;
            }

            if (block1.func_149730_j() && !block.func_149730_j()){
                b0 = 2;
            }

            if (block2.func_149730_j() && !block3.func_149730_j()){
                b0 = 5;
            }

            if (block3.func_149730_j() && !block2.func_149730_j()){
                b0 = 4;
            }

            par1World.setBlockMetadataWithNotify(par2, par3, par4, b0, 2);
        }
    }
    
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){  
    	if(par1World.isRemote){
    		return true;
    	}else{
    		if(this.isFacingAnotherBlock(par1World, par2, par3, par4)){
    			par5EntityPlayer.openGui(mod_SecurityCraft.instance, 9, par1World, par2, par3, par4);
    		}else{
    			PlayerUtils.sendMessageToPlayer(par5EntityPlayer, StatCollector.translateToLocal("tile.inventoryScanner.name"), StatCollector.translateToLocal("messages.invScan.notConnected"), EnumChatFormatting.RED);
    		}
    		
    		return true;
    	}
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){    	
        int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        ((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());
        
        if (l == 0){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);
        }

        if (l == 1){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);
        }

        if (l == 2){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
        }

        if (l == 3){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
        }
        
        this.checkAndPlaceAppropriately(par1World, par2, par3, par4);
    }
    
    private void checkAndPlaceAppropriately(World par1World, int par2, int par3, int par4) {
		if(par1World.getBlockMetadata(par2, par3, par4) == 4 && par1World.getBlock(par2 - 2, par3, par4) == this && par1World.getBlock(par2 - 1, par3, par4) == Blocks.air && par1World.getBlockMetadata(par2 - 2, par3, par4) == 5){
			par1World.setBlock(par2 - 1, par3, par4, mod_SecurityCraft.inventoryScannerField, 1, 3);
		}
		else if(par1World.getBlockMetadata(par2, par3, par4) == 5 && par1World.getBlock(par2 + 2, par3, par4) == this && par1World.getBlock(par2 + 1, par3, par4) == Blocks.air && par1World.getBlockMetadata(par2 + 2, par3, par4) == 4){
			par1World.setBlock(par2 + 1, par3, par4, mod_SecurityCraft.inventoryScannerField, 1, 3);
		}
		else if(par1World.getBlockMetadata(par2, par3, par4) == 2 && par1World.getBlock(par2, par3, par4 - 2) == this && par1World.getBlock(par2, par3, par4 - 1) == Blocks.air && par1World.getBlockMetadata(par2, par3, par4 - 2) == 3){
			par1World.setBlock(par2, par3, par4 - 1, mod_SecurityCraft.inventoryScannerField, 2, 3);
		}
		else if(par1World.getBlockMetadata(par2, par3, par4) == 3 && par1World.getBlock(par2, par3, par4 + 2) == this && par1World.getBlock(par2, par3, par4 + 1) == Blocks.air && par1World.getBlockMetadata(par2, par3, par4 + 2) == 2){
			par1World.setBlock(par2, par3, par4 + 1, mod_SecurityCraft.inventoryScannerField, 2, 3);
		}
	}
    
    private boolean isFacingAnotherBlock(World par1World, int par2, int par3, int par4){
    	if(par1World.getBlockMetadata(par2, par3, par4) == 4 && par1World.getBlock(par2 - 2, par3, par4) == mod_SecurityCraft.inventoryScanner && par1World.getBlock(par2 - 1, par3, par4) == mod_SecurityCraft.inventoryScannerField && par1World.getBlockMetadata(par2 - 2, par3, par4) == 5){
			return true;
		}
		else if(par1World.getBlockMetadata(par2, par3, par4) == 5 && par1World.getBlock(par2 + 2, par3, par4) == mod_SecurityCraft.inventoryScanner && par1World.getBlock(par2 + 1, par3, par4) == mod_SecurityCraft.inventoryScannerField&& par1World.getBlockMetadata(par2 + 2, par3, par4) == 4){
			return true;
		}
		else if(par1World.getBlockMetadata(par2, par3, par4) == 2 && par1World.getBlock(par2, par3, par4 - 2) == mod_SecurityCraft.inventoryScanner && par1World.getBlock(par2, par3, par4 - 1) == mod_SecurityCraft.inventoryScannerField && par1World.getBlockMetadata(par2, par3, par4 - 2) == 3){
			return true;
		}
		else if(par1World.getBlockMetadata(par2, par3, par4) == 3 && par1World.getBlock(par2, par3, par4 + 2) == mod_SecurityCraft.inventoryScanner && par1World.getBlock(par2, par3, par4 + 1) == mod_SecurityCraft.inventoryScannerField && par1World.getBlockMetadata(par2, par3, par4 + 2) == 2){
			return true;
		}else{
			return false;
		}
    }
    
    public void breakBlock(World par1World, int par2, int par3, int par4, Block par5Block, int par6){	
    	if(par6 == 4 && par1World.getBlock(par2 - 2, par3, par4) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2 - 2, par3, par4) == 5){
    		ModuleUtils.insertModule(par1World, par2 - 2, par3, par4, null);	
    		((TileEntityInventoryScanner) par1World.getTileEntity(par2 - 2, par3, par4)).clearStorage();
    	}else if(par6 == 5 && par1World.getBlock(par2 + 2, par3, par4) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2 + 2, par3, par4) == 4){
    		ModuleUtils.insertModule(par1World, par2 + 2, par3, par4, null);	
    		((TileEntityInventoryScanner) par1World.getTileEntity(par2 + 2, par3, par4)).clearStorage();
		}else if(par6 == 2 && par1World.getBlock(par2, par3, par4 - 2) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2, par3, par4 - 2) == 3){
			ModuleUtils.insertModule(par1World, par2, par3, par4 - 2, null);	
    		((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 - 2)).clearStorage();
    	}else if(par6 == 3 && par1World.getBlock(par2, par3, par4 + 2) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2, par3, par4 + 2) == 2){
    		ModuleUtils.insertModule(par1World, par2, par3, par4 + 2, null);	
    		((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 + 2)).clearStorage();
    	}
    	
    	for(int i = 0; i < ((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4)).getContents().length; i++){
    		if(((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4)).getContents()[i] != null){
    			EntityItem item = new EntityItem(par1World, par2, par3, par4, ((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4)).getContents()[i]);
    			par1World.spawnEntityInWorld(item);
    		}
    	}
    	
    	par1World.removeTileEntity(par2, par3, par4);	
    	
    	super.breakBlock(par1World, par2, par3, par4, par5Block, par6);
    }
    
    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower(){
        return true;
    }
    
    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5){
    	if(!(par1IBlockAccess.getTileEntity(par2, par3, par4) instanceof TileEntityInventoryScanner) || ((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(par2, par3, par4)).getType() == null){
    		return 0 ;
    	}
    	    	
    	return (((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(par2, par3, par4)).getType().matches("redstone") && ((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(par2, par3, par4)).shouldProvidePower())? 15 : 0;
    }
    
    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5){
    	if(((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(par2, par3, par4)).getType() == null){
    		return 0;
    	}
    	
    	return (((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(par2, par3, par4)).getType().matches("redstone") && ((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(par2, par3, par4)).shouldProvidePower())? 15 : 0;
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
	@SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2)
    {
        if(par1 == 3 && par2 == 0){
    		return this.furnaceIconFront;
    	}
        
        return par1 == 1 ? this.furnaceIconTop : (par1 == 0 ? this.furnaceIconTop : (par1 != par2 ? this.blockIcon : this.furnaceIconFront));
    }

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister){
        this.blockIcon = par1IconRegister.registerIcon("furnace_side");
        this.furnaceIconFront = par1IconRegister.registerIcon("securitycraft:inventoryScanner");
        this.furnaceIconTop = par1IconRegister.registerIcon("furnace_top");
    }

	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityInventoryScanner();
	}

}
