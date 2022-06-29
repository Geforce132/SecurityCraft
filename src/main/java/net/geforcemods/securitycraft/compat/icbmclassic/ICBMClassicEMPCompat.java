package net.geforcemods.securitycraft.compat.icbmclassic;

import icbm.classic.api.events.EmpEvent;
import net.geforcemods.securitycraft.api.IEMPAffected;
import net.geforcemods.securitycraft.entity.camera.EntitySecurityCamera;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ICBMClassicEMPCompat {
	@SubscribeEvent
	public void onEmpBlockPost(EmpEvent.BlockPost event) {
		TileEntity te = event.world.getTileEntity(event.blockPos);

		if (te instanceof IEMPAffected)
			((IEMPAffected) te).shutDown();
	}

	@SubscribeEvent
	public void onEmpEntityPost(EmpEvent.EntityPost event) {
		Entity entity = event.target;

		if (entity instanceof IEMPAffected)
			((IEMPAffected) entity).shutDown();

		if (entity instanceof EntitySecurityCamera) {
			entity.removePassengers();
			entity.setDead();
		}
	}
}
