package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

public class ContainerGeneric extends Container {

	private TileEntity te;

	public ContainerGeneric()
	{}

	public ContainerGeneric(InventoryPlayer inventory, TileEntity te){
		this.te = te;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		//this is also used for items (e.g. Briefcase), so the te can be null
		return te == null || BlockUtils.isWithinUsableDistance(te.getWorld(), te.getPos(), player, te.getBlockType());
	}

}
