package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.blocks.BlockKeycardReader;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.geforcemods.securitycraft.tileentity.TileEntityRetinalScanner;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ModuleUtils{

	/**
	 * Insert a module into a customizable TileEntity. <p>
	 *
	 * Args: world, x, y, z, moduleType.
	 */
	public static void insertModule(World world, int x, int y, int z, EnumCustomModules module){
		((CustomizableSCTE) world.getTileEntity(x, y, z)).insertModule(module);
	}

	/**
	 * Used by the Laser Block to insert modules in all directions.
	 *
	 * Args: world, x, y, z, direction, blockToCheckFor, range, module, updateAdjacentBlocks
	 */
	public static void checkForBlockAndInsertModule(World world, int x, int y, int z, String dir, Block blockToCheckFor, int range, ItemStack module, boolean updateAdjacentBlocks){
		for(int i = 1; i <= range; i++)
			if(dir.equalsIgnoreCase("x+")){
				if(world.getBlock(x + i, y, z) == blockToCheckFor && !((CustomizableSCTE) world.getTileEntity(x + i, y, z)).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) world.getTileEntity(x + i, y, z)).insertModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndInsertModule(world, x + i, y, z, blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("x-")){
				if(world.getBlock(x - i, y, z) == blockToCheckFor && !((CustomizableSCTE) world.getTileEntity(x - i, y, z)).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) world.getTileEntity(x - i, y, z)).insertModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndInsertModule(world, x - i, y, z, blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("y+")){
				if(world.getBlock(x, y + i, z) == blockToCheckFor && !((CustomizableSCTE) world.getTileEntity(x, y + i, z)).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) world.getTileEntity(x, y + i, z)).insertModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndInsertModule(world, x, y + i, z, blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("y-")){
				if(world.getBlock(x, y - i, z) == blockToCheckFor && !((CustomizableSCTE) world.getTileEntity(x, y - i, z)).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) world.getTileEntity(x, y - i, z)).insertModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndInsertModule(world, x, y - i, z, blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("z+")){
				if(world.getBlock(x, y, z + i) == blockToCheckFor && !((CustomizableSCTE) world.getTileEntity(x, y, z + i)).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) world.getTileEntity(x, y, z + i)).insertModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndInsertModule(world, x, y, z + i, blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("z-"))
				if(world.getBlock(x, y, z - i) == blockToCheckFor && !((CustomizableSCTE) world.getTileEntity(x, y, z - i)).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) world.getTileEntity(x, y, z - i)).insertModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndInsertModule(world, x, y, z - i, blockToCheckFor, range, module, updateAdjacentBlocks);
				}
	}

	public static void checkInAllDirsAndInsertModule(World world, int x, int y, int z, Block blockToCheckFor, int range, ItemStack module, boolean updateAdjacentBlocks){
		checkForBlockAndInsertModule(world, x, y, z, "x+", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndInsertModule(world, x, y, z, "x-", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndInsertModule(world, x, y, z, "y+", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndInsertModule(world, x, y, z, "y-", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndInsertModule(world, x, y, z, "z+", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndInsertModule(world, x, y, z, "z-", blockToCheckFor, range, module, updateAdjacentBlocks);
	}

	/**
	 * Used by the Laser Block to remove modules in all directions.
	 *
	 * Args: world, x, y, z, direction, blockToCheckFor, range, module, updateAdjacentBlocks
	 */
	public static void checkForBlockAndRemoveModule(World world, int x, int y, int z, String dir, Block blockToCheckFor, int range, EnumCustomModules module, boolean updateAdjacentBlocks){
		for(int i = 1; i <= range; i++)
			if(dir.equalsIgnoreCase("x+")){
				if(world.getBlock(x + i, y, z) == blockToCheckFor && ((CustomizableSCTE) world.getTileEntity(x + i, y, z)).hasModule(module)){
					((CustomizableSCTE) world.getTileEntity(x + i, y, z)).removeModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndRemoveModule(world, x + i, y, z, blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("x-")){
				if(world.getBlock(x - i, y, z) == blockToCheckFor && ((CustomizableSCTE) world.getTileEntity(x - i, y, z)).hasModule(module)){
					((CustomizableSCTE) world.getTileEntity(x - i, y, z)).removeModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndRemoveModule(world, x - i, y, z, blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("y+")){
				if(world.getBlock(x, y + i, z) == blockToCheckFor && ((CustomizableSCTE) world.getTileEntity(x, y + i, z)).hasModule(module)){
					((CustomizableSCTE) world.getTileEntity(x, y + i, z)).removeModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndRemoveModule(world, x, y + i, z, blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("y-")){
				if(world.getBlock(x, y - i, z) == blockToCheckFor && ((CustomizableSCTE) world.getTileEntity(x, y - i, z)).hasModule(module)){
					((CustomizableSCTE) world.getTileEntity(x, y - i, z)).removeModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndRemoveModule(world, x, y - i, z, blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("z+")){
				if(world.getBlock(x, y, z + i) == blockToCheckFor && ((CustomizableSCTE) world.getTileEntity(x, y, z + i)).hasModule(module)){
					((CustomizableSCTE) world.getTileEntity(x, y, z + i)).removeModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndRemoveModule(world, x, y, z + i, blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("z-"))
				if(world.getBlock(x, y, z - i) == blockToCheckFor && ((CustomizableSCTE) world.getTileEntity(x, y, z - i)).hasModule(module)){
					((CustomizableSCTE) world.getTileEntity(x, y, z - i)).removeModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndRemoveModule(world, x, y, z - i, blockToCheckFor, range, module, updateAdjacentBlocks);
				}
	}

	public static void checkInAllDirsAndRemoveModule(World world, int x, int y, int z, Block blockToCheckFor, int range, EnumCustomModules module, boolean updateAdjacentBlocks){
		checkForBlockAndRemoveModule(world, x, y, z, "x+", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndRemoveModule(world, x, y, z, "x-", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndRemoveModule(world, x, y, z, "y+", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndRemoveModule(world, x, y, z, "y-", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndRemoveModule(world, x, y, z, "z+", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndRemoveModule(world, x, y, z, "z-", blockToCheckFor, range, module, updateAdjacentBlocks);
	}

	/**
	 * Gets the players added to customizable modules (such as the Whitelist Module.) <p>
	 *
	 * Args: world, x, y, z, moduleType.
	 */
	public static List<String> getPlayersFromModule(World world, int x, int y, int z, EnumCustomModules module) {
		List<String> list = new ArrayList<String>();

		CustomizableSCTE te = (CustomizableSCTE) world.getTileEntity(x, y, z);

		if(te.hasModule(module)){
			ItemStack item = te.getModule(module);

			for(int i = 1; i <= 10; i++)
				if(item.stackTagCompound != null && item.stackTagCompound.getString("Player" + i) != null && !item.stackTagCompound.getString("Player" + i).isEmpty())
					list.add(item.stackTagCompound.getString("Player" + i).toLowerCase());
		}

		return list;
	}

	/**
	 * A large block of code that checks if the TileEntity at the specified coordinates has the given module inserted, and what should happen if it does. <p>
	 *
	 * Args: world, x, y, z, player, moduleType.
	 */
	public static boolean checkForModule(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, EnumCustomModules module){
		TileEntity te = world.getTileEntity(x, y, z);

		if(te == null || !(te instanceof CustomizableSCTE))
			return false;

		if(te instanceof TileEntityKeypad){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && getPlayersFromModule(world, x, y, z, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, StatCollector.translateToLocal("tile.keypad.name"), StatCollector.translateToLocal("messages.module.whitelisted"), EnumChatFormatting.GREEN);
				BlockKeypad.activate(world, x, y, z);
				return true;
			}

			if(module == EnumCustomModules.BLACKLIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.BLACKLIST) && getPlayersFromModule(world, x, y, z, EnumCustomModules.BLACKLIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, StatCollector.translateToLocal("tile.keypad.name"), StatCollector.translateToLocal("messages.module.blacklisted"), EnumChatFormatting.RED);
				return true;
			}
		}else if(te instanceof TileEntityKeycardReader){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && getPlayersFromModule(world, x, y, z, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, StatCollector.translateToLocal("tile.keycardReader.name"), StatCollector.translateToLocal("messages.module.whitelisted"), EnumChatFormatting.GREEN);
				BlockKeycardReader.activate(world, x, y, z);
				return true;
			}

			if(module == EnumCustomModules.BLACKLIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.BLACKLIST) && getPlayersFromModule(world, x, y, z, EnumCustomModules.BLACKLIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, StatCollector.translateToLocal("tile.keycardReader.name"), StatCollector.translateToLocal("messages.module.blacklisted"), EnumChatFormatting.RED);
				return true;
			}
		}else if(te instanceof TileEntityRetinalScanner){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && getPlayersFromModule(world, x, y, z, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase()))
				return true;
		}else if(te instanceof TileEntityInventoryScanner)
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && getPlayersFromModule(world, x, y, z, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase()))
				return true;

		return false;
	}
}