package org.freeforums.geforce.securitycraft.blocks;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.Validate;
import org.freeforums.geforce.securitycraft.enums.EnumCustomModules;
import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.CustomizableSCTE;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityPortableRadar;

public class BlockPortableRadar extends BlockContainer implements IHelpInfo {
	
	public static final PropertyBool POWERED = PropertyBool.create("powered");
	
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
    public boolean isNormalCube()
    {
        return false;
    }
    
    public int getRenderType(){
    	return 3;
    }
     
    public void updateTick(World par1World, BlockPos pos, IBlockState state, Random par5Random){
        this.addEffectsToPlayers(par1World, pos, state); 
    }
    
    public void addEffectsToPlayers(World par1World, BlockPos pos, IBlockState state){
        if (par1World.isRemote){      	
        	return;       	
        }else{
            //double d0 = (double)(5 * 10);
            double d0 = (double)(mod_SecurityCraft.configHandler.portableRadarSearchRadius);
        	
            AxisAlignedBB axisalignedbb = AxisAlignedBB.fromBounds((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)).expand(d0, d0, d0).addCoord(0.0D, (double) par1World.getHeight(), 0.0D);
            List list = par1World.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
            Iterator iterator = list.iterator();
            EntityPlayer entityplayer;                 
            
            if(list.isEmpty()){
            	if(par1World.getTileEntity(pos) != null && par1World.getTileEntity(pos) instanceof TileEntityPortableRadar && ((CustomizableSCTE) par1World.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE) && ((Boolean) state.getValue(POWERED)).booleanValue()){
            		this.togglePowerOutput(par1World, pos, false);
            		return;
                }
            }
            
            if(!((CustomizableSCTE) par1World.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE)){
            	this.togglePowerOutput(par1World, pos, false);
            }

            while (iterator.hasNext()){      
            	EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(((TileEntityPortableRadar)par1World.getTileEntity(pos)).getUsername());            
                
                entityplayer = (EntityPlayer)iterator.next();
                
                if(entityplayermp != null && ((CustomizableSCTE) par1World.getTileEntity(pos)).hasModule(EnumCustomModules.WHITELIST) && HelpfulMethods.getPlayersFromModule(par1World, pos, EnumCustomModules.WHITELIST).contains(entityplayermp.getName().toLowerCase())){ continue; }              
                
                if(this.isOwnerOnline(((TileEntityPortableRadar)par1World.getTileEntity(pos)).getUsername())){
                	HelpfulMethods.sendMessageToPlayer(entityplayermp, ((TileEntityPortableRadar)par1World.getTileEntity(pos)).hasCustomName() ? (EnumChatFormatting.ITALIC + entityplayer.getName() + EnumChatFormatting.RESET +" is near your portable radar named " + EnumChatFormatting.ITALIC + ((TileEntityPortableRadar)par1World.getTileEntity(pos)).getCustomName() + EnumChatFormatting.RESET + ".") : (EnumChatFormatting.ITALIC + entityplayer.getName() + EnumChatFormatting.RESET + " is near a portable radar (at " + Utils.getFormattedCoordinates(pos) + ")."), null);               
                }   
                
                if(par1World.getTileEntity(pos) != null && par1World.getTileEntity(pos) instanceof TileEntityPortableRadar && ((CustomizableSCTE) par1World.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE)){
                	this.togglePowerOutput(par1World, pos, true);
                }
            }
            
            

        }
    }

	private boolean isOwnerOnline(String username) {
    	if(MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(username) != null){
    		return true;
    	}else{
    		return false;
    	}
    		
    	
    }

    private void togglePowerOutput(World par1World, BlockPos pos, boolean par5) {
    	if(par5 && !((Boolean) par1World.getBlockState(pos).getValue(POWERED)).booleanValue()){
			Utils.setBlockProperty(par1World, pos, POWERED, true, true);
			HelpfulMethods.updateAndNotify(par1World, pos, Utils.getBlock(par1World, pos), 1, false);
		}else if(!par5 && ((Boolean) par1World.getBlockState(pos).getValue(POWERED)).booleanValue()){
			Utils.setBlockProperty(par1World, pos, POWERED, false, true);
			HelpfulMethods.updateAndNotify(par1World, pos, Utils.getBlock(par1World, pos), 1, false);
		}
	}     

	public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
    	((TileEntityPortableRadar)par1World.getTileEntity(pos)).setUsername(((EntityPlayer)par5EntityLivingBase).getName());
		((TileEntityOwnable) par1World.getTileEntity(pos)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getName());       
	}
	
    public boolean canProvidePower()
    {
        return true;
    }
    
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side){
    	if(((Boolean) state.getValue(POWERED)).booleanValue()){
    		return 15;
    	}else{
    		return 0;
    	}
    }
    
    public IBlockState getStateFromMeta(int meta)
    {   
        return meta == 1 ? this.getDefaultState().withProperty(POWERED, true) : this.getDefaultState().withProperty(POWERED, false);    
    }

    public int getMetaFromState(IBlockState state)
    {
        return ((Boolean) state.getValue(POWERED)).booleanValue() ? 1 : 0;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {POWERED});
    }

	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityPortableRadar();
	}

	public String getHelpInfo() {
		return "The portable radar will send the owner a chat message whenever a player is inside of the radar's detection radius (modifiable in the config file). You can name the portable radar by right-clicking on it with a named name-tag.";
	}

	public String[] getRecipe() {
		return new String[]{"The portable radar requires: 7 iron ingots, 1 redstone torch, 1 redstone", "XXX", "XYX", "XZX", "X = iron ingot, Y = redstone torch, Z = redstone"};
	}

}
