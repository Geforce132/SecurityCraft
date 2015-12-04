package net.geforcemods.securitycraft.blocks;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntityPortableRadar;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
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
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortableRadar extends BlockContainer {
	
	public static final PropertyBool POWERED = PropertyBool.create("powered");
	
	public BlockPortableRadar(Material par2Material) {
		super(par2Material);
		this.setBlockBounds(0.3F, 0.0F, 0.3F, 0.7F, 0.45F, 0.7F);	
	}
	
	/**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube(){
        return false;
    }
    
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean isNormalCube(){
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
            double d0 = (double)(mod_SecurityCraft.configHandler.portableRadarSearchRadius);
        	
            AxisAlignedBB axisalignedbb = AxisAlignedBB.fromBounds((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)).expand(d0, d0, d0).addCoord(0.0D, (double) par1World.getHeight(), 0.0D);
            List<?> list = par1World.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
            Iterator<?> iterator = list.iterator();
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
            	EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(((TileEntityPortableRadar)par1World.getTileEntity(pos)).getOwner().getName());            
                
                entityplayer = (EntityPlayer)iterator.next();
                
                if(entityplayermp != null && ((CustomizableSCTE) par1World.getTileEntity(pos)).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(par1World, pos, EnumCustomModules.WHITELIST).contains(entityplayermp.getName().toLowerCase())){ continue; }              
                
                if(this.isOwnerOnline(((TileEntityPortableRadar)par1World.getTileEntity(pos)).getOwner().getName())){
                	PlayerUtils.sendMessageToPlayer(entityplayermp, StatCollector.translateToLocal("tile.portableRadar.name"), ((TileEntityPortableRadar)par1World.getTileEntity(pos)).hasCustomName() ? (StatCollector.translateToLocal("messages.portableRadar.withName").replace("#p", EnumChatFormatting.ITALIC + entityplayer.getName() + EnumChatFormatting.RESET).replace("#n", EnumChatFormatting.ITALIC + ((TileEntityPortableRadar)par1World.getTileEntity(pos)).getCustomName() + EnumChatFormatting.RESET)) : (StatCollector.translateToLocal("messages.portableRadar.withoutName").replace("#p", EnumChatFormatting.ITALIC + entityplayer.getName() + EnumChatFormatting.RESET).replace("#l", Utils.getFormattedCoordinates(pos))), EnumChatFormatting.BLUE);               
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
    		BlockUtils.setBlockProperty(par1World, pos, POWERED, true, true);
    		BlockUtils.updateAndNotify(par1World, pos, BlockUtils.getBlock(par1World, pos), 1, false);
		}else if(!par5 && ((Boolean) par1World.getBlockState(pos).getValue(POWERED)).booleanValue()){
			BlockUtils.setBlockProperty(par1World, pos, POWERED, false, true);
			BlockUtils.updateAndNotify(par1World, pos, BlockUtils.getBlock(par1World, pos), 1, false);
		}
	}     

	public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
		((TileEntityOwnable) par1World.getTileEntity(pos)).getOwner().set(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getName());       
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
		return new TileEntityPortableRadar().attacks(EntityPlayer.class, mod_SecurityCraft.configHandler.portableRadarSearchRadius, mod_SecurityCraft.configHandler.portableRadarDelay);
	}

}
