package org.freeforums.geforce.securitycraft.timers;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeycardReader;

public class ScheduleKeycardUpdate{
		Timer timer;
		private int xCoord;
		private int yCoord;
		private int zCoord;
		private World world;
		public ScheduleKeycardUpdate(int seconds, World par1World, int par2, int par3, int par4){
			timer = new Timer();
			this.world = par1World;
			this.xCoord = par2;
			this.yCoord = par3;
			this.zCoord = par4;
			timer.schedule(new RemindTask(), seconds*1000); //TODO 60
			
			
			
		}
		class RemindTask extends TimerTask{

			public void run(){
				((TileEntityKeycardReader)world.getTileEntity(new BlockPos(xCoord, yCoord, zCoord))).setIsProvidingPower(false);
				world.notifyNeighborsOfStateChange(new BlockPos(xCoord, yCoord, zCoord), mod_SecurityCraft.keycardReader);
				world.markBlockForUpdate(new BlockPos(xCoord, yCoord, zCoord));
				timer.cancel();
			}
		}
	}