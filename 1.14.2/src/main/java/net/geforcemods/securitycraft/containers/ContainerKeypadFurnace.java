package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.blocks.BlockKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.FurnaceContainer;

public class ContainerKeypadFurnace extends FurnaceContainer{

	private TileEntityKeypadFurnace te;

	public ContainerKeypadFurnace(PlayerInventory player, TileEntityKeypadFurnace te) {
		super(player, te);
		this.te = te;
	}

	@Override
	public boolean canInteractWith(PlayerEntity player){
		return true;
	}

	@Override
	public void onContainerClosed(PlayerEntity player)
	{
		te.getWorld().setBlockState(te.getPos(), te.getBlockState().with(BlockKeypadFurnace.OPEN, false));
	}
}