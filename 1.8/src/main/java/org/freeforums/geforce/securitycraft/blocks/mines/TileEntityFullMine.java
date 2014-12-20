package org.freeforums.geforce.securitycraft.blocks.mines;

import net.minecraft.entity.player.EntityPlayer;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityIntersectable;

public class TileEntityFullMine extends TileEntityIntersectable {

	public void run(EntityPlayer player) {
		BlockFullMineBase.explode(getWorld(), getPos());
	}

}
