package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
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
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemModifiedBucket extends ItemBucket {

	private Block containedBlock;

	public ItemModifiedBucket(Block containedBlock) {
		super(containedBlock);
		this.containedBlock = containedBlock;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		boolean flag = containedBlock == Blocks.air;
		MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, flag);

		if (mop == null)
			return stack;
		else
		{
			ItemStack eventResult = net.minecraftforge.event.ForgeEventFactory.onBucketUse(player, world, stack, mop);
			if (eventResult != null) return eventResult;

			if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
			{
				BlockPos pos = mop.getBlockPos();

				if (!world.isBlockModifiable(player, pos))
					return stack;

				if (flag)
				{
					if (!player.canPlayerEdit(pos.offset(mop.sideHit), mop.sideHit, stack))
						return stack;

					IBlockState state = world.getBlockState(pos);
					Material material = state.getBlock().getMaterial();

					if (material == Material.water && state.getValue(BlockLiquid.LEVEL).intValue() == 0)
					{
						world.setBlockToAir(pos);
						player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
						return fillBucket(stack, player, SCContent.fWaterBucket);
					}

					if (material == Material.lava && state.getValue(BlockLiquid.LEVEL).intValue() == 0)
					{
						world.setBlockToAir(pos);
						player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
						return fillBucket(stack, player, SCContent.fLavaBucket);
					}
				}
				else
				{
					if (containedBlock == Blocks.air)
						return new ItemStack(Items.bucket);

					BlockPos offsetPos = pos.offset(mop.sideHit);

					if (!player.canPlayerEdit(offsetPos, mop.sideHit, stack))
						return stack;

					if (tryPlaceContainedLiquid(world, offsetPos) && !player.capabilities.isCreativeMode)
					{
						player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
						return new ItemStack(Items.bucket);
					}
				}
			}

			return stack;
		}
	}

	private ItemStack fillBucket(ItemStack emptyBucket, EntityPlayer player, Item fullBucket)
	{
		if (player.capabilities.isCreativeMode)
			return emptyBucket;
		else if (--emptyBucket.stackSize <= 0)
			return new ItemStack(fullBucket);
		else
		{
			if (!player.inventory.addItemStackToInventory(new ItemStack(fullBucket)))
				player.dropPlayerItemWithRandomChoice(new ItemStack(fullBucket, 1, 0), false);

			return emptyBucket;
		}
	}

	@Override
	public boolean tryPlaceContainedLiquid(World world, BlockPos pos)
	{
		if (containedBlock == Blocks.air)
			return false;
		else
		{
			Material material = world.getBlockState(pos).getBlock().getMaterial();
			boolean isNotSolid = !material.isSolid();

			if (!world.isAirBlock(pos) && !isNotSolid)
				return false;
			else
			{
				if (world.provider.doesWaterVaporize() && containedBlock == Blocks.flowing_water)
				{
					int x = pos.getX();
					int y = pos.getY();
					int z = pos.getZ();
					world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

					for (int l = 0; l < 8; ++l)
						world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x + Math.random(), y + Math.random(), z + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
				}
				else
				{
					if (!world.isRemote && isNotSolid && !material.isLiquid())
						world.destroyBlock(pos, true);

					world.setBlockState(pos, containedBlock.getDefaultState(), 3);
				}

				return true;
			}
		}
	}
}
