package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class ReinforcedDoorItem extends BlockItem
{
	public ReinforcedDoorItem(Block block, Item.Properties properties)
	{
		super(block, properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getLevel(), ctx.getClickedPos(), ctx.getItemInHand(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx);
	}

	public InteractionResult onItemUse(Player player, Level world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ, UseOnContext ctx)
	{
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (!block.canBeReplaced(world.getBlockState(pos), new BlockPlaceContext(ctx)))
			pos = pos.relative(facing);

		if (player.mayUseItemAt(pos, facing, stack) && BlockUtils.isSideSolid(world, pos.below(), Direction.UP))
		{
			Direction angleFacing = Direction.fromYRot(player.getYRot());
			int offsetX = angleFacing.getStepX();
			int offsetZ = angleFacing.getStepZ();
			boolean flag = offsetX < 0 && hitZ < 0.5F || offsetX > 0 && hitZ > 0.5F || offsetZ < 0 && hitX > 0.5F || offsetZ > 0 && hitX < 0.5F;

			if(!placeDoor(world, pos, angleFacing, getBlock(), flag, ctx))
				return InteractionResult.FAIL;

			SoundType soundtype = world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world, pos, player);

			world.playSound(null, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

			if(!player.isCreative())
				stack.shrink(1);

			if(world.getBlockEntity(pos) instanceof OwnableBlockEntity te)
			{
				te.setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
				((OwnableBlockEntity) world.getBlockEntity(pos.above())).setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
			}

			return InteractionResult.SUCCESS;
		}
		else
			return InteractionResult.FAIL;
	}

	public boolean placeDoor(Level world, BlockPos pos, Direction facing, Block door, boolean isRightHinge, UseOnContext ctx) //naming might not be entirely correct, but it's giving a rough idea
	{
		BlockPos posAbove = pos.above();

		if(!world.getBlockState(posAbove).canBeReplaced(new BlockPlaceContext(ctx)))
			return false;

		BlockPos left = pos.relative(facing.getClockWise());
		BlockPos right = pos.relative(facing.getCounterClockWise());
		int rightNormalCubeAmount = (world.getBlockState(right).isRedstoneConductor(world, pos) ? 1 : 0) + (world.getBlockState(right.above()).isRedstoneConductor(world, pos) ? 1 : 0);
		int leftNormalCubeAmount = (world.getBlockState(left).isRedstoneConductor(world, pos) ? 1 : 0) + (world.getBlockState(left.above()).isRedstoneConductor(world, pos) ? 1 : 0);
		boolean isRightDoor = world.getBlockState(right).getBlock() == door || world.getBlockState(right.above()).getBlock() == door;
		boolean isLeftDoor = world.getBlockState(left).getBlock() == door || world.getBlockState(left.above()).getBlock() == door;

		if ((!isRightDoor || isLeftDoor) && leftNormalCubeAmount <= rightNormalCubeAmount)
		{
			if (isLeftDoor && !isRightDoor || leftNormalCubeAmount < rightNormalCubeAmount)
				isRightHinge = false;
		}
		else
			isRightHinge = true;

		boolean isAnyPowered = BlockUtils.hasActiveSCBlockNextTo(world, pos) || BlockUtils.hasActiveSCBlockNextTo(world, posAbove);
		BlockState state = door.defaultBlockState().setValue(ReinforcedDoorBlock.FACING, facing).setValue(ReinforcedDoorBlock.HINGE, isRightHinge ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT).setValue(ReinforcedDoorBlock.OPEN, isAnyPowered);

		world.setBlock(pos, state.setValue(ReinforcedDoorBlock.HALF, DoubleBlockHalf.LOWER), 2);
		world.setBlock(posAbove, state.setValue(ReinforcedDoorBlock.HALF, DoubleBlockHalf.UPPER), 2);
		world.updateNeighborsAt(pos, door);
		world.updateNeighborsAt(posAbove, door);
		return true;
	}
}
