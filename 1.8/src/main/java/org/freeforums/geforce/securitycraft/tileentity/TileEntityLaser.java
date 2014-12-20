package org.freeforums.geforce.securitycraft.tileentity;

import net.minecraft.entity.player.EntityPlayer;

import org.freeforums.geforce.securitycraft.blocks.BlockLaser;

public class TileEntityLaser extends TileEntityIntersectable {

	public void run(EntityPlayer player) {
		BlockLaser.checkForPlayer(worldObj, getPos(), player);
	}

}
