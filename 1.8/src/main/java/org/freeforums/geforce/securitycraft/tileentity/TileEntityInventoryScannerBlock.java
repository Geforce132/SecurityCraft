package org.freeforums.geforce.securitycraft.tileentity;

import org.freeforums.geforce.securitycraft.blocks.BlockInventoryScannerBlock;

import net.minecraft.entity.player.EntityPlayer;

public class TileEntityInventoryScannerBlock extends TileEntityIntersectable {

	public void run(EntityPlayer player) {
		BlockInventoryScannerBlock.checkForPlayer(getWorld(), getPos(), player);
	}

}
