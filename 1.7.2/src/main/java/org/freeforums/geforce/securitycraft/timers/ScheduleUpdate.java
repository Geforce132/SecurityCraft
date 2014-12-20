package org.freeforums.geforce.securitycraft.timers;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public class ScheduleUpdate{
		Timer timer;
		private World world;
		private int x;
		private int y;
		private int z;
		private int metadata;

		public ScheduleUpdate(World par1World, int seconds, int par3, int par4, int par5){
			timer = new Timer();
			timer.schedule(new RemindTask(), seconds*1000); //TODO 60
			world = par1World;
			x = par3;
			y = par4;
			z = par5;

			if(world.getBlock(par3, par4, par5) == mod_SecurityCraft.Keypad){
				metadata = world.getBlockMetadata(par3, par4 , par5);
				world.setBlockMetadataWithNotify(par3, par4, par5, metadata + 5, 3);
				world.notifyBlocksOfNeighborChange(par3, par4, par5, mod_SecurityCraft.Keypad);
			}
		}
		class RemindTask extends TimerTask{

			public void run(){
				if(world.getBlock(x, y, z) == mod_SecurityCraft.Keypad){
					world.setBlockMetadataWithNotify(x, y, z, metadata, 3);
					world.notifyBlocksOfNeighborChange(x, y, z, mod_SecurityCraft.Keypad);
				}

				timer.cancel();
			}
		}
	}