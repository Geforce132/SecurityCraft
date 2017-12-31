package net.geforcemods.securitycraft.items;

import cpw.mods.fml.common.eventhandler.Event;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;

public class ItemModifiedBucket extends Item {

	private Block isFull;

	public ItemModifiedBucket(Block par1Block){
		maxStackSize = 1;
		isFull = par1Block;
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer){
		boolean flag = isFull == Blocks.air;
		MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, flag);

		if (movingobjectposition == null)
			return par1ItemStack;
		else{
			FillBucketEvent event = new FillBucketEvent(par3EntityPlayer, par1ItemStack, par2World, movingobjectposition);
			if (MinecraftForge.EVENT_BUS.post(event))
				return par1ItemStack;

			if (event.getResult() == Event.Result.ALLOW){
				if (par3EntityPlayer.capabilities.isCreativeMode)
					return par1ItemStack;

				if (--par1ItemStack.stackSize <= 0)
					return event.result;

				if (!par3EntityPlayer.inventory.addItemStackToInventory(event.result))
					par3EntityPlayer.dropPlayerItemWithRandomChoice(event.result, false);

				return par1ItemStack;
			}

			if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK){
				int i = movingobjectposition.blockX;
				int j = movingobjectposition.blockY;
				int k = movingobjectposition.blockZ;

				if (!par2World.canMineBlock(par3EntityPlayer, i, j, k))
					return par1ItemStack;

				if (flag){
					if (!par3EntityPlayer.canPlayerEdit(i, j, k, movingobjectposition.sideHit, par1ItemStack))
						return par1ItemStack;

					Material material = par2World.getBlock(i, j, k).getMaterial();
					int l = par2World.getBlockMetadata(i, j, k);

					if (material == Material.water && l == 0){
						par2World.setBlockToAir(i, j, k);
						return new ItemStack(SCContent.fWaterBucket, 1, 0);
					}

					if (material == Material.lava && l == 0){
						par2World.setBlockToAir(i, j, k);
						return new ItemStack(SCContent.fLavaBucket, 1, 0);
					}
				}else{
					if (isFull == Blocks.air)
						return new ItemStack(Items.bucket);

					if (movingobjectposition.sideHit == 0)
						--j;

					if (movingobjectposition.sideHit == 1)
						++j;

					if (movingobjectposition.sideHit == 2)
						--k;

					if (movingobjectposition.sideHit == 3)
						++k;

					if (movingobjectposition.sideHit == 4)
						--i;

					if (movingobjectposition.sideHit == 5)
						++i;

					if (!par3EntityPlayer.canPlayerEdit(i, j, k, movingobjectposition.sideHit, par1ItemStack))
						return par1ItemStack;

					if (tryPlaceContainedLiquid(par2World, i, j, k) && !par3EntityPlayer.capabilities.isCreativeMode)
						return new ItemStack(Items.bucket);
				}
			}

			return par1ItemStack;
		}
	}

	/**
	 * Attempts to place the liquid contained inside the bucket.
	 */
	public boolean tryPlaceContainedLiquid(World par1World, int par2, int par3, int par4){
		if (isFull == Blocks.air)
			return false;
		else{
			Material material = par1World.getBlock(par2, par3, par4).getMaterial();
			boolean flag = !material.isSolid();

			if (!par1World.isAirBlock(par2, par3, par4) && !flag)
				return false;
			else{
				if (par1World.provider.isHellWorld && isFull == Blocks.flowing_water){
					par1World.playSoundEffect(par2 + 0.5F, par3 + 0.5F, par4 + 0.5F, "random.fizz", 0.5F, 2.6F + (par1World.rand.nextFloat() - par1World.rand.nextFloat()) * 0.8F);

					for (int l = 0; l < 8; ++l)
						par1World.spawnParticle("largesmoke", par2 + Math.random(), par3 + Math.random(), par4 + Math.random(), 0.0D, 0.0D, 0.0D);
				}else{
					if (!par1World.isRemote && flag && !material.isLiquid())
						par1World.breakBlock(par2, par3, par4, true);

					par1World.setBlock(par2, par3, par4, isFull, 0, 3);
				}

				return true;
			}
		}
	}

}
