package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.BlockKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.util.BlockUtils;
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
		return BlockUtils.isWithinUsableDistance(te.getWorld(), te.getPos(), player, SCContent.keypadFurnace);
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		te.getWorld().setBlockState(te.getPos(), te.getWorld().getBlockState(te.getPos()).withProperty(BlockKeypadFurnace.OPEN, false));
	}
}