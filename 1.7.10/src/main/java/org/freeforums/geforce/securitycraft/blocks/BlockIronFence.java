package org.freeforums.geforce.securitycraft.blocks;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.CustomDamageSources;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockIronFence extends BlockFence implements ITileEntityProvider{
	
	public BlockIronFence(String texture, Material material)
	{
		super(texture, material);
	}

    public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        return false;
    }
	
    @Override
    public boolean canConnectFenceTo(IBlockAccess p_149826_1_, int p_149826_2_, int p_149826_3_, int p_149826_4_)
    {
    	Block block = p_149826_1_.getBlock(p_149826_2_, p_149826_3_, p_149826_4_);
    	
    	//split up oneliner to be more readable
    	if(block != this && block != Blocks.fence_gate && block != mod_SecurityCraft.reinforcedFencegate)
    	{
    		if(block.getMaterial().isOpaque() && block.renderAsNormalBlock())
    			return block.getMaterial() != Material.gourd;
    		else
    			return false;
    	}
    	else
    		return true;
    }
    
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
    {
    	boolean shouldHurt = true;

    	//so dropped items don't get destroyed
		if(entity instanceof EntityItem)
			return;
    	
		//owner check
		if(entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entity;

			if(((TileEntityOwnable)world.getTileEntity(x, y, z)).getOwnerUUID().equals(player.getUniqueID().toString()))
				shouldHurt = false;
		}

		if(shouldHurt)
			entity.attackEntityFrom(CustomDamageSources.fence, 1.0F);
    }
    
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
		super.onBlockPlacedBy(par1World, par2, par3, par4, par5EntityLivingBase, par6ItemStack);

		((TileEntityOwnable) par1World.getTileEntity(par2, par3, par4)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());
	}

	public void breakBlock(World par1World, int par2, int par3, int par4, Block par5Block, int par6)
	{
		super.breakBlock(par1World, par2, par3, par4, par5Block, par6);
		par1World.removeTileEntity(par2, par3, par4);
	}
	
    public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
        TileEntity tileentity = par1World.getTileEntity(par2, par3, par4);
        return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
    }

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityOwnable();
	}
	
}
