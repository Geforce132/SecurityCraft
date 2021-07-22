package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;

public abstract class SpecialDoorBlock extends DoorBlock
{
	public SpecialDoorBlock(Block.Properties properties)
	{
		super(properties);
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean flag)
	{
		onNeighborChanged(world, pos, fromPos);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof Player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (Player)placer));

		super.setPlacedBy(world, pos, state, placer, stack);
	}

	/**
	 * Old method, renamed because I am lazy. Called by neighborChanged
	 * @param world The world the change occured in
	 * @param pos The position of this block
	 * @param neighbor The position of the changed block
	 */
	public void onNeighborChanged(Level world, BlockPos pos, BlockPos neighbor)
	{
		BlockState state = world.getBlockState(pos);
		Block neighborBlock = world.getBlockState(neighbor).getBlock();

		if(state.getValue(HALF) == DoubleBlockHalf.UPPER)
		{
			BlockPos blockBelow = pos.below();
			BlockState stateBelow = world.getBlockState(blockBelow);

			if(stateBelow.getBlock() != this)
				world.destroyBlock(pos, false);
			else if (neighborBlock != this)
				onNeighborChanged(world, blockBelow, neighbor);
		}
		else
		{
			boolean drop = false;
			BlockPos blockBelow = pos.above();
			BlockState stateBelow = world.getBlockState(blockBelow);

			if(stateBelow.getBlock() != this)
			{
				world.destroyBlock(pos, false);
				drop = true;
			}

			if(!world.getBlockState(pos.below()).isFaceSturdy(world, pos.below(), Direction.UP))
			{
				world.destroyBlock(pos, false);
				drop = true;

				if(stateBelow.getBlock() == this)
					world.destroyBlock(blockBelow, false);
			}

			if(drop)
			{
				if(!world.isClientSide)
				{
					world.destroyBlock(pos, false);
					Block.popResource(world, pos, new ItemStack(getDoorItem()));
				}
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, Random rand)
	{
		BlockState upperState = world.getBlockState(pos);
		BlockState lowerState;

		if(upperState.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER)
		{
			lowerState = upperState;
			pos = pos.above();
			upperState = world.getBlockState(pos);
		}
		else
			lowerState = world.getBlockState(pos.below());

		world.setBlock(pos, upperState.setValue(DoorBlock.OPEN, false), 3);
		world.setBlock(pos.below(), lowerState.setValue(DoorBlock.OPEN, false), 3);
		world.levelEvent(null, 1011, pos, 0);
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onRemove(state, world, pos, newState, isMoving);

		if(state.getBlock() != newState.getBlock())
			world.removeBlockEntity(pos);
	}

	@Override
	public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int id, int param)
	{
		super.triggerEvent(state, world, pos, id, param);

		BlockEntity tileentity = world.getBlockEntity(pos);

		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player)
	{
		return new ItemStack(getDoorItem());
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state)
	{
		return PushReaction.BLOCK;
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public abstract BlockEntity createTileEntity(BlockState state, BlockGetter world);

	public abstract Item getDoorItem();
}
