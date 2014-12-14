package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;
import org.freeforums.geforce.securitycraft.tileentity.CustomizableSCTE;

public class BlockLaser extends Block{
	
	public static final PropertyInteger BOUNDTYPE = PropertyInteger.create("boundtype", 1, 3);

	public BlockLaser() {
		super(Material.circuits);
		this.setBlockBounds(0.250F, 0.300F, 0.300F, 0.750F, 0.700F, 0.700F);

	}
	
	public AxisAlignedBB getCollisionBoundingBox(World par1World, BlockPos pos, IBlockState state)
    {
        return null;
    }
	
	public boolean isOpaqueCube(){
		return false;	
	}
	
	/**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean isNormalCube()
    {
        return false;
    }
    
    
    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void onEntityCollidedWithBlock(World par1World, BlockPos pos, Entity par5Entity) {
        if(!par1World.isRemote && par5Entity instanceof EntityLivingBase && !HelpfulMethods.doesMobHavePotionEffect((EntityLivingBase) par5Entity, Potion.invisibility)){	
			for(int i = 1; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
				Block id = Utils.getBlock(par1World, pos.east(i));
				if(id == mod_SecurityCraft.LaserBlock){
					if(((CustomizableSCTE) par1World.getTileEntity(pos.east(i))).hasModule(EnumCustomModules.WHITELIST) && HelpfulMethods.getPlayersFromModule(par1World, pos.east(i), EnumCustomModules.WHITELIST).contains(((EntityLivingBase) par5Entity).getName())){ return; }
					Utils.setBlockProperty(par1World, pos.east(i), BlockLaserBlock.POWERED, true);
					par1World.notifyNeighborsOfStateChange(pos.east(i), mod_SecurityCraft.LaserBlock);
					par1World.scheduleUpdate(pos.east(i), mod_SecurityCraft.LaserBlock, 50);
					
					if(par1World.getTileEntity(pos.east(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) par1World.getTileEntity(pos.east(i))).hasModule(EnumCustomModules.HARMING)){
						((EntityLivingBase) par5Entity).attackEntityFrom(DamageSource.generic, 10F);
					}
				}else{
					continue;
				}
			}
			
			for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
				Block id = Utils.getBlock(par1World, pos.west(i));
				if(id == mod_SecurityCraft.LaserBlock){
					if(((CustomizableSCTE) par1World.getTileEntity(pos.west(i))).hasModule(EnumCustomModules.WHITELIST) && HelpfulMethods.getPlayersFromModule(par1World, pos.west(i), EnumCustomModules.WHITELIST).contains(((EntityLivingBase) par5Entity).getName())){ return; }
					Utils.setBlockProperty(par1World, pos.west(i), BlockLaserBlock.POWERED, true);
					par1World.notifyNeighborsOfStateChange(pos.west(i), mod_SecurityCraft.LaserBlock);
					par1World.scheduleUpdate(pos.west(i), mod_SecurityCraft.LaserBlock, 50);

					if(par1World.getTileEntity(pos.west(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) par1World.getTileEntity(pos.west(i))).hasModule(EnumCustomModules.HARMING)){
						((EntityLivingBase) par5Entity).attackEntityFrom(DamageSource.generic, 10F);
					}
				}else{
					continue;
				}
			}
			
			for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
				Block id = Utils.getBlock(par1World, pos.south(i));
				if(id == mod_SecurityCraft.LaserBlock){
					if(((CustomizableSCTE) par1World.getTileEntity(pos.south(i))).hasModule(EnumCustomModules.WHITELIST) && HelpfulMethods.getPlayersFromModule(par1World, pos.south(i), EnumCustomModules.WHITELIST).contains(((EntityLivingBase) par5Entity).getName())){ return; }
					Utils.setBlockProperty(par1World, pos.south(i), BlockLaserBlock.POWERED, true);
					par1World.notifyNeighborsOfStateChange(pos.south(i), mod_SecurityCraft.LaserBlock);
					par1World.scheduleUpdate(pos.south(i), mod_SecurityCraft.LaserBlock, 50);

					if(par1World.getTileEntity(pos.south(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) par1World.getTileEntity(pos.south(i))).hasModule(EnumCustomModules.HARMING)){
						((EntityLivingBase) par5Entity).attackEntityFrom(DamageSource.generic, 10F);
					}
				}else{
					continue;
				}
			}
			
			for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
				Block id = Utils.getBlock(par1World, pos.north(i));
				if(id == mod_SecurityCraft.LaserBlock){
					if(((CustomizableSCTE) par1World.getTileEntity(pos.north(i))).hasModule(EnumCustomModules.WHITELIST) && HelpfulMethods.getPlayersFromModule(par1World, pos.north(i), EnumCustomModules.WHITELIST).contains(((EntityLivingBase) par5Entity).getName())){ return; }
					Utils.setBlockProperty(par1World, pos.north(i), BlockLaserBlock.POWERED, true);
					par1World.notifyNeighborsOfStateChange(pos.north(i), mod_SecurityCraft.LaserBlock);
					par1World.scheduleUpdate(pos.north(i), mod_SecurityCraft.LaserBlock, 50);

					if(par1World.getTileEntity(pos.north(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) par1World.getTileEntity(pos.north(i))).hasModule(EnumCustomModules.HARMING)){
						((EntityLivingBase) par5Entity).attackEntityFrom(DamageSource.generic, 10F);
					}
				}else{
					continue;
				}
			}
			
			for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
				Block id = Utils.getBlock(par1World, pos.up(i));
				if(id == mod_SecurityCraft.LaserBlock){
					if(((CustomizableSCTE) par1World.getTileEntity(pos.up(i))).hasModule(EnumCustomModules.WHITELIST) && HelpfulMethods.getPlayersFromModule(par1World, pos.up(i), EnumCustomModules.WHITELIST).contains(((EntityLivingBase) par5Entity).getName())){ return; }
					Utils.setBlockProperty(par1World, pos.up(i), BlockLaserBlock.POWERED, true);
					par1World.notifyNeighborsOfStateChange(pos.up(i), mod_SecurityCraft.LaserBlock);
					par1World.scheduleUpdate(pos.up(i), mod_SecurityCraft.LaserBlock, 50);

					if(par1World.getTileEntity(pos.up(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) par1World.getTileEntity(pos.up(i))).hasModule(EnumCustomModules.HARMING)){
						((EntityLivingBase) par5Entity).attackEntityFrom(DamageSource.generic, 10F);
					}
				}else{
					continue;
				}
			}
			
			for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
				Block id = Utils.getBlock(par1World, pos.down(i));
				if(id == mod_SecurityCraft.LaserBlock){
					if(((CustomizableSCTE) par1World.getTileEntity(pos.down(i))).hasModule(EnumCustomModules.WHITELIST) && HelpfulMethods.getPlayersFromModule(par1World, pos.down(i), EnumCustomModules.WHITELIST).contains(((EntityLivingBase) par5Entity).getName())){ return; }
					Utils.setBlockProperty(par1World, pos.down(i), BlockLaserBlock.POWERED, true);
					par1World.notifyNeighborsOfStateChange(pos.down(i), mod_SecurityCraft.LaserBlock);
					par1World.scheduleUpdate(pos.down(i), mod_SecurityCraft.LaserBlock, 50);

					if(par1World.getTileEntity(pos.down(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) par1World.getTileEntity(pos.down(i))).hasModule(EnumCustomModules.HARMING)){
						((EntityLivingBase) par5Entity).attackEntityFrom(DamageSource.generic, 10F);
					}
				}else{
					continue;
				}
			}
        }
    }
    
    
    /**
     * Called right before the block is destroyed by a player.  Args: world, pos, state
     */
    @SuppressWarnings("static-access")
	public void onBlockDestroyedByPlayer(World par1World, BlockPos pos, IBlockState state)
    {
    	if(!par1World.isRemote){
    		for(int i = 1; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = Utils.getBlock(par1World, pos.east(i));
    			if(id == mod_SecurityCraft.LaserBlock || id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.destroyBlock(pos.east(j), false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = Utils.getBlock(par1World, pos.west(i));
    			if(id == mod_SecurityCraft.LaserBlock || id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.destroyBlock(pos.west(j), false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = Utils.getBlock(par1World, pos.south(i));
    			if(id == mod_SecurityCraft.LaserBlock || id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.destroyBlock(pos.south(j), false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = Utils.getBlock(par1World, pos.north(i));
    			if(id == mod_SecurityCraft.LaserBlock || id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.destroyBlock(pos.north(j), false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = Utils.getBlock(par1World, pos.up(i));
    			if(id == mod_SecurityCraft.LaserBlock || id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.destroyBlock(pos.up(j), false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = Utils.getBlock(par1World, pos.down(i));
    			if(id == mod_SecurityCraft.LaserBlock || id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.destroyBlock(pos.down(j), false);
    				}
    			}else{
    				continue;
    			}
    		}
    	}
    }
    
    /**
     * Updates the blocks bounds based on its current state. Args: world, pos, state
     */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, BlockPos pos)
    {
        if (((Integer) par1IBlockAccess.getBlockState(pos).getValue(BOUNDTYPE)).intValue() == 1)
        {
    		this.setBlockBounds(0.250F, 0.000F, 0.300F, 0.750F, 1.000F, 0.700F);
        }
        else if (((Integer) par1IBlockAccess.getBlockState(pos).getValue(BOUNDTYPE)).intValue() == 2)
        {

    		this.setBlockBounds(0.250F, 0.300F, 0.000F, 0.750F, 0.700F, 1.000F);
        }
        else if (((Integer) par1IBlockAccess.getBlockState(pos).getValue(BOUNDTYPE)).intValue() == 3)
        {

    		this.setBlockBounds(0.000F, 0.300F, 0.300F, 1.000F, 0.700F, 0.700F);
        }
        else
        {
    		this.setBlockBounds(0.250F, 0.300F, 0.300F, 0.750F, 0.700F, 0.700F);
        }
    } 
    
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(BOUNDTYPE, meta);
    }

    public int getMetaFromState(IBlockState state)
    {
        return ((Integer) state.getValue(BOUNDTYPE)).intValue();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {BOUNDTYPE});
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public Item getItem(World par1World, BlockPos pos)
    {
        return null;
    }

}
