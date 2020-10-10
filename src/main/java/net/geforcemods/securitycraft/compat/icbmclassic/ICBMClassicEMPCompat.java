package net.geforcemods.securitycraft.compat.icbmclassic;

import icbm.classic.api.events.EmpEvent;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ICBMClassicEMPCompat
{
	@SubscribeEvent
	public void onEmpBlockPost(EmpEvent.BlockPost event)
	{
		if(event.state.getBlock() == SCContent.securityCamera)
		{
			TileEntity te = event.world.getTileEntity(event.blockPos);

			if(te instanceof TileEntitySecurityCamera)
				((TileEntitySecurityCamera)te).shutDown();
		}
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
