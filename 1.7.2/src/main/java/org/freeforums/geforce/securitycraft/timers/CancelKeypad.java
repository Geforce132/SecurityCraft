package org.freeforums.geforce.securitycraft.timers;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;

@SuppressWarnings("static-access")
public class CancelKeypad{
		Timer timer;
		
		public CancelKeypad(World world, int x, int y , int z){
			timer = new Timer();
			timer.schedule(new RemindTask(), 5000);
			
			if(((TileEntityKeypad)world.getTileEntity(x, y, z)).isGettingHacked()){
				((TileEntityKeypad)world.getTileEntity(x, y, z)).setIsGettingHacked(false);
			}
			
			mod_SecurityCraft.instance.configHandler.hackingFailed = true;
		}
		class RemindTask extends TimerTask{

			public void run(){
				mod_SecurityCraft.instance.configHandler.hackingFailed = false;
				
				timer.cancel();
			}
		}
	}