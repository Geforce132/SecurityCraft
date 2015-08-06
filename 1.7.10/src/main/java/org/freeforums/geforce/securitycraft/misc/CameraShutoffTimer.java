package org.freeforums.geforce.securitycraft.misc;

import java.util.Timer;
import java.util.TimerTask;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityFrame;

public class CameraShutoffTimer{
		Timer timer;
		private TileEntityFrame tileEntity;
		
		public CameraShutoffTimer(TileEntityFrame tileEntity){
			timer = new Timer();
			timer.schedule(new RemindTask(), (60000 * 5)); //300,000 milliseconds, or five minutes.
			this.tileEntity = tileEntity;
		}
		class RemindTask extends TimerTask{

			public void run(){
				tileEntity.disableView();
				
				timer.cancel();
			}
		}
	}