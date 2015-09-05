package net.breakinbad.securitycraft.blocks.mines;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.breakinbad.securitycraft.api.IExplosive;
import net.breakinbad.securitycraft.main.Utils.BlockUtils;
import net.breakinbad.securitycraft.main.mod_SecurityCraft;
import net.breakinbad.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockTrackMine extends BlockRailBase implements IExplosive, ITileEntityProvider {

	@SideOnly(Side.CLIENT)
    private IIcon theIcon;
	
	public BlockTrackMine() {
		super(false);
	}
	
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
    	if(!par1World.isRemote){
    		if(par5EntityLivingBase instanceof EntityPlayer){
    			((TileEntityOwnable) par1World.getTileEntity(par2, par3, par4)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());
    		}
    	}
    }
	
	public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z){
		BlockUtils.destroyBlock(world, x, y, z, false);
        
        world.createExplosion(cart, x, y + 1, z, mod_SecurityCraft.configHandler.smallerMineExplosion ? 4.0F : 8.0F, true);
        
		cart.setDead();
    }    
	
	public void explode(World world, int par2, int par3, int par4) {
		BlockUtils.destroyBlock(world, par2, par3, par4, false);
        world.createExplosion((Entity) null, par2, par3 + 1, par4, mod_SecurityCraft.configHandler.smallerMineExplosion ? 4.0F : 8.0F, true);  
	}
	
	public void breakBlock(World par1World, int par2, int par3, int par4, Block par5Block, int par6){
        super.breakBlock(par1World, par2, par3, par4, par5Block, par6);
        par1World.removeTileEntity(par2, par3, par4);
    }

    protected void func_150048_a(World par1World, int par2, int par3, int par4, int par5, int par6, Block par7){
    	try{
    		BlockRailBase.Rail rail = new BlockRailBase.Rail(par1World, par2, par3, par4);
    		Method method = rail.getClass().getDeclaredMethod("func_150650_a");
		
    		method.setAccessible(true);
    		int number = (Integer) method.invoke(rail);
		
    		if (par7.canProvidePower() && number == 3)
    		{
    			this.func_150052_a(par1World, par2, par3, par4, false);
    		}
    		
    	}catch(IllegalAccessException e){
			e.printStackTrace();
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}catch(InvocationTargetException e) {
			e.printStackTrace();
		}catch(NoSuchMethodException e){
			e.printStackTrace();
		}catch(SecurityException e){
			e.printStackTrace();
		}
    }
    
	public void activateMine(World world, int par2, int par3, int par4) {}

	public void defuseMine(World world, int par2, int par3, int par4) {}

	public boolean isActive(World world, int par2, int par3, int par4) {
		return true;
	}

	public boolean isDefusable() {
		return false;
	}

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2){
        return par2 >= 6 ? this.theIcon : this.blockIcon;
    }

   
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister){
        super.registerBlockIcons(par1IconRegister);
        this.theIcon = par1IconRegister.registerIcon("securitycraft:rail_mineTurned");
    }

	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityOwnable();
	}

}
