package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class SpecialDoorBlock extends DoorBlock {
	public SpecialDoorBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		TileEntity lowerTe = world.getTileEntity(pos);
		TileEntity upperTe = world.getTileEntity(pos.up());

		if (lowerTe instanceof IOwnable && upperTe instanceof IOwnable) {
			if (placer instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) placer;

				((IOwnable) lowerTe).setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
				((IOwnable) upperTe).setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
			}

			if (lowerTe instanceof LinkableTileEntity && upperTe instanceof LinkableTileEntity)
				LinkableTileEntity.link((LinkableTileEntity) lowerTe, (LinkableTileEntity) upperTe);
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		BlockState upperState = world.getBlockState(pos);

		if (!upperState.get(DoorBlock.OPEN))
			return;

		BlockState lowerState;

		if (upperState.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
			lowerState = upperState;
			pos = pos.up();
			upperState = world.getBlockState(pos);
		}
		else
			lowerState = world.getBlockState(pos.down());

		world.setBlockState(pos, upperState.with(DoorBlock.OPEN, false), 3);
		world.setBlockState(pos.down(), lowerState.with(DoorBlock.OPEN, false), 3);
		world.playEvent(null, 1011, pos, 0);
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		super.onReplaced(state, world, pos, newState, isMoving);

		if (state.getBlock() != newState.getBlock())
			world.removeTileEntity(pos);
	}

	@Override
	public boolean eventReceived(BlockState state, World world, BlockPos pos, int id, int param) {
		super.eventReceived(state, world, pos, id, param);

		TileEntity tileentity = world.getTileEntity(pos);

		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return new ItemStack(getDoorItem());
	}

	@Override
	public PushReaction getPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

	public abstract Item getDoorItem();
}
