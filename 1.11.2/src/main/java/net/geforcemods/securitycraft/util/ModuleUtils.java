package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.geforcemods.securitycraft.tileentity.TileEntityRetinalScanner;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ModuleUtils{
	
	public static void insertModule(World par1World, BlockPos pos, EnumCustomModules module){
		((CustomizableSCTE) par1World.getTileEntity(pos)).insertModule(module);
	}
	
	public static void removeModule(World par1World, BlockPos pos, EnumCustomModules module){
		((CustomizableSCTE) par1World.getTileEntity(pos)).removeModule(module);
	}
	
	public static void checkForBlockAndInsertModule(World par1World, BlockPos pos, String dir, Block blockToCheckFor, int range, ItemStack module, boolean updateAdjecentBlocks){
		for(int i = 1; i <= range; i++){
			if(dir.equalsIgnoreCase("x+")){
				if(par1World.getBlockState(pos.east(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.east(i))).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) par1World.getTileEntity(pos.east(i))).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, pos.east(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("x-")){
				if(par1World.getBlockState(pos.west(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.west(i))).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) par1World.getTileEntity(pos.west(i))).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, pos.west(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y+")){
				if(par1World.getBlockState(pos.up(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.up(i))).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) par1World.getTileEntity(pos.up(i))).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, pos.up(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y-")){
				if(par1World.getBlockState(pos.down(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.down(i))).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) par1World.getTileEntity(pos.down(i))).insertModule(module);
					if(updateAdjecentBlocks){ 
						checkInAllDirsAndInsertModule(par1World, pos.down(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z+")){
				if(par1World.getBlockState(pos.south(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.south(i))).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) par1World.getTileEntity(pos.south(i))).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, pos.south(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z-")){
				if(par1World.getBlockState(pos.north(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.north(i))).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) par1World.getTileEntity(pos.north(i))).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, pos.north(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}
		}
	}
	
	public static void checkInAllDirsAndInsertModule(World par1World, BlockPos pos, Block blockToCheckFor, int range, ItemStack module, boolean updateAdjecentBlocks){
		checkForBlockAndInsertModule(par1World, pos, "x+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, pos, "x-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, pos, "y+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, pos, "y-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, pos, "z+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, pos, "z-", blockToCheckFor, range, module, updateAdjecentBlocks);
	}
	
	public static void checkForBlockAndRemoveModule(World par1World, BlockPos pos, String dir, Block blockToCheckFor, int range, EnumCustomModules module, boolean updateAdjecentBlocks){
		for(int i = 1; i <= range; i++){
			if(dir.equalsIgnoreCase("x+")){
				if(par1World.getBlockState(pos.east(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(pos.east(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.east(i))).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, pos.east(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("x-")){
				if(par1World.getBlockState(pos.west(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(pos.west(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.west(i))).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, pos.west(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y+")){
				if(par1World.getBlockState(pos.up(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(pos.up(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.up(i))).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, pos.up(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y-")){
				if(par1World.getBlockState(pos.down(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(pos.down(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.down(i))).removeModule(module);
					if(updateAdjecentBlocks){ 
						checkInAllDirsAndRemoveModule(par1World, pos.down(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z+")){
				if(par1World.getBlockState(pos.south(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(pos.south(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.south(i))).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, pos.south(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z-")){
				if(par1World.getBlockState(pos.north(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(pos.north(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.north(i))).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, pos.north(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}
		}
	}
	
	public static void checkInAllDirsAndRemoveModule(World par1World, BlockPos pos, Block blockToCheckFor, int range, EnumCustomModules module, boolean updateAdjecentBlocks){
		checkForBlockAndRemoveModule(par1World, pos, "x+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, pos, "x-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, pos, "y+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, pos, "y-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, pos, "z+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, pos, "z-", blockToCheckFor, range, module, updateAdjecentBlocks);
	}
	
	public static List<String> getPlayersFromModule(World par1World, BlockPos pos, EnumCustomModules module) {
		List<String> list = new ArrayList<String>();
		
		CustomizableSCTE te = (CustomizableSCTE) par1World.getTileEntity(pos);
		
		if(te.hasModule(module)){
			ItemStack item = te.getModule(module);
						
			for(int i = 1; i <= 10; i++){
				if(item.getTagCompound() != null && item.getTagCompound().getString("Player" + i) != null && !item.getTagCompound().getString("Player" + i).isEmpty()){
					list.add(item.getTagCompound().getString("Player" + i).toLowerCase());
				}
			}
		}
		
		return list;
	}
	
	public static boolean checkForModule(World par1World, BlockPos pos, EntityPlayer par5EntityPlayer, EnumCustomModules module){
		TileEntity te = par1World.getTileEntity(pos);
		
		if(te == null || !(te instanceof CustomizableSCTE)){ return false; }
		
		if(te instanceof TileEntityKeypad){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(par1World, pos, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, ClientUtils.localize("tile.keypad.name"), ClientUtils.localize("messages.module.whitelisted"), TextFormatting.GREEN);
				return true;
			}
			
			if(module == EnumCustomModules.BLACKLIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.BLACKLIST) && ModuleUtils.getPlayersFromModule(par1World, pos, EnumCustomModules.BLACKLIST).contains(par5EntityPlayer.getName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, ClientUtils.localize("tile.keypad.name"), ClientUtils.localize("messages.module.blacklisted"), TextFormatting.RED);
				return true;
			}
		}else if(te instanceof TileEntityKeycardReader){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(par1World, pos, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, ClientUtils.localize("tile.keycardReader.name"), ClientUtils.localize("messages.module.whitelisted"), TextFormatting.GREEN);
				par1World.notifyNeighborsOfStateChange(pos, par1World.getBlockState(pos).getBlock(), false);
				return true;
			}
			
			if(module == EnumCustomModules.BLACKLIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.BLACKLIST) && ModuleUtils.getPlayersFromModule(par1World, pos, EnumCustomModules.BLACKLIST).contains(par5EntityPlayer.getName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, ClientUtils.localize("tile.keycardReader.name"), ClientUtils.localize("messages.module.blacklisted"), TextFormatting.RED);
				return true;
			}
		}else if(te instanceof TileEntityRetinalScanner){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(par1World, pos, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getName().toLowerCase())){
				return true;
			}
		}else if(te instanceof TileEntityInventoryScanner){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(par1World, pos, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getName().toLowerCase())){
				return true;
			}
		}
		
		return false;
	}
}