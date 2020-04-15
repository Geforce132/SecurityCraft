package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class ItemModifiedBucket extends ItemBucket {

	private Block containedBlock;

	public ItemModifiedBucket(Block containedBlock) {
		super(containedBlock);
		this.containedBlock = containedBlock;
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, new BehaviorDefaultDispenseItem() {
			private final BehaviorDefaultDispenseItem instance = new BehaviorDefaultDispenseItem();

			@Override
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			{
				ItemBucket itembucket = (ItemBucket)stack.getItem();
				BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().getValue(BlockDispenser.FACING));

				return itembucket.tryPlaceContainedLiquid(null, source.getWorld(), blockpos) ? new ItemStack(Items.BUCKET) : instance.dispense(source, stack);
			}
		});
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		boolean isAir = containedBlock == Blocks.AIR;
		RayTraceResult rayTrace = rayTrace(world, player, isAir);
		ActionResult<ItemStack> eventResult = ForgeEventFactory.onBucketUse(player, world, stack, rayTrace);
		if (eventResult != null) return eventResult;

		if (rayTrace == null)
			return new ActionResult<>(EnumActionResult.PASS, stack);
		else if (rayTrace.typeOfHit != RayTraceResult.Type.BLOCK)
			return new ActionResult<>(EnumActionResult.PASS, stack);
		else
		{
			BlockPos pos = rayTrace.getBlockPos();

			if (!world.isBlockModifiable(player, pos))
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			else if (isAir)
			{
				if (!player.canPlayerEdit(pos.offset(rayTrace.sideHit), rayTrace.sideHit, stack))
					return new ActionResult<>(EnumActionResult.FAIL, stack);
				else
				{
					IBlockState state = world.getBlockState(pos);
					Material material = state.getMaterial();

					if (material == Material.WATER && state.getValue(BlockLiquid.LEVEL) == 0)
					{
						world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
						player.addStat(StatList.getObjectUseStats(this));
						player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
						return new ActionResult<>(EnumActionResult.SUCCESS, fillBucket(stack, player, SCContent.fWaterBucket));
					}
					else if (material == Material.LAVA && state.getValue(BlockLiquid.LEVEL) == 0)
					{
						player.playSound(SoundEvents.ITEM_BUCKET_FILL_LAVA, 1.0F, 1.0F);
						world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
						player.addStat(StatList.getObjectUseStats(this));
						return new ActionResult<>(EnumActionResult.SUCCESS, fillBucket(stack, player, SCContent.fLavaBucket));
					}
					else
						return new ActionResult<>(EnumActionResult.FAIL, stack);
				}
			}
			else
			{
				boolean isReplaceable = world.getBlockState(pos).getBlock().isReplaceable(world, pos);
				BlockPos offsetPos = isReplaceable && rayTrace.sideHit == EnumFacing.UP ? pos : pos.offset(rayTrace.sideHit);

				if (!player.canPlayerEdit(offsetPos, rayTrace.sideHit, stack))
					return new ActionResult<>(EnumActionResult.FAIL, stack);
				else if (this.tryPlaceContainedLiquid(player, world, offsetPos))
				{
					player.addStat(StatList.getObjectUseStats(this));
					return !player.capabilities.isCreativeMode ? new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(Items.BUCKET)) : new ActionResult<>(EnumActionResult.SUCCESS, stack);
				}
				else
					return new ActionResult<>(EnumActionResult.FAIL, stack);
			}
		}
	}

	private ItemStack fillBucket(ItemStack emptyBuckets, EntityPlayer player, Item fullBucket)
	{
		if (player.capabilities.isCreativeMode)
			return emptyBuckets;

		emptyBuckets.shrink(1);

		if (emptyBuckets.getCount() <= 0)
			return new ItemStack(fullBucket);
		else
		{
			if (!player.inventory.addItemStackToInventory(new ItemStack(fullBucket)))
				player.dropItem(new ItemStack(fullBucket, 1, 0), false);

			return emptyBuckets;
		}
	}

	@Override
	public boolean tryPlaceContainedLiquid(EntityPlayer player, World world, BlockPos pos)
	{
		if (containedBlock == Blocks.AIR)
			return false;
		else
		{
			Material material = world.getBlockState(pos).getMaterial();
			boolean flag = !material.isSolid();

			if (!world.isAirBlock(pos) && !flag)
				return false;
			else
			{
				if (world.provider.doesWaterVaporize() && containedBlock == SCContent.bogusWaterFlowing)
				{
					int x = pos.getX();
					int y = pos.getY();
					int z = pos.getZ();

					world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

					for (int l = 0; l < 8; ++l)
						world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x + Math.random(), y + Math.random(), z + Math.random(), 0.0D, 0.0D, 0.0D);
				}
				else
				{
					if (!world.isRemote && flag && !material.isLiquid())
						world.destroyBlock(pos, true);

					world.setBlockState(pos, containedBlock.getDefaultState(), 3);
				}

				return true;
			}
		}
	}
}
