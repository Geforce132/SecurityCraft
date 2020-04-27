package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadTileEntity;
import net.geforcemods.securitycraft.tileentity.RetinalScannerTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ModuleUtils{
	public static List<String> getPlayersFromModule(World world, BlockPos pos, CustomModules module) {
		IModuleInventory te = (IModuleInventory) world.getTileEntity(pos);

		if(te.hasModule(module))
			return getPlayersFromModule(te.getModule(module));
		else return new ArrayList<>();
	}

	public static List<String> getPlayersFromModule(ItemStack stack)
	{
		List<String> list = new ArrayList<>();

		if(stack.getItem() instanceof ModuleItem)
		{
			for(int i = 1; i <= ModuleItem.MAX_PLAYERS; i++)
			{
				if(stack.getTag() != null && stack.getTag().getString("Player" + i) != null && !stack.getTag().getString("Player" + i).isEmpty())
					list.add(stack.getTag().getString("Player" + i).toLowerCase());
			}
		}

		return list;
	}

	public static boolean checkForModule(World world, BlockPos pos, PlayerEntity player, CustomModules module){
		TileEntity te = world.getTileEntity(pos);

		if(!(te instanceof IModuleInventory))
			return false;

		if(te instanceof KeypadTileEntity){
			if(module == CustomModules.WHITELIST && ((IModuleInventory) te).hasModule(CustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, CustomModules.WHITELIST).contains(player.getName().getFormattedText().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYPAD.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.whitelisted"), TextFormatting.GREEN);
				return true;
			}

			if(module == CustomModules.BLACKLIST && ((IModuleInventory) te).hasModule(CustomModules.BLACKLIST) && ModuleUtils.getPlayersFromModule(world, pos, CustomModules.BLACKLIST).contains(player.getName().getFormattedText().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYPAD.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.blacklisted"), TextFormatting.RED);
				return true;
			}
		}else if(te instanceof KeycardReaderTileEntity){
			if(module == CustomModules.WHITELIST && ((IModuleInventory) te).hasModule(CustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, CustomModules.WHITELIST).contains(player.getName().getFormattedText().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYCARD_READER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.whitelisted"), TextFormatting.GREEN);
				world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock());
				return true;
			}

			if(module == CustomModules.BLACKLIST && ((IModuleInventory) te).hasModule(CustomModules.BLACKLIST) && ModuleUtils.getPlayersFromModule(world, pos, CustomModules.BLACKLIST).contains(player.getName().getFormattedText().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYCARD_READER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.blacklisted"), TextFormatting.RED);
				return true;
			}
		}else if(te instanceof RetinalScannerTileEntity){
			if(module == CustomModules.WHITELIST && ((IModuleInventory) te).hasModule(CustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, CustomModules.WHITELIST).contains(player.getName().getFormattedText().toLowerCase()))
				return true;
		}else if(te instanceof InventoryScannerTileEntity)
			if(module == CustomModules.WHITELIST && ((IModuleInventory) te).hasModule(CustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, CustomModules.WHITELIST).contains(player.getName().getFormattedText().toLowerCase()))
				return true;

		return false;
	}
}