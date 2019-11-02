package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.blocks.BlockKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;

public class ContainerKeypadFurnace extends ContainerFurnace{

	private TileEntityKeypadFurnace te;

	public ContainerKeypadFurnace(InventoryPlayer player, TileEntityKeypadFurnace te) {
		super(player, te);
		this.te = te;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player){
		return true;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		te.getWorld().setBlockState(te.getPos(), te.getBlockState().with(BlockKeypadFurnace.OPEN, false));
	}
}