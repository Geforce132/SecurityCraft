package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class ItemModifiedBucket extends ItemBucket {
	
	private Block containedBlock;

	public ItemModifiedBucket(Block containedBlock) {
		super(containedBlock);
		this.containedBlock = containedBlock;
	}
	
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        boolean flag = this.containedBlock == Blocks.AIR;
        RayTraceResult rayTraceResult = this.rayTrace(worldIn, playerIn, flag);

        if (rayTraceResult == null)
        {
            return itemStackIn;
        }
        else
        {
            ActionResult<ItemStack> result = ForgeEventFactory.onBucketUse(playerIn, worldIn, itemStackIn, rayTraceResult);
            if (result.getType() != EnumActionResult.FAIL) return result.getResult();

            if (rayTraceResult.typeOfHit == Type.BLOCK)
            {
                BlockPos blockpos = rayTraceResult.getBlockPos();

                if (!worldIn.isBlockModifiable(playerIn, blockpos))
                {
                    return itemStackIn;
                }

                if (flag)
                {
                    if (!playerIn.canPlayerEdit(blockpos.offset(rayTraceResult.sideHit), rayTraceResult.sideHit, itemStackIn))
                    {
                        return itemStackIn;
                    }

                    IBlockState iblockstate = worldIn.getBlockState(blockpos);
                    Material material = iblockstate.getBlock().getMaterial(iblockstate);
                    //StatList.OBJECT_USE_STATS no longer accessible TODO check for 1.10.2 and 1.11.2
                    if (material == Material.WATER && iblockstate.getValue(BlockLiquid.LEVEL).intValue() == 0)
                    {
                        worldIn.setBlockToAir(blockpos);
                    	//TODO StatList.OBJECT_USE_STATS
                        return this.fillBucket(itemStackIn, playerIn, mod_SecurityCraft.fWaterBucket);
                    }

                    if (material == Material.LAVA && iblockstate.getValue(BlockLiquid.LEVEL).intValue() == 0)
                    {
                        worldIn.setBlockToAir(blockpos);
                    	//TODO StatList.OBJECT_USE_STATS
                        return this.fillBucket(itemStackIn, playerIn, mod_SecurityCraft.fLavaBucket);
                    }
                }
                else
                {
                    if (this.containedBlock == Blocks.AIR)
                    {
                        return new ItemStack(Items.BUCKET);
                    }

                    BlockPos blockpos1 = blockpos.offset(rayTraceResult.sideHit);

                    if (!playerIn.canPlayerEdit(blockpos1, rayTraceResult.sideHit, itemStackIn))
                    {
                        return itemStackIn;
                    }

                    if (this.tryPlaceContainedLiquid(worldIn, blockpos1) && !playerIn.capabilities.isCreativeMode)
                    {
                    	//TODO StatList.OBJECT_USE_STATS
                        return new ItemStack(Items.BUCKET);
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
                player.dropItem(new ItemStack(fullBucket, 1, 0), false);
            }

            return emptyBuckets;
        }
    }
	
	public boolean tryPlaceContainedLiquid(World worldIn, BlockPos pos)
    {
        if (this.containedBlock == Blocks.AIR)
        {
            return false;
        }
        else
        {
            Material material = worldIn.getBlockState(pos).getBlock().getMaterial(worldIn.getBlockState(pos));
            boolean flag = !material.isSolid();

            if (!worldIn.isAirBlock(pos) && !flag)
            {
                return false;
            }
            else
            {
                if (worldIn.provider.doesWaterVaporize() && this.containedBlock == Blocks.FLOWING_WATER)
                {
                    int i = pos.getX();
                    int j = pos.getY();
                    int k = pos.getZ();
                    
                    for(EntityPlayer player : worldIn.playerEntities)
            		{
            			worldIn.playSound(player, new BlockPos(i + 0.5F, j + 0.5F, k + 0.5F), SoundEvent.REGISTRY.getObject(new ResourceLocation("random.fizz")), SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);
            		}
                    
                    for (int l = 0; l < 8; ++l)
                    {
                        worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, i + Math.random(), j + Math.random(), k + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
                    }
                }
                else
                {
                    if (!worldIn.isRemote && flag && !material.isLiquid())
                    {
                        worldIn.destroyBlock(pos, true);
                    }

                    worldIn.setBlockState(pos, this.containedBlock.getDefaultState(), 3);
                }

                return true;
            }
        }
    }
}
