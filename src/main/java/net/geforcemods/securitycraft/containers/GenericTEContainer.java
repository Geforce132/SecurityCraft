package net.geforcemods.securitycraft.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GenericTEContainer extends Container {
	public final TileEntity te;
	private IWorldPosCallable worldPosCallable;

	public GenericTEContainer(ContainerType<GenericTEContainer> type, int windowId, World world, BlockPos pos)
	{
		super(type, windowId);

		te = world.getTileEntity(pos);
		worldPosCallable = IWorldPosCallable.of(world, pos);
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return isWithinUsableDistance(worldPosCallable, player, te.getBlockState().getBlock());
	}
}
