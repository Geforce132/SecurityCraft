package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.blocks.BlockKeycardReader;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
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
	public static void insertModule(World par1World, int par2, int par3, int par4, EnumCustomModules module){
		((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4)).insertModule(module);
	}

	/**
	 * Used by the Laser Block to insert modules in all directions.
	 * 
	 * Args: world, x, y, z, direction, blockToCheckFor, range, module, updateAdjecentBlocks
	 */
	public static void checkForBlockAndInsertModule(World par1World, int par2, int par3, int par4, String dir, Block blockToCheckFor, int range, ItemStack module, boolean updateAdjecentBlocks){
		for(int i = 1; i <= range; i++){
			if(dir.equalsIgnoreCase("x+")){
				if(par1World.getBlock(par2 + i, par3, par4) == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(par2 + i, par3, par4)).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(par2 + i, par3, par4)).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, par2 + i, par3, par4, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("x-")){
				if(par1World.getBlock(par2 - i, par3, par4) == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(par2 - i, par3, par4)).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(par2 - i, par3, par4)).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, par2 - i, par3, par4, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y+")){
				if(par1World.getBlock(par2, par3 + i, par4) == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(par2, par3 + i, par4)).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(par2, par3 + i, par4)).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, par2, par3 + i, par4, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y-")){
				if(par1World.getBlock(par2, par3 - i, par4) == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(par2, par3 - i, par4)).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(par2, par3 - i, par4)).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, par2, par3 - i, par4, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z+")){
				if(par1World.getBlock(par2, par3, par4 + i) == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4 + i)).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4 + i)).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, par2, par3, par4 + i, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z-")){
				if(par1World.getBlock(par2, par3, par4 - i) == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4 - i)).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4 - i)).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, par2, par3, par4 - i, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}
		}
	}

	public static void checkInAllDirsAndInsertModule(World par1World, int par2, int par3, int par4, Block blockToCheckFor, int range, ItemStack module, boolean updateAdjecentBlocks){
		checkForBlockAndInsertModule(par1World, par2, par3, par4, "x+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, par2, par3, par4, "x-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, par2, par3, par4, "y+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, par2, par3, par4, "y-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, par2, par3, par4, "z+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, par2, par3, par4, "z-", blockToCheckFor, range, module, updateAdjecentBlocks);
	}

	/**
	 * Used by the Laser Block to remove modules in all directions.
	 * 
	 * Args: world, x, y, z, direction, blockToCheckFor, range, module, updateAdjecentBlocks
	 */
	public static void checkForBlockAndRemoveModule(World par1World, int par2, int par3, int par4, String dir, Block blockToCheckFor, int range, EnumCustomModules module, boolean updateAdjecentBlocks){
		for(int i = 1; i <= range; i++){
			if(dir.equalsIgnoreCase("x+")){
				if(par1World.getBlock(par2 + i, par3, par4) == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(par2 + i, par3, par4)).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(par2 + i, par3, par4)).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, par2 + i, par3, par4, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("x-")){
				if(par1World.getBlock(par2 - i, par3, par4) == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(par2 - i, par3, par4)).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(par2 - i, par3, par4)).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, par2 - i, par3, par4, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y+")){
				if(par1World.getBlock(par2, par3 + i, par4) == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(par2, par3 + i, par4)).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(par2, par3 + i, par4)).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, par2, par3 + i, par4, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y-")){
				if(par1World.getBlock(par2, par3 - i, par4) == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(par2, par3 - i, par4)).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(par2, par3 - i, par4)).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, par2, par3 - i, par4, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z+")){
				if(par1World.getBlock(par2, par3, par4 + i) == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4 + i)).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4 + i)).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, par2, par3, par4 + i, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z-")){
				if(par1World.getBlock(par2, par3, par4 - i) == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4 - i)).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4 - i)).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, par2, par3, par4 - i, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}
		}
	}

	public static void checkInAllDirsAndRemoveModule(World par1World, int par2, int par3, int par4, Block blockToCheckFor, int range, EnumCustomModules module, boolean updateAdjecentBlocks){
		checkForBlockAndRemoveModule(par1World, par2, par3, par4, "x+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, par2, par3, par4, "x-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, par2, par3, par4, "y+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, par2, par3, par4, "y-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, par2, par3, par4, "z+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, par2, par3, par4, "z-", blockToCheckFor, range, module, updateAdjecentBlocks);
	}

	/**
	 * Get the {@link ItemModule} instance of the given module type. <p>
	 * 
	 * Args: moduleType.
	 */
	public static ItemModule getItemFromModule(EnumCustomModules module) { //TODO Add any new modules to this list!
		if(module == EnumCustomModules.REDSTONE){
			return mod_SecurityCraft.redstoneModule;
		}else if(module == EnumCustomModules.WHITELIST){
			return mod_SecurityCraft.whitelistModule;
		}else if(module == EnumCustomModules.BLACKLIST){
			return mod_SecurityCraft.blacklistModule;
		}else if(module == EnumCustomModules.HARMING){
			return mod_SecurityCraft.harmingModule;
		}else if(module == EnumCustomModules.SMART){
			return mod_SecurityCraft.smartModule;
		}else if(module == EnumCustomModules.STORAGE){
			return mod_SecurityCraft.storageModule;
		}else{
			return null;
		}
	}


	/**
	 * Gets the players added to customizable modules (such as the Whitelist Module.) <p>
	 * 
	 * Args: world, x, y, z, moduleType.
	 */
	public static List<String> getPlayersFromModule(World par1World, int par2, int par3, int par4, EnumCustomModules module) {
		List<String> list = new ArrayList<String>();

		CustomizableSCTE te = (CustomizableSCTE) par1World.getTileEntity(par2, par3, par4);

		if(te.hasModule(module)){
			ItemStack item = te.getModule(module);

			for(int i = 1; i <= 10; i++){
				if(item.stackTagCompound != null && item.stackTagCompound.getString("Player" + i) != null && !item.stackTagCompound.getString("Player" + i).isEmpty()){
					list.add(item.stackTagCompound.getString("Player" + i).toLowerCase());
				}
			}
		}

		return list;
	}

	/**
	 * A large block of code that checks if the TileEntity at the specified coordinates has the given module inserted, and what should happen if it does. <p>
	 * 
	 * Args: world, x, y, z, player, moduleType.
	 */
	public static boolean checkForModule(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, EnumCustomModules module){
		TileEntity te = par1World.getTileEntity(par2, par3, par4);

		if(te == null || !(te instanceof CustomizableSCTE)){ return false; }

		if(te instanceof TileEntityKeypad){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && getPlayersFromModule(par1World, par2, par3, par4, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, StatCollector.translateToLocal("tile.keypad.name"), StatCollector.translateToLocal("messages.module.whitelisted"), EnumChatFormatting.GREEN);
				BlockKeypad.activate(par1World, par2, par3, par4);
				return true;
			}

			if(module == EnumCustomModules.BLACKLIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.BLACKLIST) && getPlayersFromModule(par1World, par2, par3, par4, EnumCustomModules.BLACKLIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, StatCollector.translateToLocal("tile.keypad.name"), StatCollector.translateToLocal("messages.module.blacklisted"), EnumChatFormatting.RED);
				return true;
			}
		}else if(te instanceof TileEntityKeycardReader){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && getPlayersFromModule(par1World, par2, par3, par4, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, StatCollector.translateToLocal("tile.keycardReader.name"), StatCollector.translateToLocal("messages.module.whitelisted"), EnumChatFormatting.GREEN);
				BlockKeycardReader.activate(par1World, par2, par3, par4);
				return true;
			}

			if(module == EnumCustomModules.BLACKLIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.BLACKLIST) && getPlayersFromModule(par1World, par2, par3, par4, EnumCustomModules.BLACKLIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, StatCollector.translateToLocal("tile.keycardReader.name"), StatCollector.translateToLocal("messages.module.blacklisted"), EnumChatFormatting.RED);
				return true;
			}
		}else if(te instanceof TileEntityRetinalScanner){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && getPlayersFromModule(par1World, par2, par3, par4, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase())){
				return true;
			}
		}else if(te instanceof TileEntityInventoryScanner){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && getPlayersFromModule(par1World, par2, par3, par4, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase())){
				return true;
			}
		}

		return false;
	}
}