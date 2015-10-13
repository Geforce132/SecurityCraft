package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFakeWaterBase extends BlockStaticLiquid {
	
	public BlockFakeWaterBase(Material par2Material){
		super(par2Material);
		this.setTickRandomly(true);
		this.disableStats();
	}

	public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4){
		return this.blockMaterial != Material.lava;
	}

	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5){
		if (par1World.getBlock(par2, par3, par4) == this){
			this.setNotStationary(par1World, par2, par3, par4);
		}
	}

	/**
	 * Changes the block ID to that of an updating fluid.
	 */
	private void setNotStationary(World par1World, int par2, int par3, int par4){
		int l = par1World.getBlockMetadata(par2, par3, par4);
		par1World.setBlock(par2, par3, par4, mod_SecurityCraft.bogusWaterFlowing, l, 2);
		par1World.scheduleBlockUpdate(par2, par3, par4, mod_SecurityCraft.bogusWaterFlowing, this.tickRate(par1World));
	}

	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random){
		if (this.blockMaterial == Material.lava){
			int l = par5Random.nextInt(3);
			int i1;

			for (i1 = 0; i1 < l; ++i1){
				par2 += par5Random.nextInt(3) - 1;
				++par3;
				par4 += par5Random.nextInt(3) - 1;
				Block block = par1World.getBlock(par2, par3, par4);

				if (block.getMaterial() == Material.air){
					if (this.isFlammable(par1World, par2 - 1, par3, par4) || this.isFlammable(par1World, par2 + 1, par3, par4) || this.isFlammable(par1World, par2, par3, par4 - 1) || this.isFlammable(par1World, par2, par3, par4 + 1) || this.isFlammable(par1World, par2, par3 - 1, par4) || this.isFlammable(par1World, par2, par3 + 1, par4)){
						par1World.setBlock(par2, par3, par4, Blocks.fire);
						return;
					}
				}else if (block.getMaterial().blocksMovement()){
					return;
				}
			}

			if (l == 0){
				i1 = par2;
				int k1 = par4;

				for (int j1 = 0; j1 < 3; ++j1){
					par2 = i1 + par5Random.nextInt(3) - 1;
					par4 = k1 + par5Random.nextInt(3) - 1;

					if (par1World.isAirBlock(par2, par3 + 1, par4) && this.isFlammable(par1World, par2, par3, par4)){
						par1World.setBlock(par2, par3 + 1, par4, Blocks.fire);
					}
				}
			}
		}
	}

	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity){
		if(!par1World.isRemote){
			if(par5Entity instanceof EntityPlayer && !((EntityPlayer) par5Entity).capabilities.isCreativeMode){
				((EntityPlayer) par5Entity).attackEntityFrom(CustomDamageSources.fakeWater, 5F);
			}
			else
				par5Entity.attackEntityFrom(CustomDamageSources.fakeWater, 5F);
		}
	}

	/**
	 * Checks to see if the block is flammable.
	 */
	private boolean isFlammable(World par1World, int par2, int par3, int par4){
		return par1World.getBlock(par2, par3, par4).getMaterial().getCanBurn();
	}

	/**
	 * Gets an item for the block being called on. Args: world, x, y, z
	 */
	@SideOnly(Side.CLIENT)
	public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_){
		return null;
	}
	
}
