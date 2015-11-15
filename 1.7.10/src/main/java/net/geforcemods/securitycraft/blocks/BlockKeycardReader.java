package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.items.ItemKeycardBase;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockKeycardReader extends BlockOwnable {

	@SideOnly(Side.CLIENT)
    private IIcon keypadIconTop;
    @SideOnly(Side.CLIENT)
    private IIcon keypadIconFront;
    
	public BlockKeycardReader(Material par2Material) {
		super(par2Material);
	}
	 	     
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
        super.onBlockPlacedBy(par1World, par2, par3, par4, par5EntityLivingBase, par6ItemStack);
    	
    	int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        
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
    }
	    	
	public void insertCard(World par1World, int par2, int par3, int par4, ItemStack par5ItemStack, EntityPlayer par6EntityPlayer) {		
		if(ModuleUtils.checkForModule(par1World, par2, par3, par4, par6EntityPlayer, EnumCustomModules.WHITELIST) || ModuleUtils.checkForModule(par1World, par2, par3, par4, par6EntityPlayer, EnumCustomModules.BLACKLIST)){ return; }
		
		if((((IPasswordProtected) par1World.getTileEntity(par2, par3, par4)).getPassword() != null) && (!((TileEntityKeycardReader) par1World.getTileEntity(par2, par3, par4)).doesRequireExactKeycard() && Integer.parseInt(((IPasswordProtected) par1World.getTileEntity(par2, par3, par4)).getPassword()) <= ((ItemKeycardBase) par5ItemStack.getItem()).getKeycardLV(par5ItemStack) || ((TileEntityKeycardReader) par1World.getTileEntity(par2, par3, par4)).doesRequireExactKeycard() && Integer.parseInt(((IPasswordProtected) par1World.getTileEntity(par2, par3, par4)).getPassword()) == ((ItemKeycardBase) par5ItemStack.getItem()).getKeycardLV(par5ItemStack))){
			if(par5ItemStack.getItemDamage() == 3 && par5ItemStack.stackTagCompound != null && !par6EntityPlayer.capabilities.isCreativeMode){
				par5ItemStack.stackTagCompound.setInteger("Uses", par5ItemStack.stackTagCompound.getInteger("Uses") - 1);
				
				if(par5ItemStack.stackTagCompound.getInteger("Uses") <= 0){
					par5ItemStack.stackSize--;
				}
			}
			
			activate(par1World, par2, par3, par4);
		}else{
			if(((IPasswordProtected) par1World.getTileEntity(par2, par3, par4)).getPassword() != null){
				PlayerUtils.sendMessageToPlayer(par6EntityPlayer, StatCollector.translateToLocal("tile.keycardReader.name"), StatCollector.translateToLocal("messages.keycardReader.required").replace("#r", ((IPasswordProtected) par1World.getTileEntity(par2, par3, par4)).getPassword()).replace("#c", "" + ((ItemKeycardBase) par5ItemStack.getItem()).getKeycardLV(par5ItemStack)), EnumChatFormatting.RED);
			}else{
				PlayerUtils.sendMessageToPlayer(par6EntityPlayer, StatCollector.translateToLocal("tile.keycardReader.name"), StatCollector.translateToLocal("messages.keycardReader.notSet"), EnumChatFormatting.RED);
			}
		}
	}
	
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
    	if(!par1World.isRemote){
    		int meta = par1World.getBlockMetadata(par2, par3, par4);
    		
    		if(((IPasswordProtected) par1World.getTileEntity(par2, par3, par4)).getPassword() == null){    	
    			par5EntityPlayer.openGui(mod_SecurityCraft.instance, 4, par1World, par2, par3, par4);
    			return true;
    		}else if(meta >= 2 && meta <= 5 && par5EntityPlayer.getCurrentEquippedItem() != null && par5EntityPlayer.getCurrentEquippedItem().getItem() instanceof ItemKeycardBase){
    			insertCard(par1World, par2, par3, par4, par5EntityPlayer.getCurrentEquippedItem(), par5EntityPlayer);
    			return true;
    		}
    	}
    	
    	return true;
    }
    
    public static void activate(World par1World, int par2, int par3, int par4){
		par1World.setBlockMetadataWithNotify(par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4) + 5, 3);
		par1World.notifyBlocksOfNeighborChange(par2, par3, par4, mod_SecurityCraft.keycardReader);
		par1World.scheduleBlockUpdate(par2, par3, par4, mod_SecurityCraft.keycardReader, 60);
	}
    
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random){
    	if(!par1World.isRemote && par1World.getBlockMetadata(par2, par3, par4) > 6 && par1World.getBlockMetadata(par2, par3, par4) < 11){
			par1World.setBlockMetadataWithNotify(par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4) - 5, 3);
		}                       
	}
   
    public boolean canProvidePower(){
    	return true;
    }
    
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5){
    	if(par1IBlockAccess.getBlockMetadata(par2, par3, par4) > 6 && par1IBlockAccess.getBlockMetadata(par2, par3, par4) < 11){
    		return 15;
    	}else{
    		return 0;
    	}
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random){
    	int meta = par1World.getBlockMetadata(par2, par3, par4);
    	
        if(meta > 6 && meta < 11){
            double d0 = (double)((float)par2 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d1 = (double)((float)par3 + 0.7F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d2 = (double)((float)par4 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d3 = 0.2199999988079071D;
            double d4 = 0.27000001072883606D;
       
            par1World.spawnParticle("reddust", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
            par1World.spawnParticle("reddust", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D); 
            par1World.spawnParticle("reddust", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
            par1World.spawnParticle("reddust", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
            par1World.spawnParticle("reddust", d0, d1, d2, 0.0D, 0.0D, 0.0D);
        } 
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2){
        if(par1 == 3 && par2 == 0){
    		return this.keypadIconFront;
    	}
        
    	if(par2 == 7 || par2 == 8 || par2 == 9 || par2 == 10){
    		return par1 == 1 ? this.keypadIconTop : (par1 == 0 ? this.keypadIconTop : (par1 != (par2 - 5) ? this.blockIcon : this.keypadIconFront));
    	}else{
    		return par1 == 1 ? this.keypadIconTop : (par1 == 0 ? this.keypadIconTop : (par1 != par2 ? this.blockIcon : this.keypadIconFront));
    	}
    }

	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister){
    	this.blockIcon = par1IconRegister.registerIcon("furnace_side");
        this.keypadIconTop = par1IconRegister.registerIcon("furnace_top");
        this.keypadIconFront = par1IconRegister.registerIcon("securitycraft:keycardReaderFront");
    }
    
    public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityKeycardReader();
    }
	
}
