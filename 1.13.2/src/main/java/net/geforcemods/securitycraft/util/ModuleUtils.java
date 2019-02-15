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
	//North: Z-  South: Z+  East: X+  West: X-  Up: Y+  Down: Y-

	public static void checkForBlockAndInsertModule(World world, BlockPos pos, String dir, Block blockToCheckFor, int range, ItemStack module, boolean updateAdjacentBlocks){
		for(int i = 1; i <= range; i++)
			if(dir.equalsIgnoreCase("x+")){
				if(world.getBlockState(pos.east(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) world.getTileEntity(pos.east(i))).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) world.getTileEntity(pos.east(i))).insertModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndInsertModule(world, pos.east(i), blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("x-")){
				if(world.getBlockState(pos.west(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) world.getTileEntity(pos.west(i))).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) world.getTileEntity(pos.west(i))).insertModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndInsertModule(world, pos.west(i), blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("y+")){
				if(world.getBlockState(pos.up(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) world.getTileEntity(pos.up(i))).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) world.getTileEntity(pos.up(i))).insertModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndInsertModule(world, pos.up(i), blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("y-")){
				if(world.getBlockState(pos.down(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) world.getTileEntity(pos.down(i))).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) world.getTileEntity(pos.down(i))).insertModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndInsertModule(world, pos.down(i), blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("z+")){
				if(world.getBlockState(pos.south(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) world.getTileEntity(pos.south(i))).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) world.getTileEntity(pos.south(i))).insertModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndInsertModule(world, pos.south(i), blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("z-"))
				if(world.getBlockState(pos.north(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) world.getTileEntity(pos.north(i))).hasModule(EnumCustomModules.getModuleFromStack(module))){
					((CustomizableSCTE) world.getTileEntity(pos.north(i))).insertModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndInsertModule(world, pos.north(i), blockToCheckFor, range, module, updateAdjacentBlocks);
				}
	}

	public static void checkInAllDirsAndInsertModule(World world, BlockPos pos, Block blockToCheckFor, int range, ItemStack module, boolean updateAdjacentBlocks){
		checkForBlockAndInsertModule(world, pos, "x+", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndInsertModule(world, pos, "x-", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndInsertModule(world, pos, "y+", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndInsertModule(world, pos, "y-", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndInsertModule(world, pos, "z+", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndInsertModule(world, pos, "z-", blockToCheckFor, range, module, updateAdjacentBlocks);
	}

	public static void checkForBlockAndRemoveModule(World world, BlockPos pos, String dir, Block blockToCheckFor, int range, EnumCustomModules module, boolean updateAdjacentBlocks){
		for(int i = 1; i <= range; i++)
			if(dir.equalsIgnoreCase("x+")){
				if(world.getBlockState(pos.east(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) world.getTileEntity(pos.east(i))).hasModule(module)){
					((CustomizableSCTE) world.getTileEntity(pos.east(i))).removeModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndRemoveModule(world, pos.east(i), blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("x-")){
				if(world.getBlockState(pos.west(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) world.getTileEntity(pos.west(i))).hasModule(module)){
					((CustomizableSCTE) world.getTileEntity(pos.west(i))).removeModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndRemoveModule(world, pos.west(i), blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("y+")){
				if(world.getBlockState(pos.up(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) world.getTileEntity(pos.up(i))).hasModule(module)){
					((CustomizableSCTE) world.getTileEntity(pos.up(i))).removeModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndRemoveModule(world, pos.up(i), blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("y-")){
				if(world.getBlockState(pos.down(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) world.getTileEntity(pos.down(i))).hasModule(module)){
					((CustomizableSCTE) world.getTileEntity(pos.down(i))).removeModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndRemoveModule(world, pos.down(i), blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("z+")){
				if(world.getBlockState(pos.south(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) world.getTileEntity(pos.south(i))).hasModule(module)){
					((CustomizableSCTE) world.getTileEntity(pos.south(i))).removeModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndRemoveModule(world, pos.south(i), blockToCheckFor, range, module, updateAdjacentBlocks);
				}
			}else if(dir.equalsIgnoreCase("z-"))
				if(world.getBlockState(pos.north(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) world.getTileEntity(pos.north(i))).hasModule(module)){
					((CustomizableSCTE) world.getTileEntity(pos.north(i))).removeModule(module);
					if(updateAdjacentBlocks)
						checkInAllDirsAndRemoveModule(world, pos.north(i), blockToCheckFor, range, module, updateAdjacentBlocks);
				}
	}

	public static void checkInAllDirsAndRemoveModule(World world, BlockPos pos, Block blockToCheckFor, int range, EnumCustomModules module, boolean updateAdjacentBlocks){
		checkForBlockAndRemoveModule(world, pos, "x+", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndRemoveModule(world, pos, "x-", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndRemoveModule(world, pos, "y+", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndRemoveModule(world, pos, "y-", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndRemoveModule(world, pos, "z+", blockToCheckFor, range, module, updateAdjacentBlocks);
		checkForBlockAndRemoveModule(world, pos, "z-", blockToCheckFor, range, module, updateAdjacentBlocks);
	}

	public static List<String> getPlayersFromModule(World world, BlockPos pos, EnumCustomModules module) {
		List<String> list = new ArrayList<String>();

		CustomizableSCTE te = (CustomizableSCTE) world.getTileEntity(pos);

		if(te.hasModule(module)){
			ItemStack item = te.getModule(module);

			for(int i = 1; i <= 10; i++)
				if(item.getTag() != null && item.getTag().getString("Player" + i) != null && !item.getTag().getString("Player" + i).isEmpty())
					list.add(item.getTag().getString("Player" + i).toLowerCase());
		}

		return list;
	}

	public static boolean checkForModule(World world, BlockPos pos, EntityPlayer player, EnumCustomModules module){
		TileEntity te = world.getTileEntity(pos);

		if(te == null || !(te instanceof CustomizableSCTE))
			return false;

		if(te instanceof TileEntityKeypad){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumCustomModules.WHITELIST).contains(player.getName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("tile.securitycraft:keypad.name"), ClientUtils.localize("messages.securitycraft:module.whitelisted"), TextFormatting.GREEN);
				return true;
			}

			if(module == EnumCustomModules.BLACKLIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.BLACKLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumCustomModules.BLACKLIST).contains(player.getName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("tile.securitycraft:keypad.name"), ClientUtils.localize("messages.securitycraft:module.blacklisted"), TextFormatting.RED);
				return true;
			}
		}else if(te instanceof TileEntityKeycardReader){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumCustomModules.WHITELIST).contains(player.getName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("tile.securitycraft:keycardReader.name"), ClientUtils.localize("messages.securitycraft:module.whitelisted"), TextFormatting.GREEN);
				world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock());
				return true;
			}

			if(module == EnumCustomModules.BLACKLIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.BLACKLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumCustomModules.BLACKLIST).contains(player.getName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("tile.securitycraft:keycardReader.name"), ClientUtils.localize("messages.securitycraft:module.blacklisted"), TextFormatting.RED);
				return true;
			}
		}else if(te instanceof TileEntityRetinalScanner){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumCustomModules.WHITELIST).contains(player.getName().toLowerCase()))
				return true;
		}else if(te instanceof TileEntityInventoryScanner)
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumCustomModules.WHITELIST).contains(player.getName().toLowerCase()))
				return true;

		return false;
	}
}