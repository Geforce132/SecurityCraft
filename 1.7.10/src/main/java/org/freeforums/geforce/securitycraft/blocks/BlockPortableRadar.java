package org.freeforums.geforce.securitycraft.blocks;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityPortableRadar;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPortableRadar extends BlockContainer{

	@SideOnly(Side.CLIENT)
	private IIcon topIcon;
	
	@SideOnly(Side.CLIENT)
	private IIcon sidesIcon;
	
	
	public BlockPortableRadar(Material par2Material) {
		super(par2Material);
		this.setBlockBounds(0.3F, 0.0F, 0.3F, 0.7F, 0.45F, 0.7F);	
	}
	
	/**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }
     
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random){
        this.addEffectsToPlayers(par1World, par2, par3, par4); 
    }
    
    public void addEffectsToPlayers(World par1World, int par2, int par3, int par4)
    {
        if (par1World.isRemote){
        	
        	return;
        	
        }else{
            //double d0 = (double)(5 * 10);
            double d0 = (double)(mod_SecurityCraft.configHandler.portableRadarSearchRadius);
        	
            AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox((double)par2, (double)par3, (double)par4, (double)(par2 + 1), (double)(par3 + 1), (double)(par4 + 1)).expand(d0, d0, d0);
            axisalignedbb.maxY = (double)par1World.getHeight();
            List list = par1World.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
            Iterator iterator = list.iterator();
            EntityPlayer entityplayer;
            
            while (iterator.hasNext())
            {
            	
            	EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().func_152612_a(((TileEntityPortableRadar)par1World.getTileEntity(par2, par3, par4)).getUsername());
                
                
                entityplayer = (EntityPlayer)iterator.next();
                
                if(this.isOwnerOnline(((TileEntityPortableRadar)par1World.getTileEntity(par2, par3, par4)).getUsername())){
                	if(!((TileEntityPortableRadar)par1World.getTileEntity(par2, par3, par4)).isEmped()){
                		HelpfulMethods.sendMessageToPlayer(entityplayermp, ((TileEntityPortableRadar)par1World.getTileEntity(par2, par3, par4)).hasCustomName() ? (EnumChatFormatting.ITALIC + entityplayer.getCommandSenderName() + EnumChatFormatting.RESET +" is near your portable radar named " + EnumChatFormatting.ITALIC + ((TileEntityPortableRadar)par1World.getTileEntity(par2, par3, par4)).getCustomName() + EnumChatFormatting.RESET + ".") : (EnumChatFormatting.ITALIC + entityplayer.getCommandSenderName() + EnumChatFormatting.RESET + " is near a portable radar (at X: " + par2 + " Y:" + par3 + " Z:" + par4 + ")."), null);
                	}else{
                		HelpfulMethods.sendMessageToPlayer(entityplayermp, "xxxxxxxxxx",  EnumChatFormatting.OBFUSCATED);
                	}
                }

            }

        }
    }

	
    private boolean isOwnerOnline(String username) {
    	if(MinecraftServer.getServer().getConfigurationManager().func_152612_a(username) != null){
    		return true;
    	}else{
    		return false;
    	}
    		
    	
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister){
    	this.sidesIcon = par1IconRegister.registerIcon("securitycraft:portableRadarSides");
    	this.topIcon = par1IconRegister.registerIcon("securitycraft:portableRadarTop1");
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2){
    	return par1 == 1 ? topIcon : sidesIcon;
    }


	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
    	((TileEntityPortableRadar)par1World.getTileEntity(par2, par3, par4)).setUsername(((EntityPlayer)par5EntityLivingBase).getCommandSenderName());
    }


	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityPortableRadar();
	}

}
