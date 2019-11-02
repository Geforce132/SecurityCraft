package org.freeforums.geforce.securitycraft.timers;

import java.util.Timer;
import java.util.TimerTask;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import net.minecraft.world.World;

public class ReverseLaserBlock{
		Timer timer;
		private int xCoord;
		private int yCoord;
		private int zCoord;
		private World world;
		public ReverseLaserBlock(int seconds, World par1World, int par2, int par3, int par4){
			timer = new Timer();
			this.world = par1World;
			this.xCoord = par2;
			this.yCoord = par3;
			this.zCoord = par4;
			timer.schedule(new RemindTask(), seconds*1000);
			
			
			
			
		}
		class RemindTask extends TimerTask{

			public void run(){
				world.setBlock(xCoord, yCoord, zCoord, mod_SecurityCraft.LaserBlock);
				world.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, mod_SecurityCraft.LaserBlock);
				
				timer.cancel();
			}
		}
	}