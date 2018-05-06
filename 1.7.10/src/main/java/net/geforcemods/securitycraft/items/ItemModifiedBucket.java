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

	private Block containedBlock;

	public ItemModifiedBucket(Block block){
		maxStackSize = 1;
		containedBlock = block;
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
		boolean flag = containedBlock == Blocks.air;
		MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, flag);

		if (mop == null)
			return stack;
		else{
			FillBucketEvent event = new FillBucketEvent(player, stack, world, mop);
			if (MinecraftForge.EVENT_BUS.post(event))
				return stack;

			if (event.getResult() == Event.Result.ALLOW){
				if (player.capabilities.isCreativeMode)
					return stack;

				if (--stack.stackSize <= 0)
					return event.result;

				if (!player.inventory.addItemStackToInventory(event.result))
					player.dropPlayerItemWithRandomChoice(event.result, false);

				return stack;
			}

			if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK){
				int blockX = mop.blockX;
				int blockY = mop.blockY;
				int blockZ = mop.blockZ;

				if (!world.canMineBlock(player, blockX, blockY, blockZ))
					return stack;

				if (flag){
					if (!player.canPlayerEdit(blockX, blockY, blockZ, mop.sideHit, stack))
						return stack;

					Material material = world.getBlock(blockX, blockY, blockZ).getMaterial();
					int l = world.getBlockMetadata(blockX, blockY, blockZ);

					if (material == Material.water && l == 0){
						world.setBlockToAir(blockX, blockY, blockZ);
						return new ItemStack(SCContent.fWaterBucket, 1, 0);
					}

					if (material == Material.lava && l == 0){
						world.setBlockToAir(blockX, blockY, blockZ);
						return new ItemStack(SCContent.fLavaBucket, 1, 0);
					}
				}else{
					if (containedBlock == Blocks.air)
						return new ItemStack(Items.bucket);

					if (mop.sideHit == 0)
						--blockY;

					if (mop.sideHit == 1)
						++blockY;

					if (mop.sideHit == 2)
						--blockZ;

					if (mop.sideHit == 3)
						++blockZ;

					if (mop.sideHit == 4)
						--blockX;

					if (mop.sideHit == 5)
						++blockX;

					if (!player.canPlayerEdit(blockX, blockY, blockZ, mop.sideHit, stack))
						return stack;

					if (tryPlaceContainedLiquid(world, blockX, blockY, blockZ) && !player.capabilities.isCreativeMode)
						return new ItemStack(Items.bucket);
				}
			}

			return stack;
		}
	}

	/**
	 * Attempts to place the liquid contained inside the bucket.
	 */
	public boolean tryPlaceContainedLiquid(World world, int x, int y, int z){
		if (containedBlock == Blocks.air)
			return false;
		else{
			Material material = world.getBlock(x, y, z).getMaterial();
			boolean flag = !material.isSolid();

			if (!world.isAirBlock(x, y, z) && !flag)
				return false;
			else{
				if (world.provider.isHellWorld && containedBlock == Blocks.flowing_water){
					world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

					for (int l = 0; l < 8; ++l)
						world.spawnParticle("largesmoke", x + Math.random(), y + Math.random(), z + Math.random(), 0.0D, 0.0D, 0.0D);
				}else{
					if (!world.isRemote && flag && !material.isLiquid())
						world.breakBlock(x, y, z, true);

					world.setBlock(x, y, z, containedBlock, 0, 3);
				}

				return true;
			}
		}
	}

}
