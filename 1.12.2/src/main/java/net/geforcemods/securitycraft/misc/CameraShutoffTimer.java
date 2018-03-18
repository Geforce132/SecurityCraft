package net.geforcemods.securitycraft.misc;

import java.util.Timer;
import java.util.TimerTask;

import net.geforcemods.securitycraft.tileentity.TileEntityFrame;
import net.minecraft.client.Minecraft;

public class CameraShutoffTimer{
	Timer timer;
	private TileEntityFrame tileEntity;

	public CameraShutoffTimer(TileEntityFrame tileEntity){
		timer = new Timer();
		timer.schedule(new RemindTask(), (60000 * 5)); //300,000 milliseconds, or five minutes.
		this.tileEntity = tileEntity;
	}
	class RemindTask extends TimerTask{

		@Override
		public void run(){
			if(tileEntity != null && Minecraft.getMinecraft().world != null)
				tileEntity.disableView();

			timer.cancel();
		}
	}
}