package org.freeforums.geforce.securitycraft.timers;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.blocks.BlockKeypad;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;

public class ScheduleUpdate{
		Timer timer;
		private World world;
		private int x;
		private int y;
		private int z;
		private String code;

		public ScheduleUpdate(World par1World, int seconds, int par3, int par4, int par5){
			timer = new Timer();
			timer.schedule(new RemindTask(), seconds*1000); //TODO 60
			world = par1World;
			x = par3;
			y = par4;
			z = par5;
			code = ((TileEntityKeypad) world.getTileEntity(new BlockPos(par3, par4, par5))).getKeypadCode();
			
			if(Utils.getBlock(par1World, par3, par4, par5) == mod_SecurityCraft.keypad){
				Utils.setBlockProperty(par1World, new BlockPos(par3, par4, par5), BlockKeypad.POWERED, true, true);
				((TileEntityKeypad) world.getTileEntity(new BlockPos(par3, par4, par5))).setKeypadCode(code);
				world.notifyNeighborsOfStateChange(new BlockPos(par3, par4, par5), mod_SecurityCraft.keypad);
			}
		}
		class RemindTask extends TimerTask{

			public void run(){
				if(Utils.getBlock(world, x, y, z) == mod_SecurityCraft.keypad){
					String code = ((TileEntityKeypad) world.getTileEntity(new BlockPos(x, y, z))).getKeypadCode();
					Utils.setBlockProperty(world, new BlockPos(x, y, z), BlockKeypad.POWERED, false, true);
					((TileEntityKeypad) world.getTileEntity(new BlockPos(x, y, z))).setKeypadCode(code);
					world.notifyNeighborsOfStateChange(new BlockPos(x, y, z), mod_SecurityCraft.keypad);
				}

				timer.cancel();
			}
		}
	}