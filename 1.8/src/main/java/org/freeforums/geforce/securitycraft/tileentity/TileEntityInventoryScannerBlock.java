package org.freeforums.geforce.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import org.freeforums.geforce.securitycraft.blocks.BlockInventoryScannerBlock;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityInventoryScannerBlock extends TileEntityIntersectable {

	public void update() {
		int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1)));
        List list = this.worldObj.getEntitiesWithinAABB(Entity.class, axisalignedbb);
        Iterator iterator = list.iterator();
        Entity entity;

        while (iterator.hasNext())
        {
            entity = (Entity)iterator.next();
            run(entity);
        }
	}
	
	public void run(EntityPlayer player) {}
	
	public void run(Entity entity) {
		BlockInventoryScannerBlock.checkForEntity(getWorld(), getPos(), entity);
	}

}
