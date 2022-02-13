package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
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

	//redstone signals should not be able to open these doors
	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(world, pos, state, placer, stack);

		TileEntity lowerTe = world.getBlockEntity(pos);
		TileEntity upperTe = world.getBlockEntity(pos.above());

		if (lowerTe instanceof IOwnable && upperTe instanceof IOwnable) {
			if (placer instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) placer;

				((IOwnable) lowerTe).setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
				((IOwnable) upperTe).setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
			}

			if (lowerTe instanceof LinkableBlockEntity && upperTe instanceof LinkableBlockEntity)
				LinkableBlockEntity.link((LinkableBlockEntity) lowerTe, (LinkableBlockEntity) upperTe);
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		BlockState upperState = world.getBlockState(pos);

		if (!upperState.getValue(DoorBlock.OPEN))
			return;

		BlockState lowerState;

		if (upperState.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
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
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		super.onRemove(state, world, pos, newState, isMoving);

		if (state.getBlock() != newState.getBlock())
			world.removeBlockEntity(pos);
	}

	@Override
	public boolean triggerEvent(BlockState state, World world, BlockPos pos, int id, int param) {
		super.triggerEvent(state, world, pos, id, param);

		TileEntity tileentity = world.getBlockEntity(pos);

		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return new ItemStack(getDoorItem());
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
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
