package org.freeforums.geforce.securitycraft.blocks;

import org.freeforums.geforce.securitycraft.api.IIntersectable;
import org.freeforums.geforce.securitycraft.main.Utils.BlockUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.CustomDamageSources;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockIronFence extends BlockFence implements IIntersectable {

	public BlockIronFence(Material material)
	{
		super(material);
	}

	public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9)
	{
		return false;
	}

	@Override
	public boolean canConnectTo(IBlockAccess par1IBlockAccess, BlockPos pos)
	{
		Block block = par1IBlockAccess.getBlockState(pos).getBlock();

		//split up oneliner to be more readable
		if(block != this && !(block instanceof BlockFenceGate) && block != mod_SecurityCraft.reinforcedFencegate)
		{
			if(block.getMaterial().isOpaque())
				return block.getMaterial() != Material.gourd;
			else
				return false;
		}
		else
			return true;
	}

	public void onEntityIntersected(World world, BlockPos pos, Entity entity)
	{
		//so dropped items don't get destroyed
		if(entity instanceof EntityItem)
			return;
		//owner check
		else if(entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entity;

			if(BlockUtils.isOwnerOfBlock((TileEntityOwnable) world.getTileEntity(pos), player));
				return;
		}
		else if(entity instanceof EntityCreeper)
		{
			EntityCreeper creeper = (EntityCreeper)entity;
			EntityLightningBolt lightning = new EntityLightningBolt(world, pos.getX(), pos.getY(), pos.getZ());

			creeper.onStruckByLightning(lightning);
			creeper.extinguish();
			return;
		}

		entity.attackEntityFrom(CustomDamageSources.fence, 6.0F); //3 hearts per attack
	}

    public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
		super.onBlockPlacedBy(par1World, pos, state, par5EntityLivingBase, par6ItemStack);

		((TileEntityOwnable) par1World.getTileEntity(pos)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getName());
	}

	public void breakBlock(World par1World, BlockPos pos, IBlockState par3IBlockState)
	{
		super.breakBlock(par1World, pos, par3IBlockState);
		par1World.removeTileEntity(pos);
	}

	public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam)
    {
        super.onBlockEventReceived(worldIn, pos, state, eventID, eventParam);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
    }

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityOwnable().intersectsEntities();
	}
}