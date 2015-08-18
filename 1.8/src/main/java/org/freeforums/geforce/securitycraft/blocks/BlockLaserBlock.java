package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import org.freeforums.geforce.securitycraft.main.Utils.BlockUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityLaserBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("static-access")
public class BlockLaserBlock extends BlockOwnable {

	public static final PropertyBool POWERED = PropertyBool.create("powered");
	    
	public BlockLaserBlock(Material par2Material) {
		super(par2Material);
	}
	
	public int getRenderType(){
		return 3;
	}
	
	/**
     * Called whenever the block is added into the world. Args: world, pos
     */
    public void onBlockAdded(World par1World, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(par1World, pos, state);
    }
    
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
    	super.onBlockPlacedBy(par1World, pos, state, par5EntityLivingBase, par6ItemStack);
    	
        if(!par1World.isRemote){
        	this.setLaser(par1World, pos);
        }
    }

    private void setLaser(World par1World, BlockPos pos) {
		for(int i = 1; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlockState(pos.east(i)).getBlock();
			if(id != Blocks.air && id != mod_SecurityCraft.LaserBlock){ break; }
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlockState(pos.east(j)).getBlock() == Blocks.air){
						par1World.setBlockState(pos.east(j), mod_SecurityCraft.Laser.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 3));
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlockState(new BlockPos(pos.west(i))).getBlock();
			if(id != Blocks.air && id != mod_SecurityCraft.LaserBlock){ break; }
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlockState(pos.west(j)).getBlock() == Blocks.air){
						par1World.setBlockState(pos.west(j), mod_SecurityCraft.Laser.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 3));
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlockState(pos.south(i)).getBlock();
			if(id != Blocks.air && id != mod_SecurityCraft.LaserBlock){ break; }
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlockState(pos.south(j)).getBlock() == Blocks.air){
						par1World.setBlockState(pos.south(j), mod_SecurityCraft.Laser.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 2));
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlockState(pos.north(i)).getBlock();
			if(id != Blocks.air && id != mod_SecurityCraft.LaserBlock){ break; }
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlockState(pos.north(j)).getBlock() == Blocks.air){
						par1World.setBlockState(pos.north(j), mod_SecurityCraft.Laser.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 2));
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlockState(pos.up(i)).getBlock();
			if(id != Blocks.air && id != mod_SecurityCraft.LaserBlock){ break; }
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlockState(pos.up(j)).getBlock() == Blocks.air){
						par1World.setBlockState(pos.up(j), mod_SecurityCraft.Laser.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 1));
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlockState(pos.down(i)).getBlock();
			if(id != Blocks.air && id != mod_SecurityCraft.LaserBlock){ break; }
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlockState(pos.down(j)).getBlock() == Blocks.air){
						par1World.setBlockState(pos.down(j), mod_SecurityCraft.Laser.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 1));
					}
				}
			}else{
				continue;
			}
		}
	}
    
    /**
     * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
     */
    public void onBlockDestroyedByPlayer(World par1World, BlockPos pos, IBlockState state) {
    	if(!par1World.isRemote){
    		destroyAdjecentLasers(par1World, pos.getX(), pos.getY(), pos.getZ());
    	}
    }
    
    public static void destroyAdjecentLasers(World par1World, int par2, int par3, int par4){
    	for(int i = 1; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(par1World, par2 + i, par3, par4);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(BlockUtils.getBlock(par1World, par2 + j, par3, par4) == mod_SecurityCraft.Laser){
						par1World.destroyBlock(new BlockPos(par2 + j, par3, par4), false);
					}else{
						return;
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(par1World, par2 - i, par3, par4);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(BlockUtils.getBlock(par1World, par2 - j, par3, par4) == mod_SecurityCraft.Laser){
						par1World.destroyBlock(new BlockPos(par2 - j, par3, par4), false);
					}else{
						return;
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(par1World, par2, par3, par4 + i);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(BlockUtils.getBlock(par1World, par2, par3, par4 + j) == mod_SecurityCraft.Laser){
						par1World.destroyBlock(new BlockPos(par2, par3, par4 + j), false);
					}else{
						return;
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(par1World, par2 , par3, par4 - i);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(BlockUtils.getBlock(par1World, par2, par3, par4 - j) == mod_SecurityCraft.Laser){
						par1World.destroyBlock(new BlockPos(par2, par3, par4 - j), false);
					}else{
						return;
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(par1World, par2, par3 + i, par4);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(BlockUtils.getBlock(par1World, par2, par3 + j, par4) == mod_SecurityCraft.Laser){
						par1World.destroyBlock(new BlockPos(par2, par3 + j, par4), false);
					}else{
						return;
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(par1World, par2, par3 - i, par4);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(BlockUtils.getBlock(par1World, par2, par3 - j, par4) == mod_SecurityCraft.Laser){
						par1World.destroyBlock(new BlockPos(par2, par3 - j, par4), false);
					}else{
						return;
					}
				}
			}else{
				continue;
			}
		}
    }
    
    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side){
    	if(((Boolean) state.getValue(POWERED)).booleanValue()){
    		return 15;
    	}else{
    		return 0;
    	}
    }
    
    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side){   	
    	if(((Boolean) state.getValue(POWERED)).booleanValue()){
    		return 15;
    	}else{
    		return 0;
    	}
    }
    
    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, BlockPos pos, IBlockState state, Random par5Random){
        if (!par1World.isRemote && ((Boolean) state.getValue(POWERED)).booleanValue()){
        	BlockUtils.setBlockProperty(par1World, pos, POWERED, false, true);
        }                      
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, BlockPos pos, IBlockState state, Random par5Random){      
            if(((Boolean) state.getValue(POWERED)).booleanValue()){
            double d0 = (double)((float)pos.getX() + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d1 = (double)((float)pos.getY() + 0.7F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d2 = (double)((float)pos.getZ() + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d3 = 0.2199999988079071D;
            double d4 = 0.27000001072883606D;

            
            par1World.spawnParticle(EnumParticleTypes.REDSTONE, d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
            par1World.spawnParticle(EnumParticleTypes.REDSTONE, d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D); 
            par1World.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
            par1World.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
            par1World.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
        
    }
    
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(POWERED, meta == 1 ? true : false);
    }

    public int getMetaFromState(IBlockState state)
    {
        return (((Boolean) state.getValue(POWERED)).booleanValue() ? 1 : 0);
    }
    
    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {POWERED});
    }

	public TileEntity createNewTileEntity(World par1World, int par2) {
		return new TileEntityLaserBlock();
	}

	public String[] getRecipe() {
		return new String[]{"The laser block requires: 7 stone, 1 block of redstone, 1 glass pane", "XXX", "XYX", "XZX", "X = stone, Y = block of redstone, Z = glass pane"};
	}

}
