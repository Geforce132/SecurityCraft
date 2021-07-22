package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class SpecialDoorItem extends Item
{
	public SpecialDoorItem(Item.Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResultType useOn(ItemUseContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getLevel(), ctx.getClickedPos(), ctx.getItemInHand(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx);
	}

	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ, ItemUseContext ctx)
	{
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (!block.canBeReplaced(state, new BlockItemUseContext(ctx)))
			pos = pos.relative(facing);

		if (player.mayUseItemAt(pos, facing, stack) && BlockUtils.isSideSolid(world, pos.below(), Direction.UP))
		{
			Direction angleFacing = Direction.fromYRot(player.yRot);
			int offsetX = angleFacing.getStepX();
			int offsetZ = angleFacing.getStepZ();
			boolean flag = offsetX < 0 && hitZ < 0.5F || offsetX > 0 && hitZ > 0.5F || offsetZ < 0 && hitX > 0.5F || offsetZ > 0 && hitX < 0.5F;

			if(!placeDoor(world, pos, angleFacing, getDoorBlock(), flag, ctx))
				return ActionResultType.FAIL;

			SoundType soundtype = world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world, pos, player);

			world.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

			if(!player.isCreative())
				stack.shrink(1);

			if(world.getBlockEntity(pos) != null)
			{
				CustomizableTileEntity lowerTe = ((CustomizableTileEntity) world.getBlockEntity(pos));
				CustomizableTileEntity upperTe = ((CustomizableTileEntity) world.getBlockEntity(pos.above()));

				lowerTe.setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
				upperTe.setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
				CustomizableTileEntity.link(lowerTe, upperTe);
			}

			return ActionResultType.SUCCESS;
		}
		else
			return ActionResultType.FAIL;
	}

	public boolean placeDoor(World world, BlockPos pos, Direction facing, Block door, boolean isRightHinge, ItemUseContext ctx) //naming might not be entirely correct, but it's giving a rough idea
	{
		BlockPos posAbove = pos.above();

		if(!world.getBlockState(posAbove).canBeReplaced(new BlockItemUseContext(ctx)))
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

		BlockState state = door.defaultBlockState().setValue(DoorBlock.FACING, facing).setValue(DoorBlock.HINGE, isRightHinge ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT);

		world.setBlock(pos, state.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), 2);
		world.setBlock(posAbove, state.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), 2);
		world.updateNeighborsAt(pos, door);
		world.updateNeighborsAt(posAbove, door);
		return true;
	}

	public abstract Block getDoorBlock();
}
