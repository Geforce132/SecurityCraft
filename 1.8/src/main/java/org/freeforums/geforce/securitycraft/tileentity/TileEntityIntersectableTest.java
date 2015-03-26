package org.freeforums.geforce.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import org.freeforums.geforce.securitycraft.interfaces.IIntersectable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityIntersectableTest extends TileEntity implements IUpdatePlayerListBox {

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
	
	/**
	 * Gets called when a player intersects the block's bounding box.
	 */
	
	public void run(EntityPlayer player){
		((IIntersectable) this.worldObj.getBlockState(getPos()).getBlock()).onEntityIntersected(this.worldObj, getPos(), player);
	}

}
