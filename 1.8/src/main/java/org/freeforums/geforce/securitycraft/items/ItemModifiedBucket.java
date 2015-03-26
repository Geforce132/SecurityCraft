package org.freeforums.geforce.securitycraft.items;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemModifiedBucket extends ItemBucket implements IHelpInfo{
	
	private Block containedBlock;

	public ItemModifiedBucket(Block containedBlock) {
		super(containedBlock);
		this.containedBlock = containedBlock;
	}
	
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        boolean flag = this.containedBlock == Blocks.air;
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, flag);

        if (movingobjectposition == null)
        {
            return itemStackIn;
        }
        else
        {
            ItemStack ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(playerIn, worldIn, itemStackIn, movingobjectposition);
            if (ret != null) return ret;

            if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                BlockPos blockpos = movingobjectposition.getBlockPos();

                if (!worldIn.isBlockModifiable(playerIn, blockpos))
                {
                    return itemStackIn;
                }

                if (flag)
                {
                    if (!playerIn.canPlayerEdit(blockpos.offset(movingobjectposition.sideHit), movingobjectposition.sideHit, itemStackIn))
                    {
                        return itemStackIn;
                    }

                    IBlockState iblockstate = worldIn.getBlockState(blockpos);
                    Material material = iblockstate.getBlock().getMaterial();

                    if (material == Material.water && ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0)
                    {
                        worldIn.setBlockToAir(blockpos);
                        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                        return this.fillBucket(itemStackIn, playerIn, mod_SecurityCraft.fWaterBucket);
                    }

                    if (material == Material.lava && ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0)
                    {
                        worldIn.setBlockToAir(blockpos);
                        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                        return this.fillBucket(itemStackIn, playerIn, mod_SecurityCraft.fLavaBucket);
                    }
                }
                else
                {
                    if (this.containedBlock == Blocks.air)
                    {
                        return new ItemStack(Items.bucket);
                    }

                    BlockPos blockpos1 = blockpos.offset(movingobjectposition.sideHit);

                    if (!playerIn.canPlayerEdit(blockpos1, movingobjectposition.sideHit, itemStackIn))
                    {
                        return itemStackIn;
                    }

                    if (this.tryPlaceContainedLiquid(worldIn, blockpos1) && !playerIn.capabilities.isCreativeMode)
                    {
                        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                        return new ItemStack(Items.bucket);
                    }
                }
            }

            return itemStackIn;
        }
    }
	
	private ItemStack fillBucket(ItemStack emptyBuckets, EntityPlayer player, Item fullBucket)
    {
        if (player.capabilities.isCreativeMode)
        {
            return emptyBuckets;
        }
        else if (--emptyBuckets.stackSize <= 0)
        {
            return new ItemStack(fullBucket);
        }
        else
        {
            if (!player.inventory.addItemStackToInventory(new ItemStack(fullBucket)))
            {
                player.dropPlayerItemWithRandomChoice(new ItemStack(fullBucket, 1, 0), false);
            }

            return emptyBuckets;
        }
    }

	public String getHelpInfo() {
		if(containedBlock == mod_SecurityCraft.bogusLava){
			return "The fake lava acts the same as lava, except it heals you instead of hurting you.";
		}else if(containedBlock == mod_SecurityCraft.bogusWater){
			return "The fake water acts the same as water, expect it hurts you when touched.";
		}else{
			return null;
		}
	}

	public String[] getRecipe() {
		if(containedBlock == mod_SecurityCraft.bogusLava){
			return new String[]{"The bucket of fake lava requires: 1 lava bucket, 1 healing potion.", "X", "Y", "   ", "X = healing potion, Y = lava bucket"};
		}else if(containedBlock == mod_SecurityCraft.bogusWater){
			return new String[]{"The bucket of fake water requires: 1 water bucket, 1 harming potion.", "X", "Y", "   ", "X = harming potion, Y = water bucket"};
		}else{
			return null;
		}
	}
	
}
