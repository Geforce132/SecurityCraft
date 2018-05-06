package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockIronFence extends BlockFence implements ITileEntityProvider{

	public BlockIronFence(String texture, Material material)
	{
		super(texture, material);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		return false;
	}

	@Override
	public boolean canConnectFenceTo(IBlockAccess access, int x, int y, int z)
	{
		Block block = access.getBlock(x, y, z);

		//split up oneliner to be more readable
		if(block != this && block != Blocks.fence_gate && block != SCContent.reinforcedFencegate)
		{
			if(block.getMaterial().isOpaque() && block.renderAsNormalBlock())
				return block.getMaterial() != Material.gourd;
			else
				return false;
		}
		else
			return true;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		//so dropped items don't get destroyed
		if(entity instanceof EntityItem)
			return;
		//owner check
		else if(entity instanceof EntityPlayer)
		{
			if(((TileEntityOwnable) world.getTileEntity(x, y, z)).getOwner().isOwner((EntityPlayer)entity))
				return;
		}
		else if(entity instanceof EntityCreeper)
		{
			EntityCreeper creeper = (EntityCreeper)entity;
			EntityLightningBolt lightning = new EntityLightningBolt(world, x, y, z);

			creeper.onStruckByLightning(lightning);
			creeper.extinguish();
			return;
		}

		entity.attackEntityFrom(CustomDamageSources.electricity, 6.0F); //3 hearts per attack
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		super.breakBlock(world, x, y, z, block, meta);
		world.removeTileEntity(x, y, z);
	}

	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int eventID, int eventData)
	{
		super.onBlockEventReceived(world, x, y, z, eventID, eventData);
		TileEntity tileentity = world.getTileEntity(x, y, z);
		return tileentity != null ? tileentity.receiveClientEvent(eventID, eventData) : false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityOwnable();
	}
}