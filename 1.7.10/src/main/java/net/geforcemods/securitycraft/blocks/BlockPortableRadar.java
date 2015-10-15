package net.geforcemods.securitycraft.blocks;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.geforcemods.securitycraft.main.Utils.ModuleUtils;
import net.geforcemods.securitycraft.main.Utils.PlayerUtils;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntityPortableRadar;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortableRadar extends BlockContainer {

	@SideOnly(Side.CLIENT)
	private IIcon topIcon;
	
	@SideOnly(Side.CLIENT)
	private IIcon sidesIcon;
	
	
	public BlockPortableRadar(Material par2Material) {
		super(par2Material);
		this.setBlockBounds(0.3F, 0.0F, 0.3F, 0.7F, 0.45F, 0.7F);	
	}
	
    public boolean isOpaqueCube(){
        return false;
    }
    
    public boolean renderAsNormalBlock(){
        return false;
    }
    
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
        ((TileEntityOwnable) par1World.getTileEntity(par2, par3, par4)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());
		((TileEntityPortableRadar)par1World.getTileEntity(par2, par3, par4)).setUsername(((EntityPlayer)par5EntityLivingBase).getCommandSenderName());
    }
     
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random){
        this.addEffectsToPlayers(par1World, par2, par3, par4); 
    }
    
    public void addEffectsToPlayers(World par1World, int par2, int par3, int par4){
        if(!par1World.isRemote){	
            double d0 = (double)(mod_SecurityCraft.configHandler.portableRadarSearchRadius);
        	
            AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox((double)par2, (double)par3, (double)par4, (double)(par2 + 1), (double)(par3 + 1), (double)(par4 + 1)).expand(d0, d0, d0);
            axisalignedbb.maxY = (double)par1World.getHeight();
            List list = par1World.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
            Iterator iterator = list.iterator();
            EntityPlayer entityplayer;
            
            if(list.isEmpty()){
            	if(par1World.getTileEntity(par2, par3, par4) != null && par1World.getTileEntity(par2, par3, par4) instanceof TileEntityPortableRadar && ((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4)).hasModule(EnumCustomModules.REDSTONE) && par1World.getBlockMetadata(par2, par3, par4) == 1){
            		this.togglePowerOutput(par1World, par2, par3, par4, false);
            		return;
                }
            }

            while (iterator.hasNext()){      
            	EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().func_152612_a(((TileEntityPortableRadar)par1World.getTileEntity(par2, par3, par4)).getUsername());            
                
                entityplayer = (EntityPlayer)iterator.next();
                
                if(par1World.getTileEntity(par2, par3, par4) == null || !(par1World.getTileEntity(par2, par3, par4) instanceof CustomizableSCTE)){ continue; }
                
                if(((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4)).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(par1World, par2, par3, par4, EnumCustomModules.WHITELIST).contains(entityplayermp.getCommandSenderName().toLowerCase())){ continue; }              
                
                if(PlayerUtils.isPlayerOnline(((TileEntityPortableRadar)par1World.getTileEntity(par2, par3, par4)).getUsername())){
                	PlayerUtils.sendMessageToPlayer(entityplayermp, "Portable Radar", ((TileEntityPortableRadar)par1World.getTileEntity(par2, par3, par4)).hasCustomName() ? (EnumChatFormatting.ITALIC + entityplayer.getCommandSenderName() + EnumChatFormatting.RESET +" is near your portable radar named " + EnumChatFormatting.ITALIC + ((TileEntityPortableRadar)par1World.getTileEntity(par2, par3, par4)).getCustomName() + EnumChatFormatting.RESET + ".") : (EnumChatFormatting.ITALIC + entityplayer.getCommandSenderName() + EnumChatFormatting.RESET + " is near a portable radar (at X: " + par2 + " Y:" + par3 + " Z:" + par4 + ")."), null);         
                }   
                
                if(par1World.getTileEntity(par2, par3, par4) != null && par1World.getTileEntity(par2, par3, par4) instanceof TileEntityPortableRadar && ((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4)).hasModule(EnumCustomModules.REDSTONE)){
                	this.togglePowerOutput(par1World, par2, par3, par4, true);
                }
            }     
        }
    }

    private void togglePowerOutput(World par1World, int par2, int par3, int par4, boolean par5) {
		if(par5){
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 1, 3);
		}else{
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 0, 3);
		}
		
		BlockUtils.updateAndNotify(par1World, par2, par3, par4, par1World.getBlock(par2, par3, par4), 1, false);
	}  
   
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5){
    	if(par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 1){
    		return 15;
    	}else{
    		return 0;
    	}
    }
	
    public boolean canProvidePower(){
        return true;
    }
  
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2){
    	return par1 == 1 ? topIcon : sidesIcon;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister){
    	this.sidesIcon = par1IconRegister.registerIcon("securitycraft:portableRadarSides");
    	this.topIcon = par1IconRegister.registerIcon("securitycraft:portableRadarTop1");
    }
    
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityPortableRadar();
	}
	
}
