package net.geforcemods.securitycraft.compat.icbmclassic;

import icbm.classic.api.events.EmpEvent;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.tileentity.IEMPAffected;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ICBMClassicEMPCompat
{
	@SubscribeEvent
	public void onEmpBlockPost(EmpEvent.BlockPost event)
	{
		TileEntity te = event.world.getTileEntity(event.blockPos);

		if(te instanceof IEMPAffected)
			((IEMPAffected)te).shutDown();
	}

	@SubscribeEvent
	public void onEmpEntityPost(EmpEvent.EntityPost event)
	{
		if(event.target instanceof EntitySecurityCamera)
		{
			event.target.removePassengers();
			event.target.setDead();
		}
	}
}
