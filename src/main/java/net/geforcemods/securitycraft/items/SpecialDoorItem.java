package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class SpecialDoorItem extends BlockItem
{
	public SpecialDoorItem(Block block, Item.Properties properties)
	{
		super(block, properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getLevel(), ctx.getClickedPos(), ctx.getItemInHand(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx);
	}

	public InteractionResult onItemUse(Player player, Level level, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ, UseOnContext ctx)
	{
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();

		if (!block.canBeReplaced(state, new BlockPlaceContext(ctx)))
			pos = pos.relative(facing);

		if (player.mayUseItemAt(pos, facing, stack) && BlockUtils.isSideSolid(level, pos.below(), Direction.UP))
		{
			Direction angleFacing = Direction.fromYRot(player.getYRot());
			int offsetX = angleFacing.getStepX();
			int offsetZ = angleFacing.getStepZ();
			boolean flag = offsetX < 0 && hitZ < 0.5F || offsetX > 0 && hitZ > 0.5F || offsetZ < 0 && hitX > 0.5F || offsetZ > 0 && hitX < 0.5F;

			if(!placeDoor(level, pos, angleFacing, getBlock(), flag, ctx))
				return InteractionResult.FAIL;

			state = level.getBlockState(pos);
			block = state.getBlock();

			SoundType soundType = block.getSoundType(state, level, pos, player);

			level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);

			if(!player.isCreative())
				stack.shrink(1);

			if(level.getBlockEntity(pos) instanceof LinkableBlockEntity lowerBe)
			{
				LinkableBlockEntity upperBe = ((LinkableBlockEntity) level.getBlockEntity(pos.above()));

				lowerBe.setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
				upperBe.setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
				LinkableBlockEntity.link(lowerBe, upperBe);
			}

			return InteractionResult.SUCCESS;
		}
		else
			return InteractionResult.FAIL;
	}

	public boolean placeDoor(Level level, BlockPos pos, Direction facing, Block door, boolean isRightHinge, UseOnContext ctx) //naming might not be entirely correct, but it's giving a rough idea
	{
		BlockPos posAbove = pos.above();

		if(!level.getBlockState(posAbove).canBeReplaced(new BlockPlaceContext(ctx)))
			return false;

		BlockPos left = pos.relative(facing.getClockWise());
		BlockPos right = pos.relative(facing.getCounterClockWise());
		int rightNormalCubeAmount = (level.getBlockState(right).isRedstoneConductor(level, pos) ? 1 : 0) + (level.getBlockState(right.above()).isRedstoneConductor(level, pos) ? 1 : 0);
		int leftNormalCubeAmount = (level.getBlockState(left).isRedstoneConductor(level, pos) ? 1 : 0) + (level.getBlockState(left.above()).isRedstoneConductor(level, pos) ? 1 : 0);
		boolean isRightDoor = level.getBlockState(right).getBlock() == door || level.getBlockState(right.above()).getBlock() == door;
		boolean isLeftDoor = level.getBlockState(left).getBlock() == door || level.getBlockState(left.above()).getBlock() == door;

		if ((!isRightDoor || isLeftDoor) && leftNormalCubeAmount <= rightNormalCubeAmount)
		{
			if (isLeftDoor && !isRightDoor || leftNormalCubeAmount < rightNormalCubeAmount)
				isRightHinge = false;
		}
		else
			isRightHinge = true;

		BlockState state = door.defaultBlockState().setValue(DoorBlock.FACING, facing).setValue(DoorBlock.HINGE, isRightHinge ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT);

		level.setBlock(pos, state.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), 2);
		level.setBlock(posAbove, state.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), 2);
		level.updateNeighborsAt(pos, door);
		level.updateNeighborsAt(posAbove, door);
		return true;
	}
}
