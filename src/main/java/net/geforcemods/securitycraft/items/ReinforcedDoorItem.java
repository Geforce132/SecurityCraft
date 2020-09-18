package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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

public class ReinforcedDoorItem extends Item
{
	public ReinforcedDoorItem(Item.Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getItem(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z, ctx);
	}

	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ, ItemUseContext ctx)
	{
		if(world.isRemote)
			return ActionResultType.FAIL;

		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (!block.isReplaceable(world.getBlockState(pos), new BlockItemUseContext(ctx)))
			pos = pos.offset(facing);

		if (player.canPlayerEdit(pos, facing, stack) && BlockUtils.isSideSolid(world, pos.down(), Direction.UP))
		{
			Direction angleFacing = Direction.fromAngle(player.rotationYaw);
			int offsetX = angleFacing.getXOffset();
			int offsetZ = angleFacing.getZOffset();
			boolean flag = offsetX < 0 && hitZ < 0.5F || offsetX > 0 && hitZ > 0.5F || offsetZ < 0 && hitX > 0.5F || offsetZ > 0 && hitX < 0.5F;

			if(!placeDoor(world, pos, angleFacing, SCContent.REINFORCED_DOOR.get(), flag))
				return ActionResultType.FAIL;

			SoundType soundtype = world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world, pos, player);

			world.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

			if(!player.isCreative())
				stack.shrink(1);

			if(world.getTileEntity(pos) != null)
			{
				((OwnableTileEntity) world.getTileEntity(pos)).getOwner().set(player.getGameProfile().getId().toString(), player.getName());
				((OwnableTileEntity) world.getTileEntity(pos.up())).getOwner().set(player.getGameProfile().getId().toString(), player.getName());
			}

			return ActionResultType.SUCCESS;
		}
		else
			return ActionResultType.FAIL;
	}

	public boolean placeDoor(World world, BlockPos pos, Direction facing, Block door, boolean isRightHinge) //naming might not be entirely correct, but it's giving a rough idea
	{
		BlockPos posAbove = pos.up();

		if(!world.getBlockState(posAbove).isAir(world, posAbove))
			return false;

		BlockPos left = pos.offset(facing.rotateY());
		BlockPos right = pos.offset(facing.rotateYCCW());
		int rightNormalCubeAmount = (world.getBlockState(right).isNormalCube(world, pos) ? 1 : 0) + (world.getBlockState(right.up()).isNormalCube(world, pos) ? 1 : 0);
		int leftNormalCubeAmount = (world.getBlockState(left).isNormalCube(world, pos) ? 1 : 0) + (world.getBlockState(left.up()).isNormalCube(world, pos) ? 1 : 0);
		boolean isRightDoor = world.getBlockState(right).getBlock() == door || world.getBlockState(right.up()).getBlock() == door;
		boolean isLeftDoor = world.getBlockState(left).getBlock() == door || world.getBlockState(left.up()).getBlock() == door;

		if ((!isRightDoor || isLeftDoor) && leftNormalCubeAmount <= rightNormalCubeAmount)
		{
			if (isLeftDoor && !isRightDoor || leftNormalCubeAmount < rightNormalCubeAmount)
				isRightHinge = false;
		}
		else
			isRightHinge = true;

		boolean isAnyPowered = world.isBlockPowered(pos) || world.isBlockPowered(posAbove);
		BlockState state = door.getDefaultState().with(ReinforcedDoorBlock.FACING, facing).with(ReinforcedDoorBlock.HINGE, isRightHinge ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT).with(ReinforcedDoorBlock.OPEN, isAnyPowered);

		world.setBlockState(pos, state.with(ReinforcedDoorBlock.HALF, DoubleBlockHalf.LOWER), 2);
		world.setBlockState(posAbove, state.with(ReinforcedDoorBlock.HALF, DoubleBlockHalf.UPPER), 2);
		world.notifyNeighborsOfStateChange(pos, door);
		world.notifyNeighborsOfStateChange(posAbove, door);
		return true;
	}
}
