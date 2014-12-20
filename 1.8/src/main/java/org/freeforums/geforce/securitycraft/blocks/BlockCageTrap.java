package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public class BlockCageTrap extends Block{

	public final boolean deactivated;
	private final int blockTextureIndex;

	public BlockCageTrap(Material par2Material, boolean deactivated, int blockTextureIndex) {
		super(par2Material);
		this.deactivated = deactivated;
		this.blockTextureIndex = blockTextureIndex;
	}

	public AxisAlignedBB getCollisionBoundingBox(World par1World, BlockPos pos, IBlockState state)
	{
		if(!deactivated){
			return null;
		}else{
			return AxisAlignedBB.fromBounds((double) pos.getX() + this.minX, (double) pos.getY() + this.minY, (double) pos.getZ() + this.minZ, (double) pos.getX() + this.maxX, (double) pos.getY() + this.maxY, (double) pos.getZ() + this.maxZ);
		}
	}


	public void onEntityCollidedWithBlock(World par1World, BlockPos pos, Entity par5Entity){
		if(par1World.isRemote){
			return;
		}else{
			if(par5Entity instanceof EntityPlayer && !deactivated){
				Utils.setBlock(par1World, pos, mod_SecurityCraft.deactivatedCageTrap);
				par1World.scheduleUpdate(pos, mod_SecurityCraft.unbreakableIronBars, 1200);

				Utils.setBlock(par1World, pos.up(4), mod_SecurityCraft.unbreakableIronBars);
				par1World.scheduleUpdate(pos.up(4), mod_SecurityCraft.unbreakableIronBars, 1200);

				Utils.setBlock(par1World, pos.getX() + 1, pos.getY() + 4, pos.getZ(), mod_SecurityCraft.unbreakableIronBars);	
				par1World.scheduleUpdate(new BlockPos(pos.getX() + 1, pos.getY() + 4, pos.getZ()), mod_SecurityCraft.unbreakableIronBars, 1200);

				Utils.setBlock(par1World, pos.getX() - 1, pos.getY() + 4, pos.getZ(), mod_SecurityCraft.unbreakableIronBars);	
				par1World.scheduleUpdate(new BlockPos(pos.getX() - 1, pos.getY() + 4, pos.getZ()), mod_SecurityCraft.unbreakableIronBars, 1200);

				Utils.setBlock(par1World, pos.getX(), pos.getY() + 4, pos.getZ() + 1, mod_SecurityCraft.unbreakableIronBars);	
				par1World.scheduleUpdate(new BlockPos(pos.getX(), pos.getY() + 4, pos.getZ() + 1), mod_SecurityCraft.unbreakableIronBars, 1200);

				Utils.setBlock(par1World, pos.getX(), pos.getY() + 4, pos.getZ() - 1, mod_SecurityCraft.unbreakableIronBars);	
				par1World.scheduleUpdate(new BlockPos(pos.getX(), pos.getY() + 4, pos.getZ() - 1), mod_SecurityCraft.unbreakableIronBars, 1200);

				HelpfulMethods.setBlockInBox(par1World, pos.getX(), pos.getY(), pos.getZ(), mod_SecurityCraft.unbreakableIronBars);

				par1World.playSoundAtEntity(par5Entity, "random.anvil_use", 3.0F, 1.0F);
				MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation(((EntityPlayer) par5Entity).getName() + " was captured in a trap at " + Utils.getFormattedCoordinates(pos)));

			}
		}
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(Random par1Random){
		return this.deactivated ? 0 : 1;
	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	public Item getItemDropped(IBlockState state, Random par2Random, int par3)
	{
		return this.deactivated ? HelpfulMethods.getItemFromBlock(mod_SecurityCraft.deactivatedCageTrap) : HelpfulMethods.getItemFromBlock(this);
	}

}
