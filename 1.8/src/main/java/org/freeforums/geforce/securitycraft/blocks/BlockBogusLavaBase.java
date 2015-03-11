package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.interfaces.IIntersectable;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityIntersectable;

public class BlockBogusLavaBase extends BlockStaticLiquid implements IIntersectable{

	public BlockBogusLavaBase(Material p_i45429_1_){
		super(p_i45429_1_);
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	public void onEntityCollidedWithBlock(World par1World, BlockPos pos, Entity par5Entity){
		if(!par1World.isRemote){

			if(par5Entity instanceof EntityPlayer){
				((EntityPlayer) par5Entity).heal(4);
				((EntityPlayer) par5Entity).extinguish();
			}
		}
	}

	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(!world.isRemote){
			if(entity instanceof EntityPlayer){
				((EntityPlayer) entity).heal(4);
				((EntityPlayer) entity).extinguish();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public Item getItem(World par1World, BlockPos pos){
		return null;
	}

	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityIntersectable();
	}

}