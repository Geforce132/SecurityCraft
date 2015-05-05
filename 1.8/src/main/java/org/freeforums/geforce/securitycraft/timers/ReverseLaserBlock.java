package org.freeforums.geforce.securitycraft.timers;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.Utils.BlockUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public class ReverseLaserBlock{
		Timer timer;
		private int xCoord;
		private int yCoord;
		private int zCoord;
		private World world;
		public ReverseLaserBlock(int seconds, World par1World, BlockPos pos){
			timer = new Timer();
			this.world = par1World;
			this.xCoord = pos.getX();
			this.yCoord = pos.getY();
			this.zCoord = pos.getZ();
			timer.schedule(new RemindTask(), seconds*1000);	
		}
		class RemindTask extends TimerTask{

			public void run(){
				BlockUtils.setBlock(world, new BlockPos(xCoord, yCoord, zCoord), mod_SecurityCraft.LaserBlock);
				world.notifyNeighborsOfStateChange(new BlockPos(xCoord, yCoord, zCoord), mod_SecurityCraft.LaserBlock);
				
				timer.cancel();
			}
		}
	}