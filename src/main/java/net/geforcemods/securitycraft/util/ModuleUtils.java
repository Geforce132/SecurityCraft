package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.geforcemods.securitycraft.tileentity.TileEntityRetinalScanner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ModuleUtils{
	public static List<String> getPlayersFromModule(World world, BlockPos pos, EnumCustomModules module) {
		CustomizableSCTE te = (CustomizableSCTE) world.getTileEntity(pos);

		if(te.hasModule(module))
			return getPlayersFromModule(te.getModule(module));
		else return new ArrayList<>();
	}

	public static List<String> getPlayersFromModule(ItemStack stack)
	{
		List<String> list = new ArrayList<>();

		if(stack.getItem() instanceof ItemModule)
		{
			for(int i = 1; i <= ItemModule.MAX_PLAYERS; i++)
			{
				if(stack.getTagCompound() != null && stack.getTagCompound().getString("Player" + i) != null && !stack.getTagCompound().getString("Player" + i).isEmpty())
					list.add(stack.getTagCompound().getString("Player" + i).toLowerCase());
			}
		}

		return list;
	}

	public static boolean checkForModule(World world, BlockPos pos, EntityPlayer player, EnumCustomModules module){
		TileEntity te = world.getTileEntity(pos);

		if(!(te instanceof CustomizableSCTE))
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
				world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), false);
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