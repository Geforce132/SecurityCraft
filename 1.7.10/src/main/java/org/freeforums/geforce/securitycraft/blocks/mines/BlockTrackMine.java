package org.freeforums.geforce.securitycraft.blocks.mines;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTrackMine extends BlockRailBase{

	@SideOnly(Side.CLIENT)
    private IIcon theIcon;
	
	public BlockTrackMine() {
		super(false);
	}
	
	public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z)
    {
		HelpfulMethods.destroyBlock(world, x, y, z, false);
        
        if(mod_SecurityCraft.configHandler.smallerMineExplosion){
            world.createExplosion(cart, x, y + 1, z, 4.0F, true);
        }else{
            world.createExplosion(cart, x, y + 1, z, 8.0F, true);
        }
        
		cart.setDead();
    }    

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public IIcon getIcon(int par1, int par2)
    {
        return par2 >= 6 ? this.theIcon : this.blockIcon;
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        super.registerBlockIcons(par1IconRegister);
        this.theIcon = par1IconRegister.registerIcon("securitycraft:rail_mineTurned");
    }

    protected void func_150048_a(World par1World, int par2, int par3, int par4, int par5, int par6, Block par7)
    {
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
	


}
