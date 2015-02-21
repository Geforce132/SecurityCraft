package org.freeforums.geforce.securitycraft.blocks.mines;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.AxisAlignedBB;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityIntersectable;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

public class TileEntityFullMine extends TileEntityOwnable implements IUpdatePlayerListBox{

	public void update() {
		int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1)));
        List list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
        Iterator iterator = list.iterator();
        EntityPlayer entityplayer;

        while (iterator.hasNext())
        {
            entityplayer = (EntityPlayer)iterator.next();
            run(entityplayer);
        }
	}
	
	public void run(EntityPlayer player) {
		BlockFullMineBase.explode(getWorld(), getPos());
	}

}
